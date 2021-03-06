#include "types.h"
#include "defs.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "x86.h"
#include "proc.h"
#include "spinlock.h"
#include "stddef.h"
#include "pstat.h"

struct {
  struct spinlock lock;
  struct proc proc[NPROC];
} ptable;

/* array of struct proc ( 3 X 64) */
struct proc *mlfq[3][NPROC];        
/* header of each array(level 0,1,2)*/
int head[3] = {0,0,0};
          
double minpass = 0;
double sumOfShare = 0;                         //sum of cpu_share
double strideSum = 0;                          //sum of stride
int index = 0;
int check = 0;                             //check = 1 if using mlfq scheduler

int nextpid = 1;
extern void forkret(void);
extern void trapret(void);
static void wakeup1(void *chan);
static struct proc *initproc;

void
pinit(void)
{
  initlock(&ptable.lock, "ptable");
}

//PAGEBREAK: 32
// Look in the process table for an UNUSED proc.
// If found, change state to EMBRYO and initialize
// state required to run in: the kernel.
// Otherwise return 0.
static struct proc*
allocproc(void)
{
  int i;
  struct proc *p;
  char *sp;

  acquire(&ptable.lock);

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++)
    if(p->state == UNUSED)
      goto found;

  release(&ptable.lock);
  return 0;

found:
  p->state = EMBRYO;
  p->pid = nextpid++;

  /* insert process into PQueue*/
  release(&ptable.lock);

  // Allocate kernel stack.
  if((p->kstack = kalloc()) == 0){
    p->state = UNUSED;
    return 0;
  }
  sp = p->kstack + KSTACKSIZE;

  // Leave room for trap frame.
  sp -= sizeof *p->tf;
  p->tf = (struct trapframe*)sp;

  // Set up new context to start executing at forkret,
  // which returns to trapret.
  sp -= 4;
  *(uint*)sp = (uint)trapret;

  sp -= sizeof *p->context;
  p->context = (struct context*)sp;
  memset(p->context, 0, sizeof *p->context);
  p->context->eip = (uint)forkret;

  /* initialize for mlfq scheduling */
  p->prioritylevel = 0;                    
  p->ticks[0] = 0;
  p->ticks[1] = 0;
  p->ticks[2] = 0;
  p->wasRunInLastBoostCycle = 0;
  p->timeslice[0] = 0;
  p->timeslice[1] = 0;
  p->timeslice[2] = 0;

  /* initialize for stride scheduling */
  p->share = 0;
  p->stride = 0;
  p->pvalue = 0;
  p->isStride = 0;

  /* initialize for LWP */
  p->tid = 0;
  p->isThread = 0;
  p->baseAddr = 0;
  for(i = 0 ; i < 64 ; i++) {
      p->isPageEmpty[i] = 0;
      p->retValArr[i] = 0;
  } 

  return p;
}

//PAGEBREAK: 32
// Set up first user process.
void
userinit(void)
{
  struct proc *p;
  extern char _binary_initcode_start[], _binary_initcode_size[];

    /* initialize strict proc array */
  memset(mlfq , 0 , sizeof(mlfq[0][0])*3*NPROC);
    
    /* initialize default setting as MLFQ scheduling */
  schedMode = MLFQ;
  mlfq_Stride = 0;
  mlfq_PassValue = 0;
  checkingWhetherStrideOrNot = 0;

  p = allocproc();
  acquire(&ptable.lock);
  initproc = p;
  if((p->pgdir = setupkvm()) == 0)
    panic("userinit: out of memory?");
  inituvm(p->pgdir, _binary_initcode_start, (int)_binary_initcode_size);
  p->sz = PGSIZE;
  memset(p->tf, 0, sizeof(*p->tf));
  p->tf->cs = (SEG_UCODE << 3) | DPL_USER;
  p->tf->ds = (SEG_UDATA << 3) | DPL_USER;
  p->tf->es = p->tf->ds;
  p->tf->ss = p->tf->ds;
  p->tf->eflags = FL_IF;
  p->tf->esp = PGSIZE;
  p->tf->eip = 0;  // beginning of initcode.S

  safestrcpy(p->name, "initcode", sizeof(p->name));
  p->cwd = namei("/");

  // this assignment to p->state lets other cores
  // run this process. the acquire forces the above
  // writes to be visible, and the lock is also needed
  // because the assignment might not be atomic.

  p->state = RUNNABLE;

    /*insert proc into PQueue */
  insertIntoPQueue(0,p);

  release(&ptable.lock);
}

// Grow current process's memory by n bytes.
// Return 0 on success, -1 on failure.
int
growproc(int n)
{
  uint sz;
  if(proc->isThread) sz = proc->parent->sz;
  else sz = proc->sz;

  if(proc->isThread) {
    if(n > 0){
        if((sz = allocuvm(proc->pgdir, sz+PGSIZE*64, sz+PGSIZE*64 + n)) == 0)
            return -1;
    } else if(n < 0){
        if((sz = deallocuvm(proc->pgdir, sz+PGSIZE*64, sz+PGSIZE*64 + n)) == 0)
            return -1;
    }
    sz -= PGSIZE*64;
  }
  else if(proc->cntchild == 0) {
    if(n > 0){
        if((sz = allocuvm(proc->pgdir, sz ,  sz + n)) == 0)
            return -1;
    } else if(n < 0){
        if((sz = deallocuvm(proc->pgdir, sz , sz + n)) == 0)
            return -1;
    }
    proc->realsz = sz;
  }
  else {
    if(n > 0){
        if((sz = allocuvm(proc->pgdir, sz+PGSIZE*64, sz+PGSIZE*64 + n)) == 0)
            return -1;
    } else if(n < 0){
        if((sz = deallocuvm(proc->pgdir, sz+PGSIZE*64, sz+PGSIZE*64 + n)) == 0)
            return -1;
    }
    sz -= PGSIZE*64;
  }

  if(proc->isThread)
      proc->parent->sz = sz;
  else
    proc->sz = sz;

  switchuvm(proc);
  return 0;
}

// Create a new process copying p as the parent.
// Sets up stack to return as if from system call.
// Caller must set state of returned proc to RUNNABLE.
int
fork(void)
{
  int i, pid;
  struct proc *np;

  // Allocate process.
  if((np = allocproc()) == 0){
    return -1;
  }

  if(!proc->isThread) {
    // Copy process state from p.
    if((np->pgdir = copyuvm(proc->pgdir, proc->sz)) == 0){
        kfree(np->kstack);
        np->kstack = 0;
        np->state = UNUSED;
        return -1;
    }
  }
  else {
    /* proc is thread */
    /* call modified copyuvm(that is copyuvm2) */
    /* it ignore empty page which is thread's seperate stack */
    if((np->pgdir = copyuvm2(proc->pgdir, proc->sz)) == 0){
        kfree(np->kstack);
        np->kstack = 0;
        np->state = UNUSED;
        return -1;
    }
  }

  np->sz = proc->sz;
  np->realsz = np->sz;
  np->parent = proc;
  *np->tf = *proc->tf;

  // Clear %eax so that fork returns 0 in the child.
  np->tf->eax = 0;

  for(i = 0; i < NOFILE; i++)
    if(proc->ofile[i])
      np->ofile[i] = filedup(proc->ofile[i]);
  np->cwd = idup(proc->cwd);

  safestrcpy(np->name, proc->name, sizeof(proc->name));

  pid = np->pid;

  np->state = RUNNABLE;

  /*insert child process into PQueue when fork() called */
  insertIntoPQueue(np->prioritylevel , np);       

  return pid;
}

// Exit the current process.  Does not return.
// An exited process remains in the zombie state
// until its parent calls wait() to find out it exited.
void
exit(void)
{
  struct proc *p;
  int fd;

  if(proc == initproc)
    panic("init exiting");

  /* if proc is thread */
  if(proc->isThread) {
     for(p = ptable.proc ; p < &ptable.proc[NPROC] ; p++) {
         /* close all open files of thread which parent is same as proc */
         if(p->isThread && (p->parent == proc->parent)) {
            for(fd = 0; fd < NOFILE; fd++){
                if(p->ofile[fd]){
                    fileclose(p->ofile[fd]);
                    p->ofile[fd] = 0;
                }
            }
            begin_op();
            iput(p->cwd);
            end_op();
            p->cwd = 0;
         }
     }
     /* close all open files of proc */
     for(fd = 0; fd < NOFILE; fd++){
        if(proc->parent->ofile[fd]){
            fileclose(proc->parent->ofile[fd]);
            proc->parent->ofile[fd] = 0;
        }
     } 
     begin_op();
     iput(proc->parent->cwd);
     end_op();
     proc->parent->cwd = 0;
  } else {
      /* if proc is master thread or normal process */
      /* close all open files */
    for(fd = 0; fd < NOFILE; fd++){
        if(proc->ofile[fd]){
            fileclose(proc->ofile[fd]);
            proc->ofile[fd] = 0;
        }
    }
    begin_op();
    iput(proc->cwd);
    end_op();
    proc->cwd = 0;

    for(p = ptable.proc ; p < &ptable.proc[NPROC] ; p++) {
        /* close all open files of thread which parent is proc */
         if(p->isThread && (p->parent == proc)) {
            for(fd = 0; fd < NOFILE; fd++){
                if(p->ofile[fd]){
                    fileclose(p->ofile[fd]);
                    p->ofile[fd] = 0;
                }
            }
            begin_op();
            iput(p->cwd);
            end_op();
            p->cwd = 0;
         }
     }
  }

  acquire(&ptable.lock);

  /* if proc is thread , wakeup master thread's parent */
  if(proc->isThread)
    wakeup1(proc->parent->parent);
  /* else , wakeup proc's parent */
  else
    wakeup1(proc->parent);

  /* add orphan identifier and change state to ZOMBIE */
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      /* if proc is thread */
      if(proc->isThread) {
          /* other thread which parent is same as proc */
        if(p->isThread && (p->parent == proc->parent)){
            p->orphan = 1;
            p->state = ZOMBIE;
            p->parent->cntchild--;
        }
    }
      /* if proc is master thread */
    if(p->parent == proc) {
        /* all of master thread's child thread */
        if(p->isThread) {
            p->orphan = 1;
            p->state = ZOMBIE;
            p->parent->cntchild--;
        } else {
            p->parent = initproc;
            if(p->state == ZOMBIE)
                wakeup1(initproc);
        }
    }
  }
  
  /* master thread's state to ZOMBIE & delete from priority queue */
  if(proc->isThread) {
      proc->parent->state = ZOMBIE;
      deleteFromQueue(proc->parent->prioritylevel,proc->parent);
  }

  /* thread's state to ZOMBIE & delete from priority queue */
  proc->state = ZOMBIE;
  deleteFromQueue(proc->prioritylevel,proc);
                
  sched();
  panic("zombie exit");
}

// Wait for a child process to exit and return its pid.
// Return -1 if this process has no children.
int
wait(void)
{
  struct proc *p;
  int havekids, pid;

  acquire(&ptable.lock);
  for(;;){
    for(p = ptable.proc ; p < &ptable.proc[NPROC] ; p++) {
        /* if p is orphan && state ZOMBIE && thread */
        /* clear all resources */
        if(p->orphan && p->isThread && p->state == ZOMBIE) {
            deallocuvm(p->pgdir , p->baseAddr , p->baseAddr -PGSIZE);
            kfree(p->kstack);
            p->kstack = 0;
            p->pid = 0;
            p->name[0] = 0;
            p->killed = 0;

            p->isThread = 0;
            p->parent->isPageEmpty[p->tid] = 0;

            p->parent = 0;

            sumOfShare -= p->share;
            p->stride = 0;
            p->share = 0;
            p->pvalue = 0;
       
            p->state = UNUSED;
        }
    }
   
    havekids = 0;
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->parent != proc)
        continue;
      havekids = 1;
      /* Only if process is not LWP */
      if(p->state == ZOMBIE && (p->isThread == 0)){
        // Found one.
        pid = p->pid;
        kfree(p->kstack);
        p->kstack = 0;
        freevm(p->pgdir);
        p->pid = 0;
        p->parent = 0;
        p->name[0] = 0;
        p->killed = 0;
      
        
        sumOfShare -= p->share;
        p->stride = 0;
        p->share = 0;
        p->pvalue = 0;
       

        p->state = UNUSED;
        release(&ptable.lock);
        return pid;
      }
    }

    // No point waiting if we don't have any children.
    if(!havekids || proc->killed){
      release(&ptable.lock);
      return -1;
    }

    // Wait for children to exit.  (See wakeup1 call in proc_exit.)
    sleep(proc, &ptable.lock);  //DOC: wait-sleep
  }
}

//PAGEBREAK: 42
// Per-CPU process scheduler.
// Each CPU calls scheduler() after setting itself up.
// Scheduler never returns.  It loops, doing:
//  - choose a process to run
//  - swtch to start running that process
//  - eventually that process transfers control
//      via swtch back to the scheduler.

/* return Highest priority process at same level */
struct proc *getHighPriorityProcForLevel(int plevel) { 
    struct proc *p;
    int i = 0;
    /* at level 0,1 */
    if(plevel < 2) {                    
        for(; i < NPROC ; i++) {
            p = mlfq[plevel][i];
            if(p != NULL) {
                if(p->state == RUNNABLE) {
                    if(p->isStride == 1) {
                        continue;
                    }else {
                        check = 1;
                    }
                    p->wasRunInLastBoostCycle = 1;
                    return p;
                }
            }
        }
    }
    /* at level 2 */
    else {                              
        i = 0;
        for(; i < NPROC ; i++) {
            p = mlfq[plevel][i];
            if(p != NULL) {
                if(p->state == RUNNABLE) {
                    if(p->isStride == 1) {
                        continue;
                    }else {
                        check = 1;
                    }
                    if(p->timeslice[2] == 20) {
                        p->ticks[2] = 0;
                        insertIntoPQueue(2,p);
                        deleteHeadFromPQueue(2);
                        p->timeslice[2] = 0;
                    }else {
                        p->wasRunInLastBoostCycle = 1;
                        return p;
                    }
                }
            }
            head[plevel] = (head[plevel]+1)%NPROC;
        }
    }
    return NULL;
}

/* return Highest priority process */
struct proc * getHighestPriorityProc() {       
    int plevel = 0;
    check = 0;
    struct proc *p;
    for(; plevel < 3 ; plevel++){ 
        p = getHighPriorityProcForLevel(plevel);
        if(p != NULL) {
            return p;
        }
    }
    return NULL;
}

/* delete process in PQueue */
int deleteFromQueue(int plevel , struct proc *toDelete) {
    int i = 0;
    struct proc *p;
    for(; i < NPROC ; i++) {
        p = mlfq[plevel][i];
        if(p != NULL && p->pid == toDelete->pid && p == toDelete) {
            mlfq[plevel][i] = NULL;
            return i;
        }
    }
    return -1;
}

/* delete head of PQueue */
void deleteHeadFromPQueue(int plevel) {            
    mlfq[plevel][head[plevel]] = NULL;
    head[plevel] = (head[plevel]+1)%NPROC;
}

/* reset Ticks */
void resetTicks(struct proc *p) {          
    p->timeslice[0] = 0;
    p->timeslice[1] = 0;
    p->timeslice[2] = 0;
}

/* priority boost */
void boostPriority(void) {                    
    int i;
    struct proc *p = ptable.proc;
    int num = 0;
    for(i=0; i < NPROC ; i++) {
        if(p != NULL && p->state == RUNNABLE && p->wasRunInLastBoostCycle) {
            if(p->prioritylevel > 0) {
                deleteFromQueue(p->prioritylevel , p);
                p->prioritylevel = 0;
                insertIntoPQueue(p->prioritylevel , p);
            }
            resetTicks(p);
            num++;
        }else {
            p->wasRunInLastBoostCycle = 0;
        }
        p++;
    }
}

/* insert process into PQueue */
int insertIntoPQueue(int plevel , struct proc *p) {     
    int i = 0;
    for(; i < NPROC ; i++) {
        if(mlfq[plevel][i] == NULL) {
            mlfq[plevel][i] = p;
            return i;
        }
    }
    if(i == NPROC) {                // struct proc array is full!
        cprintf("Error: 65th Active process: %s\n" , p->name);
        return -1;
    }
    return i;
}

/* print proc table */
void printProcTable(uint ticksCountLocal) {
    int i = 0;
    struct proc *p = mlfq[0][0];
    int plevel = 0;
    for(; plevel < 3 ; plevel++) {
        for(i = 0 ; i < NPROC ; i++) {
            p = mlfq[plevel][i];
            if(p != NULL && p->state != UNUSED && p->pid > 2) {
                cprintf("%d\t %d\t %d\t ticks:%d,%d,%d\n" , ticksCountLocal , p->pid , p->prioritylevel , p->ticks[0] , p->ticks[1] , p->ticks[2]);
            }
        }
    }
}

void
scheduler(void)
{

    struct proc *p;
    /* get current ticks */
    uint prevTicksCount = getTicksCount();
    for(;;){
        // Enable interrupts on this processor.
        sti();
        acquire(&ptable.lock);
        /* scheduling mode is STRIDE */
        if(schedMode == STRIDE) {
            // Loop over process table looking for process to run.
            minpass = 10000000;
            sumOfShare = 0;
            strideSum = 0;
            index = 0;
            int i = 0;
            /* control ticksCount's increase when stride scheduling */
            checkingWhetherStrideOrNot = 1; 

            for(i = 0 ; i < NPROC ; i++) {
                if(ptable.proc[i].state != RUNNABLE)
                    continue;
                /* choose process which state is RUNNABLE && call cpu_share */
                if(ptable.proc[i].isStride == 1) { 
                    p = &ptable.proc[i];
                    /* minimum value of pass value */
                    if(p->pvalue < minpass) {      
                        minpass = p->pvalue;
                        index = i;
                    }
                    strideSum += p->stride;
                    if(p->stride != 0) {   
                        sumOfShare += ((double)STRIDENUM/p->stride);
                    }
                }
            }
            /* there is no process which call cpu_share */
            if(strideSum == 0) {         
                mlfq_Stride = 0;
                mlfq_PassValue = 0;
                schedMode = MLFQ;
                goto mlfq;
            }
           
            if(minpass == 10000000)
                minpass = 0;

            if(mlfq_PassValue < minpass) {
                mlfq_Stride = (double)STRIDENUM/(100-sumOfShare);
               /* mlfq array has no process(all process has cpu_share system call) */
               if(check == 0) {                   
                    checkingWhetherStrideOrNot = 1;
                    release(&ptable.lock); 
                    mlfq_PassValue += mlfq_Stride;
                    continue;
               }else{
                    /* goto MLFQ loop */
                    goto mlfq;         
                }
            /* context switch as stride scheduling */
            }else {                    
                p = &ptable.proc[index];
                proc = p;
                switchuvm(p);
                p->state = RUNNING;
                swtch(&cpu->scheduler , p->context);
                switchkvm();
                    
                p->pvalue += p->stride;
                proc = 0;
            }
        }
        /* scheduling mode is MLFQ */
        if(schedMode == MLFQ) {
            mlfq:

            check = 0;
            checkingWhetherStrideOrNot = 0;
            /* get current ticks */
            uint ticksCountLocal = getTicksCount();
            
            if(ticksCountLocal % 1 == 0 && ticksCountLocal > prevTicksCount) {
                prevTicksCount = ticksCountLocal;
            }
            /* get highest priority process */
            p = getHighestPriorityProc();

            if(p == NULL) {
                release(&ptable.lock);
                continue;
            }
            if (p->isStride == 1){
                release(&ptable.lock);
                continue;
            }
            // Switch to chosen process.  It is the process's job
            // to release ptable.lock and then reacquire it
            // before jumping back to us.
            proc = p;
            switchuvm(p);
            p->state = RUNNING;
            swtch(&cpu->scheduler, p->context);
            switchkvm();
            // Process is done running for now.
            // It should have changed its p->state before coming back.
            proc = 0;
            
            /* increase mlfq pass value */
            mlfq_PassValue += mlfq_Stride;
        }
        release(&ptable.lock);  
    }
}

// Enter scheduler.  Must hold only ptable.lock
// and have changed proc->state. Saves and restores
// intena because intena is a property of this
// kernel thread, not this CPU. It should
// be proc->intena and proc->ncli, but that would
// break in the few places where a lock is held but
// there's no process.
void
sched(void)
{
  int intena;

  if(!holding(&ptable.lock))
    panic("sched ptable.lock");
  if(cpu->ncli != 1)
    panic("sched locks");
  if(proc->state == RUNNING)
    panic("sched running");
  if(readeflags()&FL_IF)
    panic("sched interruptible");
  intena = cpu->intena;
  swtch(&proc->context, cpu->scheduler);
  cpu->intena = intena;
}

/* handle priority according to ticks */
void handlePriorityLevel(void) {
    proc->ticks[proc->prioritylevel]++;
    proc->timeslice[proc->prioritylevel]++;

    /* call boostpriority() */
    if(ticksCount == 100) {           
        boostPriority();
        ticksCount = 0;
    }

    /* demote */
    switch(proc->prioritylevel) {        
        case 0:
            if(proc->timeslice[proc->prioritylevel] == 5) {
                deleteFromQueue(proc->prioritylevel , proc);
                proc->prioritylevel++;
                proc->timeslice[proc->prioritylevel] = 0;
                insertIntoPQueue(proc->prioritylevel , proc);
            }break;
        case 1:
            if(proc->timeslice[proc->prioritylevel] == 10) {
                deleteFromQueue(proc->prioritylevel , proc);
                proc->prioritylevel++;
                proc->timeslice[proc->prioritylevel] = 0;
                insertIntoPQueue(proc->prioritylevel , proc);
            }
            break;
        default:
            /* at level 2 , RR scheduling until priority boost */
            if(proc->timeslice[proc->prioritylevel] == 20) {
                proc->timeslice[2] = 0;
                head[2] = (head[2]+1)%NPROC;
            }
            break;
    }
}
               
// Give up the CPU for one scheduling round.
void
yield(void)
{
  acquire(&ptable.lock);  //DOC: yieldlock
  proc->state = RUNNABLE;
  sched();
  release(&ptable.lock);
}

/* cpu_share function */
int
set_cpu_share(int share)
{   
    int i = 0;
    double totalShare = 0;
    mlfq_PassValue = 0;

    proc->fixedshare = share;
   
    
    for(i = 0 ; i < NPROC ; i++) {
        if(ptable.proc[i].isStride) {
            ptable.proc[i].pvalue = 0;
            totalShare += ptable.proc[i].share;
        }
    }
    if(totalShare + share > 80) {
        /* when CPU share failed */
        cprintf("total CPU share exceed!(%d+%d > 80%)\n" , totalShare , share);
        return -1;
    }else {
        /* when CPU share successed */
        proc->share = share;
        if(proc->share != 0) {
            proc->stride = STRIDENUM/proc->share;
        }
        proc->pvalue = 0;
        proc->isStride = 1;
    }
    schedMode = STRIDE;
    return 0;
}

/* thread_create function */
int
thread_create(thread_t *thread , void *(*start_routine)(void *) , void *arg)
{
  int i;
  struct proc *np;
  char *sp;                 /* stack pointer */
  uint sz , ba;
  struct proc *p;
  p = 0;

  /* Allocate process(same as fork()) */
  if((np = allocproc()) == 0){
    return -1;
  }

  np->sz = proc->sz;
  np->parent = proc;
  *np->tf = *proc->tf;
  np->pid = proc->pid;
  
  /* refers to parent's pgdir */
  np->pgdir = proc->pgdir;
  
  /* check whether space of child's stack */
  /* if specific space is empty , child's tid is index of array(0 to 63) */
  /* and isPageEmpty = 1 */
  for(i = 0 ; i < 64 ; i++) {
      if(proc->isPageEmpty[i] == 0) {
        np->tid = i;
        proc->isPageEmpty[i] = 1;
        break;
      }
  }

  /* base address of child's stack */
  /* offset is PGSIZE */
  /* and call allocuvm() */
  ba = proc->realsz + (np->tid)*PGSIZE;
  if((sz = allocuvm(np->pgdir , ba , ba + PGSIZE)) == 0)
      return -1;

  np->baseAddr = ba + PGSIZE;   /* store bass address */
  np->sz = sz;
  sp = (char*)sz;

  sp -= 4;
  *(uint*)sp = (uint)arg;       /* store arg */
  sp -= 4;
  *(uint*)sp = 0xffffffff;      /* store return value */
  
  *thread = np->tid;

  np->tf->esp =(uint)sp;                   /* set esp */
  np->tf->eip = (uint)start_routine;       /* eip point start_routine */
  np->tf->eax = 0;

  for(i = 0; i < NOFILE; i++)
    if(proc->ofile[i])
      np->ofile[i] = filedup(proc->ofile[i]);
  np->cwd = idup(proc->cwd);
 
  np->isThread = 1;                         /* because np is LWP */
  proc->cntchild++;

  acquire(&ptable.lock);
  if(proc->isStride) {
      for(p = ptable.proc ; p < &ptable.proc[NPROC] ; p++) {
          if(p->pid == proc->pid && p->isThread) {
            p->share = proc->fixedshare/(double)(proc->cntchild+1);
            proc->share = p->share;

            p->isStride = 1;
            p->pvalue = 0;
            p->stride = STRIDENUM/p->share;
          }
      }
  }
  release(&ptable.lock);
  
  acquire(&ptable.lock);
  np->state = RUNNABLE;
  release(&ptable.lock);
  
  /*insert child process into PQueue when fork() called */
  insertIntoPQueue(np->prioritylevel , np);
  return 0;
}

int
thread_join(thread_t thread , void** retval)
{
 // cprintf("!111\n"); 
  struct proc *p;
  int havekids;

  acquire(&ptable.lock);
  for(;;){
    // Scan through table looking for exited children.
    havekids = 0;
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->parent != proc || p->tid != thread)         /* parent and tid check */
        continue;
      havekids = 1;
      if(p->state == ZOMBIE && p->isThread == 1){       /* p must be LWP */
        *retval = p->parent->retValArr[thread];         /* load address of p's return value */
        // Found one.
        deallocuvm(p->pgdir , p->baseAddr , p->baseAddr - PGSIZE);  /* call deallocuvm() using bass address */
        kfree(p->kstack);
        p->kstack = 0;
        p->pid = 0;
        p->parent = 0;
        p->name[0] = 0;
        p->killed = 0;
      
        /* reset isThread , isPageEmpty */
        p->isThread = 0;
        proc->isPageEmpty[thread] = 0;

        /* same as wait() system call */
        p->stride = 0;
        p->share = 0;
        p->pvalue = 0;
       

        p->state = UNUSED;
        release(&ptable.lock);
   //     cprintf("join happend\n");
        return 0;
      }
    }

    // No point waiting if we don't have any children.
    if(!havekids || proc->killed){
      release(&ptable.lock);
      return -1;
    }

    // Wait for children to exit.  (See wakeup1 call in proc_exit.)
    sleep(proc, &ptable.lock);  //DOC: wait-sleep
  }
 
   return 0;
  
}

void
thread_exit(void* retval)
{
  int fd;

  if(proc == initproc)
    panic("init exiting");

  // Close all open files.
  for(fd = 0; fd < NOFILE; fd++){
    if(proc->ofile[fd]){
      fileclose(proc->ofile[fd]);
      proc->ofile[fd] = 0;
    }
  }

  begin_op();
  iput(proc->cwd);
  end_op();
  proc->cwd = 0;

  acquire(&ptable.lock);

  if(proc->isStride) {
      proc->parent->share += proc->share;
      sumOfShare -= proc->share;
      proc->share = 0;
  }
 // cprintf("exit tid : %d, cpu : %d\n",proc->tid,(int)proc->share);

  // Parent might be sleeping in wait().
  wakeup1(proc->parent);
  
  /* save return value of child LWP */
  proc->parent->retValArr[proc->tid] = retval;
  // Jump into the scheduler, never to return.
  proc->state = ZOMBIE;
    
  /* delete proc from PQueue */
  deleteFromQueue(proc->prioritylevel,proc);
  
  sched();
  panic("zombie exit");

}



// A fork child's very first scheduling by scheduler()
// will swtch here.  "Return" to user space.
void
forkret(void)
{
  static int first = 1;
  // Still holding ptable.lock from scheduler.
  release(&ptable.lock);

  if (first) {
    // Some initialization functions must be run in the context
    // of a regular process (e.g., they call sleep), and thus cannot
    // be run from main().
    first = 0;
    iinit(ROOTDEV);
    initlog(ROOTDEV);
  }

  // Return to "caller", actually trapret (see allocproc).
}

// Atomically release lock and sleep on chan.
// Reacquires lock when awakened.
void
sleep(void *chan, struct spinlock *lk)
{
  if(proc == 0)
    panic("sleep");

  if(lk == 0)
    panic("sleep without lk");

  // Must acquire ptable.lock in order to
  // change p->state and then call sched.
  // Once we hold ptable.lock, we can be
  // guaranteed that we won't miss any wakeup
  // (wakeup runs with ptable.lock locked),
  // so it's okay to release lk.
  if(lk != &ptable.lock){  //DOC: sleeplock0
    acquire(&ptable.lock);  //DOC: sleeplock1
    release(lk);
  }

  // Go to sleep.
  proc->chan = chan;
  proc->state = SLEEPING;

    /* delete proc in struct proc array when sleep called! */
  deleteFromQueue(proc->prioritylevel,proc);
                
  sched();

  // Tidy up.
  proc->chan = 0;

  // Reacquire original lock.
  if(lk != &ptable.lock){  //DOC: sleeplock2
    release(&ptable.lock);
    acquire(lk);
  }
}

//PAGEBREAK!
// Wake up all processes sleeping on chan.
// The ptable lock must be held.
static void
wakeup1(void *chan)
{
  struct proc *p;

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++) 
    if(p->state == SLEEPING && p->chan == chan) {
      p->state = RUNNABLE;
      
      p->isStride = 0;
      insertIntoPQueue(p->prioritylevel , p);
    }
}

// Wake up all processes sleeping on chan.
void
wakeup(void *chan)
{
  acquire(&ptable.lock);
  wakeup1(chan);
  release(&ptable.lock);
}

// Kill the process with the given pid.
// Process won't exit until it returns
// to user space (see trap in trap.c).
int
kill(int pid)
{
  struct proc *p;

  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->pid == pid){
      p->killed = 1;
      // Wake process from sleep if necessary.
      if(p->state == SLEEPING){
        p->state = RUNNABLE;
          
        /* insert process into PQueue */
        p->isStride = 0;
        insertIntoPQueue(p->prioritylevel , p);     
      }
      release(&ptable.lock);
      return 0;
    }
  }
  release(&ptable.lock);
  return -1;
}

//PAGEBREAK: 36
// Print a process listing to console.  For debugging.
// Runs when user types ^P on console.
// No lock to avoid wedging a stuck machine further.
void
procdump(void)
{
  static char *states[] = {
  [UNUSED]    "unused",
  [EMBRYO]    "embryo",
  [SLEEPING]  "sleep ",
  [RUNNABLE]  "runble",
  [RUNNING]   "run   ",
  [ZOMBIE]    "zombie"
  };
  int i;
  struct proc *p;
  char *state;
  uint pc[10];

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->state == UNUSED)
      continue;
    if(p->state >= 0 && p->state < NELEM(states) && states[p->state])
      state = states[p->state];
    else
      state = "???";
    cprintf("%d %s %s", p->pid, state, p->name);
    if(p->state == SLEEPING){
      getcallerpcs((uint*)p->context->ebp+2, pc);
      for(i=0; i<10 && pc[i] != 0; i++)
        cprintf(" %p", pc[i]);
    }
    cprintf("\n");
  }
}
/* return process information */
int getprocessinfo(struct pstat *pstats) {
    acquire(&ptable.lock);
    struct proc *p;
    int i = 0;
    for(p = ptable.proc ; p < &ptable.proc[NPROC] ; p++) {
        pstats->pid[i] = p->pid;
        if(p->state == UNUSED) {
            pstats->inuse[i] = 0;
        }else {
            pstats->inuse[i] = 1;
        }

        pstats->priority[i] = p->prioritylevel;
        pstats->state[i] = p->state;

        pstats->ticks[i][0] = p->ticks[0];
        pstats->ticks[i][1] = p->ticks[1];
        pstats->ticks[i][2] = p->ticks[2];
        i++;
    }
    release(&ptable.lock);
    return 0;
}

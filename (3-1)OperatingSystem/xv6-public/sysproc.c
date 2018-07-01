#include "types.h"
#include "x86.h"
#include "defs.h"
#include "date.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "proc.h"
#include "pstat.h"

int
sys_fork(void)
{
  return fork();
}

int
sys_exit(void)
{
  exit();
  return 0;  // not reached
}

int
sys_wait(void)
{
  return wait();
}

int
sys_kill(void)
{
  int pid;

  if(argint(0, &pid) < 0)
    return -1;
  return kill(pid);
}

int
sys_getpid(void)
{
  return proc->pid;
}

int
sys_getlev(void) 
{
  return proc->prioritylevel;    
}

int
sys_getppid(void)
{
    return proc->parent->pid;
}

int
sys_yield(void)
{ 
    yield();
    return 0;
}

int 
sys_set_cpu_share(void)
{
    int share;
    if(argint(0,&share) < 0)
        return -1;
    return set_cpu_share(share);
}

// wrapper function of 'thread_create'
int
sys_thread_create(void)
{
    thread_t* thread;
    void *(*fn)(void*);
    void *arg;
    int i;

    if(argint(0,&i) < 0)
        return -1;
    thread = (thread_t*)i;

    if(argint(1,&i) < 0)
        return -1;
    fn = (void*)i;

    if(argint(2,&i) < 0)
        return -1;
    arg = (void*)i;

    return thread_create(thread , fn , arg);
}

// wrapper function of 'thread_join'
int
sys_thread_join(void)
{
    thread_t thread;
    int i;
    void **retval;

    if(argint(0,&i) < 0)
        return -1;
    thread = i;

    if(argint(1,&i) < 0)
        return -1;
    retval = (void**)i;

    return thread_join(thread , retval);
}

// wrapper function of 'exit'
int
sys_thread_exit(void)
{
    void* retval;
    int i;

    if(argint(0,&i) < 0)
        return -1;
    retval = (void*)i;

    thread_exit(retval);
    return 0;
}

int
sys_sbrk(void)
{
  int n;

  if(argint(0, &n) < 0)
    return -1;
  if(growproc(n) < 0)
    return -1;
  /* if proc is thread , return (proc's parent's size + PGSIZE*64 -n) */
  if(proc->isThread)
      return (proc->parent->sz + PGSIZE*64 -n);
  /* if proc is master thread. but has no child ,return (proc->sz -n) */
  else if(proc->cntchild == 0) return (proc->sz - n);
  /* if proc is master thread. return (proc->sz + PGSIZE*64 -n) */
  else return (proc->sz + PGSIZE*64 -n);
}

int
sys_sleep(void)
{
  int n;
  uint ticks0;

  if(argint(0, &n) < 0)
    return -1;
  acquire(&tickslock);
  ticks0 = ticks;
  while(ticks - ticks0 < n){
    if(proc->killed){
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  return 0;
}

// return how many clock tick interrupts have occurred
// since start.
int
sys_uptime(void)
{
  uint xticks;

  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}

int
sys_getpinfo(void) {
    struct pstat *pstat;
    if(argptr(0, (void*)&pstat , sizeof(*pstat)) < 0) {
        return -1;
    }
    return getprocessinfo(pstat);
}

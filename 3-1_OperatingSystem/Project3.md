# **Project_3 : Light Weight Process**


## <1> Goal
--------
####  I'll improve the multitasking capabilities of xv6 by implementing an abstraction of LWP.

 - A LWP is a process that shares resources such as address space with other LWPs.

 - This allows multitasking to be done at the user level.

 - LWP shares only their address space. It must be seperate stack for independent execution.

---

## <2> Default option
--------

 - Using proc structure to implement LWP. (some value of attribute must be added)

 - Sharing address space means sharing same page table among the LWPs spawned by
a process.

 - Refering to fork, exec, exit and wait system call in xv6.

----

## <3> Basic LWP operation
--------

### <3-1>settings
- Type of thread_t is unsigned int(uint)
- In proc structure , add following
 - int tid (LWP ID)
 - int[64] isThread ( checking whether a process is normal process or LWP) 
 - int[64] isPageEmpty (checking whether a space for one LWP's seperate stack is empty)
 - void &retValAddr (store address of child LWP's return value)
 - uint bassAddr (bass address of seperate stack for LWP)

----

### <3-2> thread_create(thread_t* , void* , void*) function
- flow
 - Allocate process(call allocproc() which is same as fork() system call)
 - child's pgdir refers to parent's pgdir
 - check parent's 'isPageEmpty' array. If find zero , tid is index of array.(this prevent external fragment)
 - so , 'proc->sz + (np->tid)*PGSIZE' is bass address of child's stack.
 - store this base address in parent's 'retValAddr' array(index is same as tid)
 - Allocate stack(call allocuvm(). offset is PGSIZE)
 - store 'return value' and 'arg' in new child's stack using stack pointer
 - set esp register to starting point , eip register to start_routine
 - set child->isThread to 1
 - set child's state to RUNNABLE
 - insert child to scheduling queue
 - return 0

----

### <3-3> thread_join(thread_t , void**) function
- flow(almost same as wait() system call)
 - scan through proc table , find all process which state is ZOMBIE.(the process is must be LWP and process's parent and tid must be checked)
 - because upper line , a checking(process must be normal process(not LWP)) must be added in wait() system call
 - load address of return value from retValAddr array
 - free child's kernel stack and seperate stack(call deallocuvm())
 - reset attribute of child(same as wait()) to 0
 - reset 'isThread' and 'isPageEmpty' array to 0
 - set child's state to UNUSED
 - no point waiting if we don't hava any children
 - sleep parent and return(wait for children to exit)
 - return 0

----

### <3-4> thread_exit(void*) function
- flow(almost same as exit() system call)
 - if process is initproc , panic
 - if not , close all open file
 - wake up parent process
 - save return value of child LWP(in parent's 'retValArr' array(index is child's tid)
 - jump into the scheduler , never to return
 - delete process from priority queue
 - call sched() function
 - no return(bacause attribute is noreturn)

----


## <4>Interaction with system calls
----

### <4-1>settings
- I will call the parent of the thread , master thread.
- In proc structure , add following
 - int orphan (if thread has no parent(master thread) , this variable is updated to 1) 
 - int realsz ( )
 - int cntchild (number of master thread's child thread)

----

### <4-2> exit() 
 - There are two ways in which a thread interacts with an exit syscall.
 - first , master-thread call exit(). (all child-thread must be terminated)
  - close all open files of self
  - close all open files of all thread which is child-thread of master-thread(searching ptable is needed)
  - wakeup master-thread's parent
  - change all child-thread's state to ZOMBIE , update 'orphan' to 1 , subtract 1 from master-thread's 'cntchild' at each case.(searching ptable is needed)
  - change master-thread's state to ZOMBIE. and delete from scheduling queue
 - Second , child-thread call exit(). (all child-thread which has same parent must be terminated)
  - close all open files of self
  - close all open files of all thread which parent is same as caller(searching ptable is needed)
  - wakeup caller->parent->parent. this is same as first case
  - change all thread(which parent is same as caller)'s state to ZOMBIE , update 'orphan' to 1 , subtract 1 from master-thread's cntchild at each case.(searching ptable is needed) 
  - change master-thread's state to ZOMBIE. and delete from scheduling queue
 - In both cases , cleaning up resources was executed in wait() syscall same as normal LWP's case. But 
checking whether thread's state is ZOMBIE and is orphan or not must be added

----

### <4-3> fork()
 - when thread call fork() , normal process is created.
  - but created process copy caller's pgdir , modified copyuvm() function is needed.(named copyuvm2().)
  - If there is an empty part of the copied area,  it should be ignored(in copyuvm2() function)
  - it is because , when assigning a child thread's stack, it allocates a free page of additional 64 pages.(it is specified in thread_create() function)

----

### <4-4> exec()
 - in exec() syscall , old pgdir was removed.
 - but when thread call exec(), old pgdir must be conserved and only caller's stack page is exchanged.
  - so , in the freevm() part, add a condition that is not a thread.

----

### <4-5> sbrk()
 - when thread call sbrk() , new memory must be allocated under thread's seperated stack
 - even though process has no child-process, size of 'PGSIZE*64' must be conserved under process's memory space 
 - moreover , when sbrk() is called , variable 'proc->sz' was changed. so one more variable 'realsz' is need to pointing end of process's pgdir or start of thread's pgdir

----

### <4-6>  kill()
 - when kill() is called , all process and thread which has same pid will be ended.
 - process will wake up if it was sleeping , and other threads will wait for master-thread to finish them
 
----

### <4-7> pipe()
 - pipe are shared to all thread which has same parent
 - these threads share data which will synchornized

----

### <4-8> sleep()
 - if thread call sleep() syscall , its state updated to SLEEPING (same as normal process)

----

## <5>Interaction with schedulers
----

### <5-1> MLFQ scheduler
 -  If thread does not call set_cpu_share() function, it is scheduled in MLFQ scheduling.

### <5-2> STRIDE scheduler
 - if master-thread call set_cpu_share() which has several child-thread , given cpu_share will be divided into (cntchild+1)
 - then master-thread and all child-thread has same cpu_share and executed in STRIDE mode
 - and if master-thread call set_cpu_share() which has nochild-thread , given cpu_share will be divided into (cntchild+1) at every time when thread_create() function called
 - when thread call thread_exit() , cpu_share will be re-calculated

----

 

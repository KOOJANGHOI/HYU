# Project_2 : Scheduling(MLFQ & STRIDE)




## <1> Goal
----

**I'll make a balanced scheduling scheme using both the advantages of MLFQ and Stride scheduling.**
* Advantages of MLFQ scheduling : It is sensitive to changes in situations where high priority or emergency system calls are called.
* Advantages of Stride scheduling  : Processes are guaranteed a certain percentage of the CPU time.

---

## <2> Default option
----

**There are two modes of scheduling mode .MLFQ mode and STRIDE mode. the default scheduling mode is MLFQ.**
* But , when SYSCALL(cpu_share) is called , It switches into STRIDE mode. 
* In STRIDE mode , the process calling the cpu_share is handled in a stride scheduling. The remaining quotas of cpu are allocated MLFQ queue (But, the mode does not change.))
*If there is no process calling cpu_share in MLFQ queue, Scheduling mode return to MLFQ mode.


----

## <3> Detail Option of MLFQ
----

* 3-level queue(L2,L1,L0). Each level of queue adopt Round Robin policy with t, 2t , 4t time quantum respectively(The lower priority , the larger the quantum).
* The first queue's quantum should be large enough for most I/O-oriented operation continue to operate until an I/O  request is generated(so , the initial value is designed to be 5ms).
* When a new process enters the queuing network , it enters L2. it moves to the FIFO form in the queue and occupies the CPU.
* When the process is finished , it leaves the queuing network.
* If an I/O wait or Event wait occurs when the process is working , the process stops working and is put behind the high-level queue.
* If the process can not complete task within the time quota , the process is put behind the low-level queue.
* In the L0 level , the working is complete to the RR method until priority boost begin.

----

## <4> Detail Option of Stride Scheduling
----

* A large number is 10000
* When STRIDE mode is started, each process calling cpu_share has (10000/share) stride.
* Now the stride scheduling is started and the pass value is stored separately. (MLFQ's stride is (10000/remaining cpu_share))
* If another SYS_CALL(cpu_share) is called during scheduling , after initializing the pass value of remaining processes to 0, the scheduling is continued until the end condition.(This part has not actually been implemented.)
* Assume that CPU utilization is 100%, and if the CPU allocation of processes exceeds 80%, it is readjusted within 80%.(It was implemented under the assumption that it was not over 80 %.In other words, exceptions were made.(Unable to allocate more than 80 %))

----

## <5> Additional details
----

* It is assumed that game the scheduler does not occur.


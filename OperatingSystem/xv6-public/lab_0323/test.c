#include "types.h"
#include "stat.h"
#include "user.h"

int
main(int argc , char *argv[])
{
    printf(1 , "My pid is %d\n" , getpid());
    printf(2 , "My ppid is %d\n" , getppid());
    exit();
}

/*
수정내용
sysproc.c 에 
int getppid(void) {
    return proc->parent->pid;
}
함수 추가 후

$ make | grep sysproc
syscall.h 에 SYS_getppid 추가
syscall.c 에 [SYS_getppid] sys_getppid 추가
user.h 에 int getppid(void) 추가
usys.S 에 SYSCALL(getppid) 추가
test.c 에서 getpid() , getppid() 호출
*/

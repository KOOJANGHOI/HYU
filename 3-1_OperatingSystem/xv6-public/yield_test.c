#include "types.h"
#include "stat.h"
#include "user.h"

int main(void) {
    int pid , i;
    pid = fork();
    for(i = 0 ; i < 100 ; i++) {
        if(pid == -1) {
            printf(1,"fork error\n");
            exit();
        }else if(pid == 0) {
            printf(1,"child(%d)\n" , getlev());
            yield();
           // exit();
        }else {
            printf(1,"parent(%d)\n" , getlev());
            yield();
           // exit();
        }
    }
    return 0;
}

// user_app.c

int main(int argc , char *argv[]) {
    __asm__("int $128");
    return 0;
}

// traps.h
...

#define T_SYSCALL       64
#define T_SYSCALL2      128

...

// trap.c


void tvinit(void) {
    ...

    for(i = 0 ; i < 256 ; i++) {
        SETGATE(idt[i] , 0 , SEG_CODE<<3 , vectors[i] , 0);
        SETGATE(idt[T_SYSCALL] , 1 , SEG_CODE<<3 , vectors[T_SYSCALL] , DPL_USER);  
        SETGATE(idt[T_SYSCALL2] , 1 , SEG_CODE<<3 , vectors[T_SYSCALL2] , DPL_USER);
    ...  
}

void trap(void) {
    ...

    if(tf->trapno == T_SYSCALL2) {
        if(proc->killed)
            exit();
        proc->tf - tf;
        cprintf("user interrupt %d called!\n" , tf->trapno);
        exit();
        if(proc->killed)
            exit();
    }
    ...
}






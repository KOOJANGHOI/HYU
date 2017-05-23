#ifndef _PSTAT_H_
#define _PSTAT_H_
#include "param.h"

struct pstat {
    int inuse[NPROC];
    int pid[NPROC];
    int priority[NPROC];
    enum procstate state[NPROC];
    int ticks[NPROC][3];//
};

#endif

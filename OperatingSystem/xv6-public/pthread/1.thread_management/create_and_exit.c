#include <pthread.h>
#include <stdio.h>
#define NUM_THREADS    5

void *PrintHello(void *threadid) {
    long tid;
    tid = (long)threadid; //type casting because void pointer
    printf("Hello World! It's me, thread #%ld!\n", tid);
    pthread_exit(NULL); //return value is NULL
}

int main (int argc, char *argv[]) {
    pthread_t threads[NUM_THREADS];
    int rc;
    long t;
    for(t = 0; t < NUM_THREADS; t++) {
        rc = pthread_create(&threads[t], NULL, PrintHello, (void *)t); //stack을 따로 쓰기때문에 arg를 따로준다.
        printf("In main: creating thread %ld thread id: %ld\n", t, threads[t]);
        if (rc) {
            printf("ERROR; return code from pthread_create() is %d\n", rc);
            exit(-1);
        }
    }
 
    pthread_exit(NULL);
}

#include <stdio.h>
#include <pthread.h>

#define NUM_THREAD      10
#define NUM_INCREASE    1000000

int cnt_global = 0;
pthread_mutex_t cnt;

void *ThreadFunc(void *arg) {
    int i;
    long cnt_local = 0;

    for (i = 0; i < NUM_INCREASE; i++) {
        cnt_local++;
        cnt_global++;
    }

    return (void *)cnt_local; // == pthread_exit((void *) cnt_local);
}

int main(int argc, const char *argv[])
{
    int i , rc;
    pthread_t threads[NUM_THREAD];

    for (i = 0; i < NUM_THREAD; i++) {
       rc = pthread_create(&threads[i] , NULL , ThreadFunc , (int*)i);
    }
    
    pthread_mutex_init(&cnt , NULL);
    void* ret;
    for (i = 0; i < NUM_THREAD; i++) {
        pthread_mutex_lock(&cnt);
        rc = pthread_join(threads[i] ,&ret);
        pthread_mutex_unlock(&cnt);
        printf("thread %lu, local count: %ld\n", threads[i], (long)ret);
    }

    printf("global count: %d\n", cnt_global);
    
    return 0;
}

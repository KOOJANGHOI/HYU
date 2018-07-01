#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <string.h>
#include <wait.h>
#define MAXARG 7

int main(int argc , char *argv[]) {
    char buf[256];
    char saves[100][256];
    char *arg[MAXARG];
    char* s;
    static const char delim[]=" \n\t";
    int pid , status;
    int i,j,k;
    FILE *fp;
    if(argc == 1) {
        while(1) {
            memset(buf,0,256);
            s=NULL;
            i=j=k=0;
            printf("prompt> ");
            fgets(buf , 256 , stdin);
            if(!strcmp(buf , "quit\n") || feof(stdin)) {
                break;
            }
            if(buf == NULL) {
                break; 
            }                     
            s = strtok(buf , ";");
            while(s!=NULL){
                strcpy(saves[i++], s);
                s=strtok(NULL,";");
            }
            for(j=0;j<i;j++) {
                k=0;
                memset(arg,0,sizeof(arg));
                s=strtok(saves[j], delim);
                while(s!=NULL) {
                    arg[k++]=s;
                    s=strtok(NULL,delim);
                }
                if((pid = fork()) == -1) {
                    printf("fork failed\n");
                } else if(pid != 0) {
                    pid = wait(&status);
                } else{
                    execvp(arg[0], arg);
                }   
            }
        }
    }else {
        fp = fopen(argv[1] , "r");
        if(fp == NULL) { printf("file open error\n"); }     
        while(!feof(fp)) {
            memset(buf,0,256);
            s=NULL;
            i=j=k=0;
            fgets(buf , 256 , fp);
        
            if(!strcmp(buf , "quit\n") || feof(stdin)) {
                break;
            }
            if(buf == NULL) {
                break; 
            }                  
            s = strtok(buf , ";");
            while(s!=NULL){
                strcpy(saves[i++], s);
                s=strtok(NULL,";");
            }
            for(j=0;j<i;j++) {
                k=0;
                memset(arg,0,sizeof(arg));
                s=strtok(saves[j], delim);
                while(s!=NULL) {
                    arg[k++]=s;
                    s=strtok(NULL,delim);
                }
                if((pid = fork()) == -1) {
                    printf("fork failed\n");
                } else if(pid != 0) {
                    pid = wait(&status);
                } else{
                    execvp(arg[0], arg);
                }   
            }
        }
        fclose(fp);
    }
    exit(0);
}

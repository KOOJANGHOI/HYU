#include <stdio.h>
#include <string.h>

int main(int argc , char *argv[]) {
    char string[] = "ls -alf ; pwd";
    char delim[] = ";";
    char *result = NULL;
    result = strtok(string , delim);
    while(result != NULL) {
      printf("%s\n" , result);
      result = strtok(NULL , delim);
    }
    return 0;
}

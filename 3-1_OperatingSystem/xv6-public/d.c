#include <stdio.h>
#include <string.h>
int main() {
    char *str = " ; pwd";
    char *result = strtok(str , " ;");
    printf("%s\n" , result);
    return 0;
}

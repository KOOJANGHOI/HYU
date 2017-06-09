#include <stdio.h>
#include <stdlib.h>

int main() {
    char *a = ";a";
    if(a[0] == ';') {
        printf("1\n");
    }else {
        printf("2");
    }
    return 0;
}


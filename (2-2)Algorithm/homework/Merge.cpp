//2013011800_±∏¿Â»∏_A 
#include <stdio.h>
#include <stdlib.h>

void Merge (int A[], int TmpArray[ ], int Lpos, int Rpos, int RightEnd)    {
    int i, LeftEnd, NumElements, TmpPos;
    LeftEnd = Rpos - 1;
    TmpPos = Lpos;
    NumElements = ((RightEnd-Lpos)+1);
    while (Lpos <= LeftEnd && Rpos <= RightEnd) {
        if (A[Lpos] >= A[Rpos])
            TmpArray[TmpPos++] = A[Lpos++];
        else
            TmpArray[TmpPos++] = A[Rpos++];
    }
    while (Lpos <= LeftEnd) {
        TmpArray[TmpPos++] = A[Lpos++];
    }
    while (Rpos <= RightEnd) {
        TmpArray[TmpPos++] = A[Rpos++];
    }
    for(i=0; i<NumElements; i++, RightEnd--)
        A[RightEnd] = TmpArray[RightEnd];
}

void MSort (int A[], int TmpArray[ ], int Left, int Right) {
    int Center = 0;
    if (Left < Right) {
        Center = (int)((Left + Right)/2);
        MSort (A, TmpArray, Left, Center);
        MSort (A, TmpArray, Center+1, Right);
        Merge (A,TmpArray, Left, Center+1, Right);    
    }
}

int main() {
    int i;
    int size = 0;    
    scanf("%d\n" , &size);
    int A1[size];
    int A2[size];
    for(i = 0 ; i < size ; i++) {
        A1[i] = 0;
        A2[i] = 0;
    }
    for(i = 0 ; i < size ; i++) {
        scanf("%d" , &A1[i]);
    }
    MSort(A1 , A2 , 0 , size-1);
    for(i = 0 ; i < size ; i++) {
        printf("%d" , A2[i]);
        printf("\n");
    }
    printf("\n");
    getchar();
    rewind(stdin);
    return 0;
}

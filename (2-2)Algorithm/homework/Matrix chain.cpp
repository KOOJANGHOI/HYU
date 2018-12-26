//2013011800_±∏¿Â»∏ 
#include<stdio.h>
#include<stdlib.h>
void printSequence(int **s , int f , int N) {
   int tmp = s[f][N];
   
   if(tmp == f+1){
      if(f+1 == N){
      	printf("%d %d", f, N);      
      }
   	  else{
        printf("%d(", f);
        printSequence(s, tmp, N);
        printf(")");
   	  }
   }else if(tmp == N) {
      printf("(");
   	  printSequence(s, f, N-1);
      printf(")%d", N);      
   }else {
      printf("(");
      printSequence(s, f, tmp-1);
      printf(")(");
      printSequence(s, tmp, N);
      printf(")");
   }
}
int main() {
   int N , i , j , t , k , temp;
   scanf("%d" , &N);
   
   int p[N+1];
   int m[N+1][N+1];   
   int** s = (int**)malloc(sizeof(int*)*(N+1));
   for(i=0;i<N+1;i++){
      s[i] = (int*)malloc(sizeof(int)*(N+1));
   }
   for(i = 0 ; i <= N ; i++) {
      scanf("%d" , &p[i]);
   }
   
   for(i = 0 ; i <= N ; i++) {
      for(j = 0 ; j <= N ; j++) {
         m[i][j] = 0;
      }
   }
   for(i = 0 ; i <= N ; i++) {
      for(j = 0 ; j <= N ; j++) {
         s[i][j] = 0;
      }
   }  
   for(t = 2 ; t <= N ; t++) {
      for(i = 1 ; i <= N-t+1 ; i++) {
         j = i+t-1;
         m[i][j] = 100000000;
         for(k = i+1 ; k <= j ; k++) {
            temp = m[i][k-1] + m[k][j] + p[i-1]*p[k-1]*p[j];
            if(temp < m[i][j]) {
               m[i][j] = temp;
               s[i][j] = k;
            }
         }
      }
   }
   printf("%d\n" , m[1][N]);
   printSequence(s, 1, N); 
   for(i=0;i<N;i++){
   		free(s[i]);
	}
	free(s);
   
   return 0;
} 

//2013012115_±∏¿Â»∏_A 
#include <stdio.h>
int main(){
   int N, e1, e2, x1, x2, i, j;
   
   scanf("%d", &N);
   if(N < 1 || N > 100)return 0;
   scanf("%d %d", &e1, &e2);
   if((e1 < 1 || e1 > 100)||(e2 < 1 || e2 > 100))return 0;
   scanf("%d %d", &x1, &x2);
   if((x1 < 1 || x1 > 100)||(x2 < 1 || x2 > 100))return 0;
   
   int a1[N], a2[N], t1[N-1], t2[N-2];
   
   for (i = 0 ; i < N; i++){
      scanf("%d", &a1[i]);
      if(a1[i] < 1 || a1[i] > 100)return 0;
   }
   
   for (i = 0 ; i < N; i++){
      scanf("%d", &a2[i]);
      if(a2[i] < 1 || a2[i] > 100)return 0;
   }
   
   for (i = 0 ; i < N-1; i++){
      scanf("%d", &t1[i]);
      if(t1[i] < 1 || t1[i] > 100)return 0;
   }
   
   for (i = 0 ; i < N-1; i++){
      scanf("%d", &t2[i]);
      if(t2[i] < 1 || t2[i] > 100)return 0;
   }
   
   int f[2][N], l[2][N], path[N];
   
   for(i=0; i < N; i ++){
      if(i == 0){
         f[0][i]=e1+a1[i];
         f[1][i]=e2+a2[i];
      }
      else{
         if(f[0][i-1]+a1[i]<=f[1][i-1]+t2[i-1]+a1[i]){
            f[0][i]=f[0][i-1]+a1[i];
            l[0][i]=0;
         }
         else{
            f[0][i]=f[1][i-1]+t2[i-1]+a1[i];
            l[0][i]=1;
         }
         if(f[1][i-1]+a2[i]<=f[0][i-1]+t1[i-1]+a2[i]){
            f[1][i]=f[1][i-1]+a2[i];
            l[1][i]=1;
         }
         else{
            f[1][i]=f[0][i-1]+t1[i-1]+a2[i];
            l[1][i]=0;
         }
      }
   }
   
   if(f[0][N-1]+x1 <= f[1][N-1]+x2){
      printf("%d\n", f[0][N-1]+x1);
      for(i=N-1;i>0;i--){
         if(i==N-1)path[i] = l[0][i];
         else path[i] = l[path[i+1]][i];
      }
      for(i=0; i<N;i++){
         if(i==N-1) printf("1 %d", N);
         else printf("%d %d\n", path[i+1]+1, i+1);
      }
   }
   else{
      printf("%d\n", f[1][N-1]+x2);
      for(i=N-1;i>0;i--){
         if(i==N-1)path[i] = l[1][i];
         else path[i] = l[path[i+1]][i];
      }
      for(i=0; i<N;i++){
         if(i==N-1) printf("2 %d", N);
         else printf("%d %d\n", path[i+1]+1, i+1);
      }
   }
   return 0;
}

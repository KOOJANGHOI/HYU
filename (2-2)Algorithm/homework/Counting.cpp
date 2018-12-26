//2013011800_±∏¿Â»∏_A 
#include<stdio.h>
#include<stdlib.h>

int main() {
	int N , M , K;
	int *A , *B , *I , *R;
	scanf("%d %d %d" , &N , &M , &K);
	int count = 0;
	int i = 0;
	
	A = (int*)malloc(sizeof(int)*(K+1));
	B = (int*)malloc(sizeof(int)*(K+1));
	I = (int*)malloc(sizeof(int)*(N+1));
	R = (int*)malloc(sizeof(int)*(M+1));

	A[0] = 0;
	B[0] = 0;
	I[0] = 0;
	for(i = 0 ; i <= M ; i++){
		R[i] = 0;
	}
	
	for(i = 1 ; i <= K ; i++) {
		scanf("%d %d" , &A[i] , &B[i]);
	}
	for(i = 1 ; i <= N ; i++) {
		scanf("%d" , &I[i]);
		R[I[i]]++;
	}	
	for(i = 1 ; i <=M ; i++) {
		R[i] += R[i-1];
	}
	for(i = 1 ; i <= K ; i++) {
		count = R[B[i]] - R[A[i]-1];
		printf("%d\n" , count);
	}

	free(A);
	free(B);
	free(I);
	free(R);
	
	return 0;
}

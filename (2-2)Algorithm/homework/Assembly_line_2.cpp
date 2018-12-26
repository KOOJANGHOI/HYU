//2013011800_±∏¿Â»∏_A
#include<stdio.h>
#include<stdlib.h>
#define MIN(X,Y) ((X) < (Y) ? (X) : (Y))

int main() {	
	int N , e1 , e2 , x1 , x2;
	int *a1 , *a2 , *t1 , *t2 , *f1 , *f2 , *l1 , *l2 , *r;
	int result = 0;
	int i = 0;
	
	scanf("%d" , &N);
	scanf("%d %d" , &e1 , &e2);
	scanf("%d %d" , &x1 , &x2);
	
	a1 = (int*)malloc(sizeof(int)*(N+1));
	a2 = (int*)malloc(sizeof(int)*(N+1));
	t1 = (int*)malloc(sizeof(int)*(N));
	t2 = (int*)malloc(sizeof(int)*(N));
	f1 = (int*)malloc(sizeof(int)*(N+2));
	f2 = (int*)malloc(sizeof(int)*(N+2));
	l1 = (int*)malloc(sizeof(int)*(N+1));
	l2 = (int*)malloc(sizeof(int)*(N+1));
	r = (int*)malloc(sizeof(int)*(N+2));
	
	a1[0] = 0;
	a2[0] = 0;
	t1[0] = 0;
	t2[0] = 0;
	f1[0] = 0;
	f2[0] = 0;
	
	for(i = 1 ; i <= N ; i++) {
		scanf("%d" , &a1[i]);
	}
	for(i = 1 ; i <= N ; i++) {
		scanf("%d" , &a2[i]);
	}
	for(i = 1 ; i < N ; i++) {
		scanf("%d" , &t1[i]);
	}
	for(i = 1 ; i < N ; i++) {
		scanf("%d" , &t2[i]);
	}
	for(i = 0 ; i <= N ; i++) {
		l1[i] = 0;
		l2[i] = 0;
	}
	for(i = 0 ; i <= N+1 ; i++) {
		r[i] = 0;
	}
	
	f1[1] = e1 + a1[1];
	f2[1] = e2 + a2[1];
	
	for(i = 2 ; i <= N ; i++) {
		f1[i] = MIN(f1[i-1] + a1[i] , f2[i-1] + t2[i-1] + a1[i]);
		f2[i] = MIN(f2[i-1] + a2[i] , f1[i-1] + t1[i-1] + a2[i]);
	}
	
	f1[N+1] = f1[N] + x1;
	f2[N+1] = f2[N] + x2;
	
	result = MIN(f1[N+1] , f2[N+1]);
	
	for(i = 2 ; i <= N ; i++) {
		if(f1[i-1] + a1[i] >= f2[i-1] + t2[i-1] + a1[i]) {
			l1[i] = 2;
		}
		else {
			l1[i] = 1;
		}
		if(f2[i-1] + a2[i] >= f1[i-1] + t1[i-1] + a2[i]){
			l2[i] = 1;
		}
		else {
			l2[i] = 2;
		}
	}
	
	if(result == f1[N+1]){
		r[N+1] = 1;	
	}
	else {
		r[N+1] = 2;
	}
	
	for(i = N+1 ; i >= 2 ; i--) {
		if(r[i] == 1) {
			r[i-1] = l1[i-1];
		}
		else {
			r[i-1] = l2[i-1];
		}
	}
	
	printf("%d\n" , result);
	for(i = 2 ; i <= N+1 ; i++) {
		printf("%d %d" , r[i] , i-1);
		if(i == N+1) {
			break;
		}
		printf("\n");
	}
	
	free(a1);
	free(a2);
	free(t1);
	free(t2);
	free(f1);
	free(f2);
	free(l1);
	free(l2);
	free(r);
	
	return 0;
} 

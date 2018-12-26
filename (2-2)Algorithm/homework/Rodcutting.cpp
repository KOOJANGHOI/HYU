//2013011800_±∏¿Â»∏_A
#include<stdio.h>
int main() {
	int N , i , j , q;
	scanf("%d" , &N);
	
	int p[N+1];
	int r[N+1];
	int s[N+1];
	
	p[0] = 0;
	for(i = 1 ; i <= N ; i++) {
		scanf("%d" , &p[i]);
	}
	for(i = 0 ; i <= N ; i++) {
		r[i] = 0;
		s[i] = 0;
	}
	
	for(j = 1 ; j <= N ; j++) {
		q = -1000000;
		for(i = 1 ; i <= j ; i++) {
			if(q < p[i] + r[j-i]) {
				q = p[i] + r[j-i];
				s[j] = i;
			}
		}
		r[j] = q;
	}
	
	printf("%d\n" , r[N]);
	while(N > 0) {
		printf("%d " , s[N]);
		N = N-s[N];
	}
	return 0;
}
 

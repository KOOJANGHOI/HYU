//2013011800_±∏¿Â»∏_A 
#include <stdio.h>
#include <stdlib.h>

int main() {
	int tmp = 0;
	int k = 0;
	int i = 0;
	int j = 0;
	int size = 0;
	
	scanf("%d" , &size);
	int arr[size+1];
	for(k = 0 ; k < size ; k++) {
		arr[k+1] = 0;	
	}
	
	for(k = 0 ; k < size ; k++ ){
		scanf("%d" , &tmp);
		arr[k] = tmp;
	}
	
	for(i = 0 ; i < size ; i ++ ){
		for(j = 0 ; j < size - i ; j++) {
			if(arr[j] < arr[j+1]){
				tmp = arr[j+1];
				arr[j+1] = arr[j];
				arr[j] = tmp; 
			}
		}
	}
	
	for(k = 0 ; k < size ; k++ ){
		printf("%d" , arr[k]);
		printf("\n");
	}
	getchar();
	rewind(stdin);
	return 0;
}

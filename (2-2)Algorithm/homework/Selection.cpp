//2013011800_±∏¿Â»∏_A 
#include<stdio.h>
#include<stdlib.h>
int main() {
	int i = 0;
	int j = 0;
	int size = 0;
	int count = 0;
	int temp = 0;
	int num = 0;
	
	scanf("%d%d" , &size , &count);
	
	int arr[size];
	for(i = 0 ; i < size ; i++) {
        arr[i] = 0;
	}
    
    for(i = 0 ; i < size ; i++ ){
		scanf("%d" , &temp);
		arr[i] = temp;
	}
    
    int min_index = 0;
    int tmp = 0;

    for(i = 0 ; i < count ; i ++ ) {
    	min_index = i;
		for(j = i+1 ; j < size ; j++) {
			if(arr[j] < arr[min_index]) {
				min_index = j;
			}
		}
		tmp = arr[min_index];
		arr[min_index] = arr[i];
		arr[i] = tmp;	
	}
	
    for(i = 0 ; i < size ; i ++) {
    	printf("%d" , arr[i]);
    	printf("\n");
	}
	getchar();
	rewind(stdin);
	return 0;
}

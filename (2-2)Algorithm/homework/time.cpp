//2013011800_구장회_A 
#include <stdio.h> 
#include <stdlib.h>
#include <string.h> 

typedef struct
{ 
	unsigned int weight;
	unsigned int parent, left ,right;
} tempHF, *HuffmanTree;

typedef struct
{
	unsigned int s1; 
	unsigned int s2; 
} findMinimum;

typedef char **HuffmanCode;

HuffmanCode HuffmanCoding(HuffmanTree HT,HuffmanCode HC,unsigned int *w,unsigned int n);
findMinimum Select(HuffmanTree HT,unsigned int n);

HuffmanCode HuffmanCoding(HuffmanTree HT,HuffmanCode HC,unsigned int *w,unsigned int n) 
{
	unsigned int i;
	unsigned int s1=0;
	unsigned int s2=0;
	unsigned int f;
	unsigned int c;
	unsigned int start;
	unsigned int m;
	char *cd; 
	HuffmanTree myHuffman;
	findMinimum min; 

	m=2*n-1;
	HT=(HuffmanTree)malloc((m+1)*sizeof(tempHF)); 
	
	for(myHuffman=HT,i=0;i<=n;i++,myHuffman++,w++) 
	{ 
		myHuffman->weight=*w; 
		myHuffman->parent=0; 
		myHuffman->left=0; 
		myHuffman->right=0; 
	} 

	for(;i<=m;i++,myHuffman++) 
	{ 
		myHuffman->weight=0; 
		myHuffman->parent=0; 
		myHuffman->left=0; 
		myHuffman->right=0; 
	}

	for(i=n+1;i<=m;i++) 
	{ 
		min=Select(HT,i-1); 
		s1=min.s1; 
		s2=min.s2; 
		HT[s1].parent=i; 
		HT[s2].parent=i; 
		HT[i].left=s1; 
		HT[i].right=s2; 
		HT[i].weight=HT[s1].weight+HT[s2].weight; 
	} 

	HC=(HuffmanCode)malloc((n+1)*sizeof(char *)); 
	cd=(char *)malloc(n*sizeof(char *)); 
	cd[n-1]='\0'; 

	for(i=1;i<=n;i++) 
	{ 
		start=n-1;
		for(c=i,f=HT[i].parent;f!=0;c=f,f=HT[f].parent) 
		  if(HT[f].left==c) cd[--start]='0'; 
		  else cd[--start]='1'; 
		  HC[i]=(char *)malloc((n-start)*sizeof(char *)); 
		  strcpy(HC[i],&cd[start]); 
	} 

	free(cd); 
	return HC; 
}

findMinimum Select(HuffmanTree HT,unsigned int n) 
{ 
	unsigned int min1,min2; 
	unsigned int temp; 
	unsigned int i,s1,s2,tempi; 
	findMinimum code; 

	s1=1;
	s2=1; 

	for(i=1;i<=n;i++) 
		if(HT[i].parent==0) 
		{ 
			min1=HT[i].weight; 
			s1=i; 
			break; 
		} 

	tempi=i++;

	for(;i<=n;i++) 
		if(HT[i].weight<min1&&HT[i].parent==0) 
		{ 
			min1=HT[i].weight; 
			s1=i; 
		}
	for(i=tempi;i<=n;i++) 
		if(HT[i].parent==0&&i!=s1) 
		{ 
			min2=HT[i].weight; 
			s2=i; 
			break; 
		}
	for(i=1;i<=n;i++) 
		if(HT[i].weight<min2&&i!=s1&&HT[i].parent==0) 
		{ 
			min2=HT[i].weight; 
			s2=i; 
		}
	if(s1>s2) 
	{ 
		temp=s1; 
		s1=s2; 
		s2=temp; 
	}

	code.s1=s1; 
	code.s2=s2; 
	return code; 
}

int main() 
{ 
	int N , sum;
	char temp[5];
	unsigned int *alphabet=NULL; 
	unsigned int i;
	char ui[9];
	int k=0;
	double tmp;
	int count = 0;
	int SUM = 0;
	HuffmanTree HT=NULL; 
	HuffmanCode HC=NULL; 
	
	scanf("%d" , &N);
	tmp = (double)N;
	
	while(tmp >= 1) {
		tmp = tmp/2;
		count++;
	}
	
	alphabet=(unsigned int *)malloc((N)*sizeof(unsigned int *)); 
	alphabet[0]=0; 
	
	for(i = 1 ; i <= N ; i++)
	{ 
		scanf("%s %d", &temp , &alphabet[i]); 
	} 
	scanf("%d" , &sum);
	
	HC=HuffmanCoding(HT,HC,alphabet,N); 
	
	printf("%d\n" , sum*count);
	
	for(i=1;i<=N;i++)
		SUM += alphabet[i]*strlen(HC[i]);

	printf("%d" , SUM); 
	return 0;
}

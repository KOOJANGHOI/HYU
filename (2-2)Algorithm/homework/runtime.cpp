//2013011800_구장회_A 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int N , *pq , *qp , maxQueued;  
int *a;      

int parent(int i){
  return i/2;
}
int left(int i){
  return 2*i;
}
int right(int i){
  return 2*i+1;
}

void exch(int i, int j){ 

	int t;

	t = pq[i]; 
	pq[i] = pq[j]; 
	pq[j] = t;
	qp[pq[i]] = i;
	qp[pq[j]] = j;
}

void minHeapInit(int *items,int n,int m){ 
	int i;

	a = items;    
	maxQueued=m;
	N = 0; 
	pq=(int*) malloc((maxQueued+1)*sizeof(int)); 
	qp=(int*) malloc(n*sizeof(int));  
	/*
	if (!pq || !qp)
	{
  		printf("malloc failed %d\n",__LINE__);
  		return 0;
	}
	*/
	for (i=0;i<n;i++)
  		qp[i]=(-1);
}

int minHeapEmpty()
{ 
return !N; 
}

int minHeapFull()
{ 
return N == maxQueued; 
}

int less(int i, int j)
{ 

return a[pq[i]] < a[pq[j]]; 
}

void fixUp(int *pq,int k) 
{
	while (k>1 && less(k,parent(k)))
	{
  		exch(k, parent(k));
  		k = parent(k);
	}
}

void minHeapify(int *pq,int k, int N) 
{
	int j;

	while (left(k) <= N)
	{
  		j = left(k);
  		if (j < N && less(j+1, j))
    		j=right(k);
  		if (!less(j, k))
    		break;
  		exch(k, j);
  		k = j;
	}
}

void minHeapInsert(int k)
{ 
	qp[k] = ++N; 
	pq[N] = k; 
	fixUp(pq, N); 
}

int heapExtractMin()
{ 
	exch(1, N); 
	minHeapify(pq, 1, --N); 
	qp[pq[N+1]]=(-1);  
	return pq[N+1]; 
}

void minHeapChange(int k)
{ 
	fixUp(pq, qp[k]); 
	minHeapify(pq, qp[k], N); 
}


int main()
{
	int n,m,i,j;
	int *priority,probSum,expected=0.0;
	int *left,*right;  
	int *parent; 
	int *length;
	char *outString;
	char temp[5];
	int sum = 0;
	int SUM = 0;
	double tmp;
	int count = 0;
	
	scanf("%d",&n);

	m=2*n-1;  
	priority=(int*) malloc(m*sizeof(int));
	left=(int*) malloc(m*sizeof(int));
	right=(int*) malloc(m*sizeof(int));
	parent=(int*) malloc(m*sizeof(int));
	outString=(char*) malloc((n+1)*sizeof(char));
	length=(int*) malloc(m*sizeof(int));
	/*
	if (!priority || !left || !right || !parent || !outString || !length)
	{
  		printf("malloc problem %d\n",__LINE__);
  		exit(0);
	}
	*/
	minHeapInit(priority,m,n);

	for (i=0;i<n;i++)
  		priority[i]=(-1);


	probSum=0.0;
	for (i=0;i<n;i++)
	{
  		scanf("%s %d",&temp,priority+i);
  		probSum+=priority[i];
  		minHeapInsert(i);
  		left[i]=right[i]=(-1);
	}

	for (i=0;i<n;i++)
  		sum += priority[i];

	for (i=n;i<m;i++)
	{
  		left[i]=heapExtractMin();
  		right[i]=heapExtractMin();
  		parent[left[i]]=parent[right[i]]=i;
  		priority[i]=priority[left[i]]+priority[right[i]];
  		minHeapInsert(i);
	}	
	i=heapExtractMin();
	/*
	if (i!=m-1)
	{
  		printf("The root isn't the root\n");
  		exit(0);
	}
	*/
	parent[m-1]=(-1);


	length[m-1]=0;
	for (i=m-1;i>=n;i--)
  		length[left[i]]=length[right[i]]=length[i]+1;

	scanf("%d" , sum);
	tmp = (double)n;
	
	while(tmp >= 1) {
		tmp = tmp/2;
		count++;
	}
	printf("%d\n" , sum*count);

	for (i=0;i<n;i++)
	{
 
  		outString[length[i]]='\0';
  		for (j=i;j!=m-1;j=parent[j])
  		{
  			outString[length[j]-1]=(left[parent[j]]==j) ? '0' : '1';
  			//printf("%s  %d  %d\n",outString,(int)priority[i],strlen(outString));
  			expected+=priority[i]*length[i];
		}
    	SUM += (int)priority[i] * strlen(outString);
	}	
	printf("%d" , SUM);
	free(priority);
	free(left);
	free(right);
	free(parent);
	free(outString);
	free(length);
	free(pq);
	free(qp);
	return 0;
}
//2013011800_±∏¿Â»∏_A 
#include <stdio.h>
#include <algorithm>
#include <vector>
#include <queue>
#include <functional>

using namespace std;

struct Node;
typedef struct Node *Tree;
struct Node
{
   int x;
   int lv;
   Tree left;
   Tree right;
};
struct comparator {
   bool operator() (Tree a, Tree b){
      return a->x > b->x;
   }
};

void Tree_Level(Tree T,int* cnt);
int cntbin(int n);
void Huffman(Tree *T, int n);

int main()
{
   int N,L;
   Tree *T;
   char temp[4];
   
   scanf("%d",&N);
   
   T=(Tree*)malloc(sizeof(Tree)*N);
   
   for(int i=0;i<N;i++) 
   {
      T[i]=(Tree)malloc(sizeof(struct Node));
      scanf("%s %d",temp,&(T[i]->x));
      T[i]->left=T[i]->right=NULL;
      T[i]->lv=0;   
   }
   scanf("%d",&L);
   
   printf("%d\n",L*cntbin(N));
   Huffman(T,N);
   
   return 0;   
}

int cntbin(int n)
{
   int cnt=1;
   while((n/2)!=0)
   {
         n/=2;
         cnt++;
   }   
   return cnt;
}
void Huffman(Tree *T,int n)
{
   int f,s;
   priority_queue< Tree, vector<Tree>, comparator > pq;
   
   int cnt=0;
   
   for(int i=0; i<n; i++)
   {
      pq.push(T[i]);
   }
   for(int i=0;i<n-1;i++)
   {
      Tree tmp;
      Tree tmp2;
      tmp=(Tree)malloc(sizeof(struct Node));
      
      tmp2=pq.top();
      f=tmp2->x;
      tmp2->lv++;
      tmp->left=tmp2;
      pq.pop();
      
      tmp2=pq.top();
      s=tmp2->x;
      tmp2->lv++;
      tmp->right=tmp2;
      pq.pop();
      
      tmp->x=f+s;
      
      pq.push(tmp);
   }
   Tree_Level(pq.top(), &cnt);
   printf("%d\n",cnt);
}

void Tree_Level(Tree T, int* cnt)
{
   if(T==NULL) return;
   if(T->left!=NULL)
   {
      T->left->lv=T->lv+1;
      T->right->lv=T->lv+1;
      Tree_Level(T->left,cnt);
      Tree_Level(T->right,cnt);
   }
   else
   {
      *cnt+=T->x*T->lv;
   }
}

//2013011800_±∏¿Â»∏_A

#include <stdio.h>
#include <vector>
#include <queue>
#include <iostream>
#include <functional>
#include <algorithm>
#define INF 99999999
#define MAX(X,Y) ((X) > (Y) ? (X) : (Y))

using namespace std;

priority_queue<pair<int, int>, vector<pair<int, int> >, greater<pair<int, int> > > PQ;
vector<pair<int, int> > node[5001];
int N, M;
int K;
int u, v, w;

int dijkstra(int start)
{
	int i , j;
	int length[5001];
	for (i = 1; i <= N; i++)
	{
		if (i != start)
			length[i] = INF;
		else
			PQ.push(make_pair(length[i], i));
	}
	length[start] = 0;
	
	while (!PQ.empty())
	{
		int utd = PQ.top().second; 
		int val = PQ.top().first;
		PQ.pop();
		
		for (j = 0 ; j < node[utd].size() ; j++)
		{
			if (length[utd] + node[utd][j].second < length[node[utd][j].first])
			{
				length[node[utd][j].first] = length[utd] + node[utd][j].second;
				PQ.push(make_pair(length[node[utd][j].first], node[utd][j].first));
			}
		}
	}
	
	int temp = 0;
	for (i = 1 ; i <= N ; i++) {
		temp = MAX(temp, length[i]);
	}
	return temp;
}

int main()
{
	int i , j , result_path;
	scanf("%d" , &N);
	
	for (i = 1 ; i <= N ; i++) {
		int vertex, outDegree;
		scanf("%d %d" , &vertex , &outDegree);
		
		for (j = 0 ; j < outDegree ; j++) {
			int v, w;
			scanf("%d %d", &v , &w);
			node[i].push_back(make_pair(v, w));
		}
		
	}
	
	result_path = dijkstra(1);
	printf("%d" , result_path);
	return 0;
}

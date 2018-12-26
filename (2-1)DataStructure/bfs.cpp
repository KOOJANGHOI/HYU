//
//  main.cpp
//  Main
//
//  Created by KOO JANGHOI on 2018. 9. 12..
//  Copyright © 2018년 KOO JANGHOI. All rights reserved.
//
#include <iostream>
#include <algorithm>
#include <vector>
#include <queue>

using namespace std;

class Node {
public:
    int i,j;
    Node(int i , int j):i(i),j(j){}
};
vector<vector<int>> map;
vector<vector<int>> visited(5,vector<int>(5,0));
vector<vector<int>> dist(5,vector<int>(5,-1));
queue<Node> q;

void bfs(vector<vector<int>> &map) {
    int dx[4] = {-1,1,0,0};
    int dy[4] = {0,0,1,-1};
    while(!q.empty()) {
        Node node = q.front();
        q.pop();
        visited[node.i][node.j]=1;
        cout << "visited at " << node.i << " " << node.j << endl;
        for(int i = 0 ; i < 4 ; i++) {
            int ii = node.i+dx[i];
            int jj = node.j+dy[i];
            if(ii>=0 && ii<5&&jj>=0&&jj<5) {
                if(visited[ii][jj]==0 && map[ii][jj]==0) {
                    visited[ii][jj]=1;
                    map[ii][jj]=1;
                    dist[ii][jj]=dist[node.i][node.j]+1;
                    q.push(Node(ii,jj));
                }
            }
        }
    }
}

void print(vector<vector<int>> &map) {
    for(int i = 0 ; i < map.size() ; i++) {
        for(int j = 0 ; j < map[0].size() ; j++) {
            cout << dist[i][j] << " ";
        }
        cout << endl;
    }
    
}
int main() {
    map = {{1,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
    for(int i = 0 ; i < map.size() ; i++) {
        for(int j = 0 ; j < map[0].size() ; j++) {
            if(map[i][j]==1) {
                dist[i][j]=0;
                q.push(Node(i,j));
            }
        }
    }
    bfs(map);
    print(map);
    return 0;
}

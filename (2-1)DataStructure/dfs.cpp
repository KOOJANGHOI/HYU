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

vector<vector<int>> map;
vector<vector<int>> visited(5,vector<int>(5,0));

void print(vector<vector<int>> &map) {
    for(int i = 0 ; i < map.size() ; i++) {
        for(int j = 0 ; j < map[0].size() ; j++) {
            cout << map[i][j] << " ";
        }
        cout << endl;
    }
}

void dfs(int i , int j) {
    cout << "visited at " << i << " " << j << endl;
    int dx[4] = {-1,1,0,0};
    int dy[4] = {0,0,1,-1};
    for(int k = 0 ; k < 4 ; k++) {
        int ii = i+dx[k];
        int jj = j+dy[k];
        if(ii>=0&&ii<5&&jj>=0&&jj<5) {
            if(visited[ii][jj]==0&&map[ii][jj]==0) {
                visited[ii][jj]=1;
                map[ii][jj]=1;
                dfs(ii,jj);
            }
        }
    }
}
int main() {
    map = {{1,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
    for(int i = 0 ; i < map.size() ; i++) {
        for(int j = 0 ; j < map[0].size() ; j++) {
            if(map[i][j]==1&&visited[i][j]==0) {
                visited[i][j]=1;
                dfs(i,j);
            }
        }
    }
    print(map);
    return 0;
}

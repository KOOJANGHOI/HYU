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

using namespace std;

class Node {
public:
    int i,j;
    Node(int i , int j):i(i),j(j){}
};

bool compare(Node n1 , Node n2) {
    if(n1.i!=n2.i) {
        return n1.i < n2.i;
    } else {
        return n1.j < n2.j;
    }
}

void printvector(vector<Node> &vec) {
    for(int i = 0 ; i < vec.size() ; i++) {
        cout << vec[i].i << "," << vec[i].j << endl;
    }
}
int main() {
    vector<Node> temp;
    temp.push_back(Node(2,4));
    temp.push_back(Node(3,2));
    temp.push_back(Node(4,3));
    temp.push_back(Node(1,1));
    temp.push_back(Node(1,3));
    sort(temp.begin(),temp.end(),compare);
    printvector(temp);
    return 0;
}

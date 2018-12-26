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

int n=5,r=3;
vector<int> num,idx;

// argument_vector
void print(vector<int> &arr) {
    for(int i = 0 ; i < arr.size() ; i++) {
        if(idx[i]==1)
            cout << arr[i] << " ";
    }
    cout << endl;
}

int main() {
    /*
     n개에서 r개를 뽑는 조합
     
     1~n이 들어있는 벡터 num
     r개의 1 , n-r개의 0이 들어있는 벡터 idx를 정렬
     
     idx에서 next_permutation 돌면서
     idx==1인 index의 num[index]값을 출력.
     
     */
    for(int i = 1 ; i <= n ; i++) {
        num.push_back(i);
        if(i<=r) {
            idx.push_back(1);
        } else {
            idx.push_back(0);
        }
    }
    sort(idx.begin(),idx.end());                        // 오름차순(default)
    //sort(idx.begin(),idx.end(),greater<int>());       // 내림차순(하면 조합의 결과는 안나옴!!!!!)
    
    
    do {
        print(num);
    }while(next_permutation(idx.begin(),idx.end()));
    return 0;
}

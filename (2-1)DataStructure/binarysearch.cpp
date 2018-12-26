//
//  main.cpp
//  Main
//
//  Created by KOO JANGHOI on 2018. 9. 12..
//  Copyright © 2018년 KOO JANGHOI. All rights reserved.
//
#include <iostream>
#include <algorithm>

using namespace std;

int target = 14;


void binsearch1(int lo , int hi) {
    while(lo <= hi) {
        cout << "calc at " << "[" << lo << "," << hi << "]" << endl;
        int mid = (lo+hi)/2;
        if(target<mid) {
            hi = mid-1;
        } else if(target > mid) {
            lo = mid+1;
        } else {
            cout << "found at " << mid << endl;
            return ;
        }
    }
}

int binsearch2(int lo , int hi) {
    if(lo>hi)
        return -1;
    int mid = (lo+hi)/2;
    if(mid==target) {
        return mid;
    } else if(target < mid) {
        return binsearch2(lo,mid-1);
    } else {
        return binsearch2(mid+1,hi);
    }
}

int main() {
    binsearch1(0,100);
    cout << binsearch2(0,100) << endl;
    return 0;
}

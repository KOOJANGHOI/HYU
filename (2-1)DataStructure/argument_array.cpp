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

void print(int *arr , int size) {
    for(int i = 0 ; i < size ; i++) {
        cout << arr[i] << " ";
    }
    cout << endl;
}

int main() {
    int size = 10;
    int arr[size];
    for(int i = 0 ; i < 10 ; i++) {
        arr[i] = i;
    }
    print(arr,size);
    return 0;
}

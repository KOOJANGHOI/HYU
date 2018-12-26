//
//  main.cpp
//  Main
//
//  Created by KOO JANGHOI on 2018. 9. 12..
//  Copyright © 2018년 KOO JANGHOI. All rights reserved.
//
#include <iostream>
#include <algorithm>
#include <float.h>
#include <cmath>

using namespace std;

int main() {
    double a = 0.1;
    double b = 0.3;
    double c = 0.2;
    cout << ((fabsf(a)-fabsf(b-c))<FLT_EPSILON) << endl;
    cout << (a==(b-c)) << endl;
    return 0;
}

#include <iostream>
#include <vector>
using namespace std;
int main(){
	vector<int> vec1;				//int 자료형을 저장하는 동적배열
	vector<double> vec2;			//double 자료형을 저장하는 동적배열
	vector<Node> vec3;				//사용자가 정의한 Node 구조체를 저장하는 동적배열
	
	vector<int> vec4(n);			//벡터의 초기 크기를 n으로 설정
	vector<int> vec5(n, 1);			//벡터의 초기 크기를 n으로 설정하고 1로 초기화
	vector<vector<int> > vec6(n, vector<int>(m, 0));	//크기가 n*m인 2차원 벡터를 선언하고 0으로 초기화
	
	vec1.push_back(5);	//벡터의 맨 뒤에 원소(5) 추가
	
	vec1.pop_back();	//벡터의 맨 뒤 원소 삭제
	
	printf("%d\n", vec1.size());	//벡터의 크기 출력
	
	vec1.resize(n);	//벡터의 크기를 n으로 재설정
	
	vec1.clear();	//벡터의 모든 원소 삭제
	
	vec1.begin();	//벡터의 첫 원소의 주소
	vec1.end();		//벡터의 마지막 원소의 다음 주소 리턴

	return 0;
}
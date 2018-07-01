
#include <iostream>
#include <vector>
#include <set>
#include <string>
#include <tuple>
#include <fstream>
#include <cmath>
#include <iomanip>
#include <map>
#include <algorithm>
using namespace std;

class Apriori{
private:
    int length;
    long double minSupport;
    map<vector<int>,long double> C;
    map<vector<int>,long double> L;
    vector<vector<int>> transactions;
    map<int,vector<vector<int>>> frequentPatterns;
    vector<tuple<vector<int>, vector<int>, long double, long double> > associationRules;
    
public:
    Apriori(long double _minSupport) {
        
        length = 1;
        minSupport = _minSupport;
    }
    
    void readInputFile(string filename) {
        ifstream fin;
        fin.open(filename);
        string str;
        while(!getline(fin, str).eof()) {
            subroutine_readInputFile(str);
        }
        subroutine_readInputFile(str);
        fin.close();
    }
    
    void writeToOutputFile(string filename) {
        ofstream fout;
        fout.open(filename);
        for(tuple<vector<int>, vector<int>, long double, long double> t : associationRules) {
            fout<< vectorToString(get<0>(t)) << '\t';
            fout<< vectorToString(get<1>(t)) << '\t';
            
            fout << fixed;
            fout.precision(2);
            fout << get<2>(t) << '\t';
            
            fout << fixed;
            fout.precision(2);
            fout << get<3>(t);
            
            fout << endl;
        }
        fout.close();
        
    }
    
    string vectorToString(vector<int> vec) {
        string res = "{";
        for(int i = 0 ; i < vec.size() ; i++) {
            res += to_string(vec[i]);
            if(i != vec.size()-1) res += ",";
        }
        res += "}";
        return res;
    }
    
    void subroutine_readInputFile(string str) {
        vector<int> temp;
        int idx = 0;
        for(int i = 0 ; i < str.size() ; i++) {
            if(str[i] == '\t') {
                temp.push_back(atoi(str.substr(idx,i).c_str()));
                idx = i+1;
            }
        }
        temp.push_back(atoi(str.substr(idx,str.size()).c_str()));
        sort(temp.begin(),temp.begin()+temp.size());
        transactions.push_back(temp);
    }
    
    void runJob() {
        while(true) {
            generateC(length);
            if(C.size()==0) break;
            generateL();
            length++;
        }
        
        for(int i = 1 ; i < length ; i++) {
            for(int j = i ; j < length-i ; j++) {
                if(i==j) {
                    vector<vector<int>> curL = frequentPatterns[i];
                    for(int a = 0 ; a < curL.size() ; a++) {
                        for(int b = a+1 ; b < curL.size() ; b++) {
                            vector<int> small = curL.at(a);
                            vector<int> large = curL.at(b);
                            if(!isHaveSameItem(small, large) && seekNextLength(small, large)) {
                                generateAssociationRules(small, large);
                            }
                        }
                    }
                } else {
                    vector<vector<int>> shortL = frequentPatterns[i];
                    vector<vector<int>> longL = frequentPatterns[j];
                    for(int a = 0 ; a < shortL.size() ; a++) {
                        for(int b = 0 ; b < longL.size() ; b++) {
                            vector<int> small = shortL.at(a);
                            vector<int> large = longL.at(b);
                            if(!isContains(small, large) && seekNextLength(small, large)) {
                                generateAssociationRules(small, large);
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    
    bool isHaveSameItem(vector<int> small , vector<int> large) {
        for(int item : small) {
            if (find(large.begin(), large.end(), item) != large.end())
                return true;
        }
        return false;
    }
    
    bool seekNextLength(vector<int> small , vector<int> large) {
        vector<int> temp;
        for(int item : small) temp.push_back(item);
        for(int item : large) temp.push_back(item);
        sort(temp.begin(),temp.begin()+temp.size());
        int nextLength = (int)small.size()+(int)large.size();
        for(vector<int> compare : frequentPatterns[nextLength]) {
            if(temp==compare) return true;
        }
        return false;
    }
    
    bool isContains(vector<int> small , vector<int> large) {
        for(int item : small) {
            if (find(large.begin(), large.end(), item) == large.end())
                return false;
        }
        return true;
    }
    
    bool isSameVector(vector<int> small , vector<int> large) {
        if((int)small.size() != (int)large.size()) return false;
        for(int i = 0 ; i < (int)small.size() ; i++) {
            if(small.at(i) != large.at(i)) return false;
        }
        return true;
    }
    
    void generateAssociationRules(vector<int> itemList1 , vector<int> itemList2) {
        vector<int> agg;
        for(int item : itemList1) agg.push_back(item);
        for(int item : itemList2) agg.push_back(item);
        sort(agg.begin(),agg.begin()+agg.size());
        long double support = (long double)getSupport(agg);
        long double confidence1 = (long double)support/getSupport(itemList1)*100.0;
        long double confidence2 = (long double)support/getSupport(itemList2)*100.0;
        associationRules.push_back(make_tuple(itemList1,itemList2,support,confidence1));
        associationRules.push_back(make_tuple(itemList2,itemList1,support,confidence2));
        return ;
    }
    
    void generateC(int length) {
        
        if(length==1) {
            set<int> oneItemSet;
            for(vector<int> transaction : transactions) {
                for(int item : transaction) {
                    oneItemSet.insert(item);
                }
            }
            for(int item : oneItemSet) {
                vector<int> temp;
                temp.push_back(item);
                C[temp] = getSupport(temp);
            }
        } else {
            joiningAndPrunning();
        }
    }
    void joiningAndPrunning() {
        
        vector<vector<int>> joingingResult;
        vector<vector<int>> restoredListOfL;
        set<vector<int>> restoredSetOfL;
        
        for(map<vector<int>,long double>::iterator it =L.begin(); it!=L.end();it++) {
            restoredListOfL.push_back(it->first);
            restoredSetOfL.insert(it->first);
        }
        
        L.clear();
        C.clear();
        
        long double len = restoredListOfL.size();
        for(int i = 0 ; i < len ; i++) {
            for(int j = i+1 ; j < len ; j++) {
                int idx = 0;
                for(idx = 0 ; idx < length-2 ; idx++) {
                    if(restoredListOfL[i][idx] != restoredListOfL[j][idx])
                        break;
                }
                if(idx==length-2) {
                    vector<int> temp;
                    for(int k = 0 ; k < length-2 ; k++) {
                        temp.push_back(restoredListOfL[i][k]);
                    }
                    if(restoredListOfL[i][idx] > restoredListOfL[j][idx]) {
                        temp.push_back(restoredListOfL[j][idx]);
                        temp.push_back(restoredListOfL[i][idx]);
                    } else {
                        temp.push_back(restoredListOfL[i][idx]);
                        temp.push_back(restoredListOfL[j][idx]);
                    }
                    joingingResult.push_back(temp);
                }
            }
        }
        for(vector<int> cand : joingingResult) {
            int idx = 0;
            long double len = cand.size();
            for(idx = 0 ; idx < len ; idx++) {
                vector<int> temp = cand;
                temp.erase(temp.begin()+idx);
                if(restoredSetOfL.find(temp)==restoredSetOfL.end()) {
                    break;
                }
            }
            if(idx==len) {
                C[cand] = getSupport(cand);
            }
        }
    }
    void generateL() {
        
        vector<vector<int>> temp;
        for(map<vector<int>,long double>::iterator it =C.begin(); it!=C.end();it++) {
            if(it->second >= minSupport) {
                L[it->first] = it->second;
                temp.push_back(it->first);
            }
        }
        frequentPatterns[length]=temp;
    }
    
    long double getSupport(vector<int> itemList) {
        
        int hit = 0;
        for(vector<int> transaction : transactions) {
            int idx = 0;
            if(transaction.size() < itemList.size()) continue;
            for(int i = 0 ; i < itemList.size() ; i++) {
                for(int j = 0 ; j < transaction.size() ; j++){
                    if(itemList[i]==transaction[j]) idx++;
                }
            }
            if(idx==itemList.size()) hit++;
        }
        return (long double)hit/transactions.size()*100.0;
    }
};

int main(int argc, const char * argv[]) {
    
    
    long double _minSupport = stold(argv[1]);
    string _inputFile = argv[2];
    string _outputFile = argv[3];
    
    Apriori apriori = Apriori(_minSupport);
    apriori.readInputFile(_inputFile);
    apriori.runJob();
    apriori.writeToOutputFile(_outputFile);
    return 0;
}

// ConsoleApplication4.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include<iostream>
#include<fstream>
#include<string>
#include<stdlib.h>
using namespace std;

int main()
{
	long count = 0;
	ifstream fin;
	fin.open("C:/Users/wangjd7/Desktop/基站预处理信息.csv");
	ofstream fout;
	fout.open("C:/Users/wangjd7/Desktop/过滤后的基站信息.csv");
	string content;
	getline(fin, content);
	string str;
	while (getline(fin, content)) {
		str = "";
		count++;
		cout << "插入第" << count << "条数据" << endl;
		int douhaocount = 0;
		for (int i = 0; i < content.length(); i++) {
			if (content[i] == ',') {
				douhaocount++;
				if (douhaocount == 4) str += ",\"";
				else if (douhaocount == 5) str += "\",\"";
				else if (douhaocount == 6) str += "\",\"";
				else str += ",";
			}
			else {
				str += content[i];
			}
		}
		str += '"';
		fout << count << ',' << str << endl;
	}
	fin.close();
	fout.close();
    return 0;
}


// ConsoleApplication1.cpp : �������̨Ӧ�ó������ڵ㡣
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
	ifstream fin("C:/Users/wangjd7/Desktop/neJiZhan.csv");
	ofstream fout("C:/Users/wangjd7/Desktop/newJiZhan.csv");
	string content;
	while (getline(fin, content)) {
		if (content[content.length() - 1] == '?') {
			content[content.length() - 1] = '\"';
			count++;
		}
		fout << content << endl;
	}
	fin.close();
	fout.close();
	cout << count << endl;
	system("pause");
	return 0;
}

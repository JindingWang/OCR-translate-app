// ConsoleApplication4.cpp : �������̨Ӧ�ó������ڵ㡣
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
	fin.open("C:/Users/wangjd7/Desktop/��վԤ������Ϣ.csv");
	ofstream fout;
	fout.open("C:/Users/wangjd7/Desktop/���˺�Ļ�վ��Ϣ.csv");
	string content;
	getline(fin, content);
	string str;
	while (getline(fin, content)) {
		str = "";
		count++;
		cout << "�����" << count << "������" << endl;
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


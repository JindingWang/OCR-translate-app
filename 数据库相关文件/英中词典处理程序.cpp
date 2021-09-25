// ConsoleApplication1.cpp : 定义控制台应用程序的入口点。
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
	fin.open("C:/Users/wangjd7/Desktop/英中词典.txt"); //英中词典
	ofstream fout;
	fout.open("C:/Users/wangjd7/Desktop/Dict.csv");
	string english;
	string chinese;
	string content;
	string frontstr = "span class=\"text_blue\"";
	string douhao = "，";
	string fenhao = "；";
	int len = 22;

	while (getline(fin, content)) {
		count++;
		cout << "插入第" << count << "条数据" << endl;
		english = "";
		for (int i = 0; i < content.length(); i++) {
			english += tolower(content[i]);
		}
		getline(fin, content);
		chinese = "";
		for (int i = 0; i < content.length()-len; i++) {
			if (content[i] == 's') {
				string str = content.substr(i, len);
				if (str == frontstr) {
					int beginIndex = i + len + 1;
					while (content[beginIndex] != '<' && content[beginIndex] != ';' && content[beginIndex] != ','
						&& (content[beginIndex] != fenhao[0] || content[beginIndex+1] != fenhao[1])
						&& (content[beginIndex] != douhao[0] || content[beginIndex+1] != douhao[1])) {
						chinese += content[beginIndex];
						beginIndex++;
					}
					break;
				}
			}
		}
		fout << count << ",\"" << english << "\",\"" << chinese << '\"' << endl;
		getline(fin, content);
	}
	fin.close();
	fout.close();
	return 0;
}


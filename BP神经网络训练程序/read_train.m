function [imglist] = read_train(root)
% niΪ��ȡͼƬ������nΪ�ļ�����Ŀ
%========��ȡ�ļ���========%
out_Files = dir(root);%չ��
tempind=0;
imglist=cell(0);
n=length(out_Files);
%========��ȡ�ļ�========%
for i = 1:n;
    if strcmp(out_Files(i).name,'.')|| strcmp(out_Files(i).name,'..')
    else
        rootpath=strcat(root,'/',out_Files(i).name);
        in_filelist=dir(rootpath);
        ni=length(in_filelist);
        for j=1:ni
            if strcmp(in_filelist(j).name,'.')|| strcmp(in_filelist(j).name,'..')|| strcmp(in_filelist(j).name,'Desktop_1.ini')|| strcmp(in_filelist(j).name,'Desktop_2.ini')
            else
                tempind=tempind+1;
                imglist{tempind}=imread(strcat(rootpath,'/',in_filelist(j).name));
            end
        end
    end
end
end


function feature = feature_lattice(img)
% ����:�׵׺��ֵĶ�ֵͼ�������49ά����������
% ======��ȡ������ת��7*7������ʸ��,��ͼ����ÿ4*4�ĵ���л�����ӣ�������ӳ�һ����=====%
%======��ͳ��ÿ��С������ͼ��������ռ�ٷֱ���Ϊ��������====%
for i=1:length(img);
bw_2828=img{i};
for cnt=1:7
    for cnt2=1:7
        Atemp=sum(bw_2828(((cnt*4-3):(cnt*4)),((cnt2*4-3):(cnt2*4))));%4*4box
        lett((cnt-1)*7+cnt2)=sum(Atemp);
    end
end
lett=lett/4080; %4080 = 16*255
lett=lett';
feature(:,i)=lett;
end
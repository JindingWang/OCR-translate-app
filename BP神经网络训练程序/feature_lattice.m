function feature = feature_lattice(img)
% 输入:白底黑字的二值图像。输出：49维的网格特征
% ======提取特征，转成7*7的特征矢量,把图像中每4*4的点进行划分相加，进行相加成一个点=====%
%======即统计每个小区域中图像象素所占百分比作为特征数据====%
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
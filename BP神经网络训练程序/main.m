clc;
clear all;
close all;
%% ��ȡͼ��
root='./newFNT';
img=read_train(root);
%% ��ȡ����
img_feature=feature_lattice(img);
%% �����ǩ
class=26;
numberpclass=1970;
ann_label=zeros(class,numberpclass*class);
ann_data=img_feature;
for i=1:class
    for j=numberpclass*(i-1)+1:numberpclass*i
        ann_label(i,j)=1;
    end
end

%% ѡ��ѵ�����Ͳ��Լ�
k=rand(1,numberpclass*class);
[m,n]=sort(k);
ntraindata=26*1770;
ntestdata=26*200;
train_data=ann_data(:,n(1:ntraindata));
test_data=ann_data(:,n(ntraindata+1:numberpclass*class));
train_label=ann_label(:,n(1:ntraindata));
test_label=ann_label(:,n(ntraindata+1:numberpclass*class));
%% BP�����紴����ѵ���Ͳ���
net=network_train(train_data,train_label);
predict_label=network_test(test_data,net);
%% ��ȷ�ʼ���
[u,v]=find(test_label==1);
label=u';
error=label-predict_label;
accuracy=size(find(error==0),2)/size(label,2)
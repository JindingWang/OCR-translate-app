function out = network_test(test_data,net)
% ���룺�������ݵ���������ֵ��������������ݵ�label�Լ����ͼ
% BP����Ԥ��

an=sim(net,test_data);
for i=1:length(test_data)
    out(i)=find(an(:,i)==max(an(:,i)));
end

end


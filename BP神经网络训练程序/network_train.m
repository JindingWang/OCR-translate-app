function net = network_train(train_data,train_label )
% ���룺ѵ��ͼ��������label�������ѵ���õ�������

% BP����ѵ��
% ��ʼ������ṹ
layer=40;
net=newff(train_data,train_label,layer);
net.trainParam.epochs=1000;
net.trainParam.lr=0.02;
net.trainParam.goal=0.00001;
net.trainFcn='traincgf';
net.trainParam.max_fail = 6;
% ����ѵ��
net=train(net,train_data,train_label);
end


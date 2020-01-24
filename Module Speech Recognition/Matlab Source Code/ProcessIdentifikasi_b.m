function [net kelas]= ProcessIdentifikasi(Datain, fiturPerOrang)

% len=size(Datain);
[m n] = size(Datain);
for i=m:-1:1
%     if mod(i,fiturPerOrang)==1
%         temp = randint(5,1);
%         kelas(1:5,i:i)= temp;
%     else
%         kelas(1:5,i:i)= temp;
%     end
%     kelas(:,i) = bitget(uint8(ceil(i/fiturPerOrang)), 1:5)';
    kelas(:,i) = de2bi(ceil(i/fiturPerOrang), 5)';
end

% BackPropagation Neural Network
net = newff(minmax(Datain'),[25 5], {'tansig' 'logsig'},'traingd');
net.trainParam.show = 50;
net.trainParam.lr = 0.015;%0.2;%0.95
net.trainParam.epochs = 10000;
net.trainParam.goal = 1e-2;

net = train(net,Datain',kelas);
end


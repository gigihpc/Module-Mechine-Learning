clear all; close all; clc;  

%Data Trainning
fitur1 = Fitur_Extraction('G1.wav');
fitur2 = Fitur_Extraction('G2.wav');
% fitur3 = Fitur_Extraction('other.wav');
Org4 = Fitur_Extraction('O4.wav');
Org4_1 = Fitur_Extraction('O4_1.wav');
Org5 = Fitur_Extraction('O5.wav');
Org5_1 = Fitur_Extraction('O5_1.wav');
Org6 = Fitur_Extraction('O6.wav');
Org6_1 = Fitur_Extraction('O6_1.wav');
Org7  = Fitur_Extraction('O7.wav');
Org7_1 = Fitur_Extraction('O7_1.wav');
Org8 = Fitur_Extraction('O8.wav');
Org8_1 = Fitur_Extraction('O8_1.wav');
Org9 = Fitur_Extraction('O9.wav');
Org9_1 = Fitur_Extraction('O9_1.wav');
Org10 = Fitur_Extraction('O10.wav');
Org10_1 = Fitur_Extraction('O10_1.wav');
Org11 = Fitur_Extraction('O11.wav');
Org11_1 = Fitur_Extraction('O11_1.wav');

%Data Testing
testfitur= Fitur_Extraction('G3_test.wav');
O4_t = Fitur_Extraction('O4_t');
O5_t = Fitur_Extraction('O5_t');
O6_t = Fitur_Extraction('O6_t');
O7_t = Fitur_Extraction('O7_t');
O8_t = Fitur_Extraction('O8_t');
O9_t = Fitur_Extraction('O9_t');
O10_t = Fitur_Extraction('O10_t');
O11_t = Fitur_Extraction('O11_t');

%     for i=1:length(fitur1)
%         if i==1
%             kelas1(1,i)=1;
%         elseif mod(i,2)==0
%             kelas1(1,i)=0;
%             kelas3(1,i)=0;
%         else
%             kelas1(1,i)=0;s
%             kelas3(1,i)=0;
%         end
%     end
%     kelas3(1,3)=1;
kelas1=zeros(1,length(fitur1)); kelas1(1,1)=1;kelas1(1,2)=1;
kelas2=kelas1;
kelas4=zeros(1,length(fitur1));kelas4(1,2)=1;kelas4(1,3)=1;
kelas4_1=kelas4;
kelas5=zeros(1,length(fitur1));kelas5(1,3)=1;kelas5(1,4)=1;
kelas5_1=kelas5;
kelas6=zeros(1,length(fitur1));kelas6(1,4)=1;kelas6(1,5)=1;
kelas6_1=kelas6;
kelas7=zeros(1,length(fitur1));kelas7(1,5)=1;kelas7(1,6)=1;
kelas7_1=kelas7;
kelas8=zeros(1,length(fitur1));kelas8(1,6)=1;kelas8(1,7)=1;
kelas8_1=kelas8;
kelas9=zeros(1,length(fitur1));kelas9(1,7)=1;kelas9(1,8)=1;
kelas9_1=kelas9;
kelas10=zeros(1,length(fitur1));kelas10(1,8)=1;kelas10(1,9)=1;
kelas10_1=kelas10;
kelas11=zeros(1,length(fitur1));kelas11(1,9)=1;kelas11(1,10)=1;
kelas11_1=kelas11;

save data.txt fitur1 fitur2 Org4 Org4_1 Org5 Org5_1 Org6 Org6_1 Org7 Org7_1 Org8 Org8_1 Org9 Org9_1 Org10 Org10_1 Org11 Org11_1 -ascii;
save kelas.txt kelas1 kelas1 kelas4 kelas4 kelas5 kelas5 kelas6 kelas6 kelas7 kelas7 kelas8 kelas8 kelas9 kelas9 kelas10 kelas10 kelas11 kelas11 -ascii;
load data.txt;
load kelas.txt;
% jmldata=[length(fitur1) length(fitur2)];
% cpy_coef1=data;
% [data_train kelas]=EkstraksiData(data,jmldata);

%Proses Klasifikasi
% z=size(coef);
% for b=1:2
%     for i=1:z(2)
%         if mod(i,2)==0
%             t(b,i)=1;
%         else
%             t(b,i)=0;
%         end
%     end
% end
% net = newff(coef,t,[4 5 6],{'logsig' 'purelin'},'traingd');

% net.trainFcn = 'traingd';            %# training function
% net.trainParam.epochs = 1000;        %# max number of iterations
% net.trainParam.lr = 0.05;            %# learning rate
% net.performFcn = 'mse';              %# mean-squared error function
% net.divideFcn = 'dividerand';        %# how to divide data
% net.divideParam.trainRatio = 70/100; %# training set
% net.divideParam.valRatio = 15/100;   %# validation set
% net.divideParam.testRatio = 15/100;  %# testing set
% net.trainParam.lr = 0.01;
% net.trainParam.epochs = 3000;
% net.trainParam.goal = 1e-3;

% init(net);
% [net,tr]=train(net,coef,t);
% view(net)

% Back Propagation Neural Network

net = newff(data,kelas,[15 20], {'tansig' 'tansig' 'logsig'},'traingd');
net.trainParam.show = 25;        % Show results every 50 iterations
net.trainParam.max_fail=3;         % Maximum validation failures
net.trainParam.epochs = 5000;     % Max number of iterations
% net.trainParam.goal = 1e-2;      % Error tolerance stopping criterion
net.trainParam.mu = 0.9;         % Momentum 
net.trainParam.lr = 0.001;        % Learning rate
net.trainParam.min_grad = 1e-7;
net.trainParam.showWindow=1;
% 
% voice_1=size(coef_1);
% voice_2=size(coef_2);
% if(voice_1(1)> voice_2(1))
%     for i=voice_2(1)+1:voice_1(1)
%         coef_2(i,:)=0;
%     end
% else
%     for i=voice_1(1)+1:voice_2(1)
%         coef_1(i,:)=0;
%     end
% end
% 
% for i=1:voice_1(2)
%     target_v1(i)= 0.5;
%     target_v2(i)=0.5;
% end
% 
% targets = [target_v1];
% train_data = [coef_1(1:voice_1(2)); coef_2(1:voice_2(2))];
net = train(net,data,kelas);
[Y,Pf,Af,E,perf] = sim(net,[testfitur;testfitur;O4_t;O4_t;O5_t;O5_t;O6_t;O6_t;O7_t;O7_t;O8_t;O8_t;O9_t;O9_t;O10_t;O10_t;O11_t;O11_t]);
out=floor(Y);

% PP=0;NN=0;NP=0;PN=0;acc=0;
% len = size(out);
% for i=1:len(1)
%     PP = length(find(out(i,:)== kelas(i,:)));
%     NN = length(find(out(i,:)==kelas(i,:)));
%     acc = acc+PP;
% end
% akurasi = (acc/(len(1)*len(2))) * 100
% view(net);
%Proses Test
% wav_file_test = 'G3.wav'; 
% Read speech samples, sampling rate and precision from file
% [ speech_3, fs, nbits ] = wavread( wav_file_test );
% coef_3 = Voice_Identification(speech_3);
% voice_3=size(coef_3);
% network_output = sim(net,coef_3(1:voice_3(2)))
% Feature extraction (feature vectors as columns)
% [ MFCCs_test, FBEs, frames ] = mfcc( speech, fs, Tw, Ts, alpha, @hamming, [LF HF], M, C+1, L );
% 
% iden = round(sim(net,coef))
% net.trainParam.epochs           10          Maximum number of epochs to train
% net.trainParam.goal             0           Performance goal
% net.trainParam.lr               0.01        Learning rate
% net.trainParam.lr_inc           1.05        Ratio to increase learning rate
% net.trainParam.lr_dec           0.7         Ratio to decrease learning rate
% net.trainParam.max_fail         5           Maximum validation failures
% net.trainParam.max_perf_inc		1.04      Maximum performance increase
% net.trainParam.mc               0.9         Momentum constant
% net.trainParam.min_grad         1e-10       Minimum performance gradient
% net.trainParam.show             25          Epochs between displays (NaN for no displays)
% net.trainParam.showCommandLine	0         Generate command-line output
% net.trainParam.showWindow         1         Show training GUI
% net.trainParam.time             inf         Maximum time to train in seconds
% 
% net = newff(P,T,[S1 S2...S(N-l)],{TF1 TF2...TFNl}, BTF,BLF,PF,IPF,OPF,DDF)
% 
% newff(P,T,[S1 S2...S(N-l)],{TF1 TF2...TFNl}, BTF,BLF,PF,IPF,OPF,DDF) takes several arguments
% 
% P	R x Q1 matrix of Q1 sample R-element input vectors
% T	SN x Q2 matrix of Q2 sample SN-element target vectors
% Si	Size of ith layer, for N-1 layers, default = [ ]. (Output layer size SN is determined from T.)
% TFi	Transfer function of i th layer. (Default = 'tansig' for hidden layers and 'purelin' for output layer.)
% BTF	Backpropagation network training function (default = 'trainlm')
% BLF	Backpropagation weight/bias learning function (default = 'learngdm')
% IPF	Row cell array of input processing functions. (Default = {'fixunknowns','removeconstantrows','mapminmax'})
% OPF	Row cell array of output processing functions. (Default = {'removeconstantrows','mapminmax'})
% DDF	Data divison function (default = 'dividerand')
% 
% and returns an N-layer feed-forward backpropagation network.
% 
% The transfer functions TFi can be any differentiable transfer function such as tansig, logsig, or purelin.
% 
% The training function BTF can be any of the backpropagation training functions such as trainlm, trainbfg, trainrp, traingd, etc.
% 
%       Caution    trainlm is the default training function because it is very fast, but it requires a lot of memory to run. If you get an out-of-memory error when training, try one of these: 
% 
%     * Slow trainlm training but reduce memory requirements by setting net.trainParam.mem_reduc to 2 or more. (See help trainlm.)
%     * Use trainbfg, which is slower but more memory efficient than trainlm.
%     * Use trainrp, which is slower but more memory efficient than trainbfg.
% 
% The learning function BLF can be either of the backpropagation learning functions learngd or learngdm.
% 
% The performance function can be any of the differentiable performance functions such as mse or msereg.

close all;
clear all;
clc;

p = [-1 -1 2 2; 0 5 0 5];
t = [-1 -1 1 1];

net = newff(p,t,4,{},'traingd');

net.trainParam.lr = 0.05;
net.trainParam.epochs = 300;
net.trainParam.goal = 1e-3;

[net,tr]=train(net,p,t);
a = sim(net,p)
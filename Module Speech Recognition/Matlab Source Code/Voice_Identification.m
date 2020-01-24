function coef = Voice_Identification(input_signal)

    % Parameter signal
    Fs = 8000;  % 8000 Hz
    T = 1/Fs;   % periode
    L = 10000;  % length data
    t = (0:L-1)*T;
    
    % Parameter framming & fft
    frame_length = 0.03 * 8000;         % 30 ms
    frame_shift = 0.025 * 8000;         % 25 ms
    NFFT = 2^nextpow2(frame_length);    % panjang fft
    
    % Parameter filter bank trapesium
    t_a = 5;
    t_c = 3;
    tsign = 0.125;
    
    
    % Create imitation signal
    % sum of 10, 25, 50, dan 100
%     x = cos(2*pi*10*t)+cos(2*pi*100*t)+cos(2*pi*500*t)+cos(2*pi*1200*t);
%     y = x + randn(size(t));       % Sinusoid plus noise
    
%     plot(t, y);
%     title('Signal Corrupted with Zero-Mean Random Noise');
%     xlabel('time (seconds)');
    
    % Uji coba
    signal_out = preEmphasize(input_signal);
    fft_out = frammingAndFFT(signal_out, frame_length, frame_shift, NFFT);
    coef = filterBankTrapesium(fft_out, t_a, t_c, tsign);
    
%     signal_in = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65];
% %     signal_out = preEmphasize(signal_in);
% %     frame_out = framming(signal_in, 5, 3);
%     fft_out = frammingAndFFT(signal_in, 25, 20, 32);
% %     fft_in = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16;1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16;1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16;1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16];
%     filterBankTrapesium(fft_out, 3, 2, 0.0125);
%     a = 0;
end

function signal_out = preEmphasize(signal_in)
    alpha = 0.125;
    len = length(signal_in);
    for idx=len:-1:2
        signal_out(idx) = signal_in(idx) - alpha * signal_in(idx-1);
    end
    signal_out(1) = signal_in(1);
end

function fft_out = frammingAndFFT(signal_in, frame_length, frame_shift, NFFT)
    len = length(signal_in);
    num_frame = 1 + ceil((len - frame_length)/frame_shift);
    fft_out = zeros(num_frame, NFFT/2+1);
    
    % FFT 1-NFFT/2+1 and power(magnitude) and normalization
    for idx=0:1:num_frame-2
        tmp = fft(signal_in((idx*frame_shift+1):(idx*frame_shift+frame_length)), NFFT);
        fft_out(idx+1,:) = tmp(1:NFFT/2+1) .* conj(tmp(1:NFFT/2+1));
        maxPower = max(fft_out(idx+1,:));
        fft_out(idx+1,:) = fft_out(idx+1,:) / maxPower;
    end
    idx = idx + 1;
    tmp = fft(signal_in((idx*frame_shift+1):len), NFFT);
    fft_out(idx+1,:) = tmp(1:NFFT/2+1) .* conj(tmp(1:NFFT/2+1));
    plot(fft_out(1,:));
    
    maxPower = max(fft_out(idx+1,:));
    fft_out(idx+1,:) = fft_out(idx+1,:) / maxPower;
    
    plot(fft_out(1,:));
    a=0;
end

function filter_out = filterBankTrapesium(fft_in, a, c, tsign)
    filter = [0:1/c:1,ones(1,a-2),1:-1/c:0];
    len_filter = a + c + c;
    [row,col] = size(fft_in);
    num_coef = ceil(col/a);
    filter_out = zeros(row, num_coef);
    
    for idx_row=1:1:row
        idx_col = 0;
        filter_out(idx_row,idx_col+1) = ceil(max(filter(c+1:len_filter) .* fft_in(idx_row,idx_col*a+1:idx_col*a+len_filter-c)) - tsign);
        for idx_col=1:1:num_coef-1
            if idx_col*a-c+len_filter > col
                filter_out(idx_row,idx_col+1) = ceil(max(filter .* [fft_in(idx_row,idx_col*a+1-c:col),zeros(1,idx_col*a-c+len_filter-col)]) - tsign);
            else
                filter_out(idx_row,idx_col+1) = ceil(max(filter .* fft_in(idx_row,idx_col*a+1-c:idx_col*a-c+len_filter)) - tsign);
            end
        end
    end
end

% function filter_out = filterBankTrapesium(fft_in, a, c, tsign)
%     ffx = fft_in(1,:);
%     fft_in = fft_in - 0.125;
%     filter_out = ceil(fft_in);
%     ww = sum(filter_out);
%     a = 0;
% end


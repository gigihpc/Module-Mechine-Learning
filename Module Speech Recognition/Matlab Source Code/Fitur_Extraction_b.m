function [fft_out,fitur] = Fitur_Extraction(fileName)
    [y, Fs, nbits, opts] = wavread(fileName);
    
    if Fs ~= 8000
        signal_in = resample(y, 8000, Fs);
    end
    
    % Parameter signal
%     Fs = 8000;      % 8000 Hz
%     T = 1/Fs;       % periode
    alpha = 0.975;    % alpha
    
    % Parameter framming & fft
    frame_length = 0.03 * 8000;         % 30 ms
    frame_shift = 0.025 * 8000;         % 25 ms
%     NFFT = 2^(nextpow2(frame_length)+);    % panjang fft
    NFFT = 1024;
    
    % Parameter filter bank trapesium
%     t_a = 5;
%     t_c = 3;
    len_rect = 3;
    tsign = 0.125;
    
    signal_out = preEmphasize(signal_in, alpha);
    fft_out = frammingAndFFT(signal_out, frame_length, frame_shift, NFFT);
    fft_normal = fftNormalization(fft_out);
    filter_out = filterBankRectangle(fft_normal, len_rect, tsign);
    fitur = fiturNormalization(filter_out);
end

function signal_out = preEmphasize(signal_in, alpha)
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
    % FFT 1-NFFT/2+1, power/magnitude and normalization
    for idx=0:1:num_frame-2
        tmp = fft(signal_in((idx*frame_shift+1):(idx*frame_shift+frame_length)), NFFT);
        fft_out(idx+1,:) = tmp(1:NFFT/2+1) .* conj(tmp(1:NFFT/2+1));
%         maxPower = max(fft_out(idx+1,:));
%         fft_out(idx+1,:) = fft_out(idx+1,:) / maxPower;
    end
    idx = idx + 1;
    tmp = fft(signal_in((idx*frame_shift+1):len), NFFT);
    fft_out(idx+1,:) = tmp(1:NFFT/2+1) .* conj(tmp(1:NFFT/2+1));
%     maxPower = max(fft_out(idx+1,:));
%     fft_out(idx+1,:) = fft_out(idx+1,:) / maxPower;
end

function fft_normal = fftNormalization(fft_in)
    fMax = max(max(fft_in));
    fft_normal = fft_in / fMax;
end

% function filter_out = filterBankTrapesium(fft_in, a, c, tsign)
%     filter = [0:1/c:1,ones(1,a-2),1:-1/c:0];
%     len_filter = a + c + c;
%     [row,col] = size(fft_in);
%     num_coef = ceil(col/a);
%     filter_out = zeros(row, num_coef);
%     
%     for idx_row=1:1:row
%         idx_col = 0;
%         filter_out(idx_row,idx_col+1) = ceil(max(filter(c+1:len_filter) .* fft_in(idx_row,idx_col*a+1:idx_col*a+len_filter-c)) - tsign);
%         for idx_col=1:1:num_coef-1
%             if idx_col*a-c+len_filter > col
%                 filter_out(idx_row,idx_col+1) = ceil(max(filter .* [fft_in(idx_row,idx_col*a+1-c:col),zeros(1,idx_col*a-c+len_filter-col)]) - tsign);
%             else
%                 filter_out(idx_row,idx_col+1) = ceil(max(filter .* fft_in(idx_row,idx_col*a+1-c:idx_col*a-c+len_filter)) - tsign);
%             end
%         end
%     end
% end

function filter_out = filterBankRectangle(fft_in, len_rect, tsign)
%     filter = ones(1,len_rect);
    [row,col] = size(fft_in);
    num_coef = ceil(col/len_rect);
    filter_out = zeros(row, num_coef);
    
    for idx_row=1:1:row
        for idx_col=0:1:num_coef-2
%             filter_out(idx_row,idx_col+1) = ceil(max(fft_in(idx_row,idx_col*len_rect+1:idx_col*len_rect+len_rect))-tsign);
              filter_out(idx_row,idx_col+1) = max(fft_in(idx_row,idx_col*len_rect+1:idx_col*len_rect+len_rect));
        end
        idx_col = idx_col + 1;
%         filter_out(idx_row,idx_col+1) = ceil(max(fft_in(idx_row,idx_col*len_rect+1:col))-tsign);
        filter_out(idx_row,idx_col+1) = max(fft_in(idx_row,idx_col*len_rect+1:col));
    end
end

function fitur_normal = fiturNormalization(filter_in)
%     filter_in = sum(filter_in);
    filter_in = sum(filter_in);
    maxFilter = max(filter_in);
    fitur_normal = filter_in / maxFilter;
%     fitur_normal = sign(filter_in);
%     fitur_normal=filter_in;
end

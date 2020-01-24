function [identifikasi akurasi] = ProcessTest(network, datatest, kelas, fiturPerOrang)

Y = sim(network,datatest);
disp(Y);
len_dttest=size(datatest);
akurasi=0;
identifikasi=0;

if len_dttest(2) > 1
    dist=0;
    correct=0;
    j=1;i=1;
    norm = round(Y);
    % tambahan
    hwb = waitbar (0, 'Proses Testing....', 'Name', 'Kelompok: Voice Recogintion');
    
%     while(i<length(kelas))
%         loop=length(kelas)/(length(norm)*2);
%         while(loop>0)
%         dist=sum((kelas(:,i)-norm(:,j)).^2)*0.5;
%         if dist < 1.4 %sqrt(2)
%             correct=correct+1;
%         end
%         loop=loop-1;
%         j=j+1;
%         waitbar (i/length(kelas), hwb);
%         end
%         i=i+fiturPerOrang;
%         waitbar (i/length(kelas), hwb);
%     end
%     close (hwb);
%     akurasi=(correct/(length(norm)))*100;
    j = length(Y);
    for i=j:-1:1
        if ceil(i/2) == bi2de(norm(:,i)')
            correct = correct + 1;
        end
        waitbar (i/j, hwb);
    end
    close(hwb);
    akurasi = (correct/j)*100;
else
%     mindist=100;
%     for i=1:length(kelas)
%         dist=sum((kelas(:,i)-Y(:,1)).^2)*0.5;
%         if dist < mindist
%             mindist=dist;
%             identifikasi=i;
%         end
%     end
%     identifikasi = ceil(identifikasi/fiturPerOrang);
    identifikasi = bi2de(round(Y'));
end
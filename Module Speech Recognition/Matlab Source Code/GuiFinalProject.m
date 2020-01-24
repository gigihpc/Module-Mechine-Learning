
function varargout = GuiFinalProject(varargin)
% GUIFINALPROJECT MATLAB code for GuiFinalProject.fig
%      GUIFINALPROJECT, by itself, creates a new GUIFINALPROJECT or raises the existing
%      singleton*.
%
%      H = GUIFINALPROJECT returns the handle to a new GUIFINALPROJECT or the handle to
%      the existing singleton*.
%
%      GUIFINALPROJECT('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in GUIFINALPROJECT.M with the given input arguments.
%
%      GUIFINALPROJECT('Property','Value',...) creates a new GUIFINALPROJECT or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before GuiFinalProject_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to GuiFinalProject_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help GuiFinalProject

% Last Modified by GUIDE v2.5 02-Jun-2014 16:18:05

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @GuiFinalProject_OpeningFcn, ...
                   'gui_OutputFcn',  @GuiFinalProject_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before GuiFinalProject is made visible.
function GuiFinalProject_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to GuiFinalProject (see VARARGIN)

% Choose default command line output for GuiFinalProject
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes GuiFinalProject wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = GuiFinalProject_OutputFcn(~, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes when figure1 is resized.
function figure1_ResizeFcn(hObject, eventdata, handles)
% hObject    handle to figure1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in loadFile.
function loadFile_Callback(hObject, eventdata, handles)
% hObject    handle to loadFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

status = get (handles.status, 'String');
% strcmp (status, 'TrainingAktif');

if strcmp(status,'TrainingAktif')

    set (handles.listboxfile, 'string','');
    
    [namafile, pathfile] = uigetfile({'*.wav'},'MultiSelect','on');
    set(handles.pathName, 'string', pathfile);
    set(handles.listboxfile, 'string', namafile);

    % loading data dari file 
    listFile = get(handles.listboxfile, 'String');
    nFile = length (listFile);

    if (strcmp(status,'TrainingAktif'))
        fid = fopen ('DataFiturTrain.txt', 'w');
        set (handles.btnTraining, 'Enable', 'on');
        set (handles.btnTesting, 'Enable', 'on');
    end
    
    if (strcmp(status,'TestingAktif'))
        fid = fopen ('DataFiturTest.txt', 'w');
        set (handles.btnTesting, 'Enable', 'on');
    end

    %listFile = get(handles.listboxfile, 'String');

    hwb = waitbar (0, 'Menghitung Fitur Ekstraksi...', 'Name', 'Kelompok: Voice Recogition');
    
    for m=1:nFile
        listFile = get(handles.listboxfile, 'String');
        path = get (handles.pathName,'string');
        itm = listFile{m};
        file = strcat(path,itm);
        [fft_out, fitur] = Fitur_Extraction(file);
        
        fprintf (fid, '%2.8f ', fitur);
        %if m ~= nFile
            fprintf (fid, '\n');
        %end
        waitbar (m/nFile, hwb);
    end
    close (hwb);

    fclose (fid);
    if (strcmp(status,'TrainingAktif'))
        load DataFiturTrain.txt;
        set (handles.tabelFitur, 'Data', DataFiturTrain)
    end
    
    if (strcmp(status,'TestingAktif'))
        load DataFiturTest.txt;
        set (handles.tabelFitur, 'Data', DataFiturTest)
    end

elseif strcmp(status,'TestingAktif')
    
    set (handles.listboxfile, 'string','');
    
    [namafile, pathfile] = uigetfile({'*.wav'},'MultiSelect','on');
    set(handles.pathName, 'string', pathfile);
    set(handles.listboxfile, 'string', namafile);

    % loading data dari file 
    listFile = get(handles.listboxfile, 'String');
    nFile = length (listFile);

    if (strcmp(status,'TrainingAktif'))
        fid = fopen ('DataFiturTrain.txt', 'w');
        set (handles.btnTraining, 'Enable', 'on');
        set (handles.btnTesting, 'Enable', 'on');
    end
    
    if (strcmp(status,'TestingAktif'))
        fid = fopen ('DataFiturTest.txt', 'w');
        set (handles.btnTesting, 'Enable', 'on');
    end

    %listFile = get(handles.listboxfile, 'String');

    hwb = waitbar (0, 'Menghitung Fitur Ekstraksi...', 'Name', 'Kelompok: Voice Recogition');
    
    for m=1:nFile
        listFile = get(handles.listboxfile, 'String');
        path = get (handles.pathName,'string');
        itm = listFile{m};
        file = strcat(path,itm);
        [fft_out, fitur] = Fitur_Extraction(file);
        
        fprintf (fid, '%2.8f ', fitur);
        %if m ~= nFile
            fprintf (fid, '\n');
        %end
        waitbar (m/nFile, hwb);
    end
    close (hwb);

    fclose (fid);
    if (strcmp(status,'TrainingAktif'))
        load DataFiturTrain.txt;
        set (handles.tabelFitur, 'Data', DataFiturTrain)
    end
    
    if (strcmp(status,'TestingAktif'))
        load DataFiturTest.txt;
        set (handles.tabelFitur, 'Data', DataFiturTest)
    end
else
    % di isi dengan nilai identifikasi
    [namafile, pathfile] = uigetfile({'*.wav'});
    set(handles.tesOrang, 'string', namafile);
    strFile = strcat (pathfile,namafile);
    [fft_out, fitur] = Fitur_Extraction(strFile);
    save DaftarFiturPerOrang.txt fitur -ascii; 
end

function soundFile_Callback(hObject, eventdata, handles)
% hObject    handle to soundFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of soundFile as text
%        str2double(get(hObject,'String')) returns contents of soundFile as a double


% --- Executes during object creation, after setting all properties.
function soundFile_CreateFcn(hObject, eventdata, handles)
% hObject    handle to soundFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in listboxfile.
function listboxfile_Callback(hObject, eventdata, handles)
% hObject    handle to listboxfile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns listboxfile contents as cell array
%        contents{get(hObject,'Value')} returns selected item from listboxfile
listData = get(handles.listboxfile,'Value');
stringData = get (handles.listboxfile,'String');
path = get(handles.pathName, 'string');
str = stringData{listData(1)};
strFile = strcat(path,str);

set (handles.namaFile, 'string', str);

[fft_out,fitur] = Fitur_Extraction(strFile);

[y fs] = wavread(strFile);
soundsc(y,fs); 

plt1 = handles.plotAxes1;
plot (plt1, wavread(strFile));
xlabel (plt1, 'Time');
ylabel (plt1, 'Amplitudo');
grid (plt1, 'on');

plt2 = handles.plotAxes2;
plot (plt2, fft_out);
xlabel (plt2, 'frekuensi');
ylabel (plt2, 'Magnitude');
grid (plt2, 'on');

%set (handles.tabelFitur, 'Data', fitur);

% --- Executes during object creation, after setting all properties.
function listboxfile_CreateFcn(hObject, eventdata, handles)
% hObject    handle to listboxfile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in listboxtemp.
function listboxtemp_Callback(hObject, eventdata, handles)
% hObject    handle to listboxtemp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns listboxtemp contents as cell array
%        contents{get(hObject,'Value')} returns selected item from listboxtemp


% --- Executes during object creation, after setting all properties.
function listboxtemp_CreateFcn(hObject, eventdata, handles)
% hObject    handle to listboxtemp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in tampilSeluruhFitur.
function tampilSeluruhFitur_Callback(hObject, eventdata, handles)
% hObject    handle to tampilSeluruhFitur (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of tampilSeluruhFitur


% --- Executes on button press in rbTesting.
function rbTesting_Callback(hObject, eventdata, handles)
% hObject    handle to rbTesting (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of rbTesting


% --- Executes on button press in rbTraining.
function rbTraining_Callback(hObject, eventdata, handles)
% hObject    handle to rbTraining (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of rbTraining


% --- Executes on button press in rbIdentifikasi.
function rbIdentifikasi_Callback(hObject, eventdata, handles)
% hObject    handle to rbIdentifikasi (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of rbIdentifikasi


% --- Executes when selected object is changed in groupRadio.
function groupRadio_SelectionChangeFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in groupRadio 
% eventdata  structure with the following fields (see UIBUTTONGROUP)
%	EventName: string 'SelectionChanged' (read only)
%	OldValue: handle of the previously selected object or empty if none was selected
%	NewValue: handle of the currently selected object
% handles    structure with handles and user data (see GUIDATA)
pilihObjek = get (handles.groupRadio, 'SelectedObject');

objTag = get (pilihObjek, 'Tag');
set (handles.loadFile, 'Enable', 'on');

switch objTag
    case 'rbTraining'
        set (handles.status, 'String', 'TrainingAktif');
    case 'rbTesting'
        set (handles.status, 'String', 'TestingAktif');
    case 'rbIden'
        set (handles.status, 'String', 'Identifikasi');    
end


% --- Executes on button press in btnTraining.
function btnTraining_Callback(hObject, eventdata, handles)
% hObject    handle to btnTraining (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
load DataFiturTrain.txt
jmlOrang = get(handles.jmlPerOrang, 'String');
jml = str2num(jmlOrang);
[net kelas]= ProcessIdentifikasi(DataFiturTrain, jml);
save kelas.txt kelas -ascii;
save t.mat net;

function jmlPerOrang_Callback(hObject, eventdata, handles)
% hObject    handle to jmlPerOrang (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of jmlPerOrang as text
%        str2double(get(hObject,'String')) returns contents of jmlPerOrang as a double


% --- Executes during object creation, after setting all properties.
function jmlPerOrang_CreateFcn(hObject, eventdata, handles)
% hObject    handle to jmlPerOrang (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in btnTesting.
function btnTesting_Callback(hObject, eventdata, handles)
% hObject    handle to btnTesting (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


load DataFiturTest.txt;
load DaftarFiturPerOrang.txt;
load t.mat;
load kelas.txt;
jmlOrang = get(handles.jmlPerOrang, 'String');
jml = str2num(jmlOrang);

cek = get(handles.cekOrang, 'Value');

if cek % jika cek satu orang
        % proses
    [identifikasi akurasi] = ProcessTest(net,DaftarFiturPerOrang',kelas,jml);
    str = sprintf('Orang ke-%d', identifikasi);
%     disp(identifikasi);
    set (handles.identifikasi, 'String', str);
else
    [identifikasi akurasi] = ProcessTest(net,DataFiturTest',kelas,jml);
    check = size(DataFiturTest');
    str = sprintf('%d%%', round(akurasi));
    set (handles.akurasi, 'String', str);
end
%disp(str);



function tesOrang_Callback(hObject, eventdata, handles)
% hObject    handle to tesOrang (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tesOrang as text
%        str2double(get(hObject,'String')) returns contents of tesOrang as a double


% --- Executes during object creation, after setting all properties.
function tesOrang_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tesOrang (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in cekOrang.
function cekOrang_Callback(hObject, eventdata, handles)
% hObject    handle to cekOrang (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of cekOrang

cek = get(handles.cekOrang, 'Value');

if cek 
    %set (handles.namaFile, 'String', 'ok');
    set (handles.btnTesting, 'Enable', 'on');
end

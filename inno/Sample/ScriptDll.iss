; -- ScriptDll.iss --
;
; This script shows how to call DLL functions at runtime from a [Code] section.

[Setup]
AppName=My Program
AppVerName=My Program version 1.5
DefaultDirName={pf}\My Program
DisableProgramGroupPage=yes

[Files]
Source: "MyProg.exe"; DestDir: "{app}"
Source: "MyProg.hlp"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme
Source: "MyDll.dll"; CopyMode: dontcopy

[Code]
const
  MB_ICONINFORMATION = $40;

procedure MyDllFunc(hWnd: Integer; lpText, lpCaption: String; uType: Cardinal);
external 'MyDllFunc@files:MyDll.dll stdcall';

function MessageBox(hWnd: Integer; lpText, lpCaption: String; uType: Cardinal): Integer;
external 'MessageBoxA@user32.dll stdcall';

function NextButtonClick(CurPage: Integer): Boolean;
var
  hWnd: Integer;
begin
  if CurPage = wpWelcome then begin
    hWnd := StrToInt(ExpandConstant('{wizardhwnd}'));
    MyDllFunc(hWnd, 'Hello from mydll.dll', 'MyDllFunc', MB_OK or MB_ICONINFORMATION);
    MessageBox(hWnd, 'Hello from user32.dll', 'MessageBoxA', MB_OK or MB_ICONINFORMATION);
  end;
  Result := True;
end;

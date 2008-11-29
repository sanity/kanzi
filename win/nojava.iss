; -- Sample2.iss --
; Same as Sample1.iss, but creates its icon in the Programs folder of the
; Start Menu instead of in a subfolder, and also creates a desktop icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=Kanzi
AppVersion = 1.0Beta
AppVerName=Kanzi - 1.0
WizardImageFile=kanzipan.bmp
DefaultDirName={pf}\Kanzi-1.0
DefaultGroupName=Kanzi
; ^ since no icons will be created in "{group}", we don't need the wizard
;   to ask for a group name.
UninstallDisplayIcon={app}\MyProg.exe

[Files]
Source: "..\dist\msmd.jar"; DestDir: "{app}"
Source: "..\dist\lib\Tidy.jar"; DestDir: "{app}\lib"
Source: "..\dist\lib\xerces.jar"; DestDir: "{app}\lib"
Source: "..\dist\lib\kunststoff.jar"; DestDir: "{app}\lib"
Source: "kanzi.ico"; DestDir: "{app}"

[Icons]
Name: "{group}\Kanzi"; Filename: "{app}\msmd.jar"; WorkingDir: "{app}"; IconFilename: "{app}\kanzi.ico"
Name: "{userdesktop}\Kanzi"; Filename: "{app}\msmd.jar"; WorkingDir: "{app}"; IconFilename: "{app}\kanzi.ico"
Name: "{group}\Remove Kanzi"; Filename: "{uninstallexe}"

[Code]
// Source: "http://www.newatlanta.com/partners/esri/faq.html";
function InitializeSetup(): Boolean;
  var CurrentVersion, JREPath, EnvJavaHome, SubKey, cVNCurrentVersion,
cVNJavaHome, cEnvJavaHome, Version1, Version2, Res: String;
    Root, P1, P2, V1, V2, R1, R2: Integer;
begin
  Result := False;
  Root := HKEY_LOCAL_MACHINE;
  SubKey := 'SOFTWARE\JavaSoft\Java Runtime Environment';
  cVNCurrentVersion := 'CurrentVersion';
  cVNJavaHome := 'JavaHome';
  cEnvJavaHome := 'JAVA_HOME';
  if RegQueryStringValue(Root, SubKey, cVNCurrentVersion, CurrentVersion)
then begin
    Version1 := CurrentVersion;
    Version2 := '1.2';
    P1 := Pos('.', Version1);
    P2 := Pos('.', Version2);
    V1 := StrToInt(Copy(Version1, 1, P1 - 1));
    R1 := StrToInt(Copy(Version1, P1 + 1, Length(Version1)));
    V2 := StrToInt(Copy(Version2, 1, P2 - 1));
    R2 := StrToint(Copy(Version2, P2 + 1, Length(Version2)));
    if (V1 > V2) or ((V1 = V2) and (R1 >= R2)) then begin
      if RegQueryStringValue(Root, SubKey + '\' + CurrentVersion, cVNJavaHome, JREPath) then begin
        EnvJavaHome := GetEnv(cEnvJavaHome);
        if (EnvJavaHome = '') or (EnvJavaHome = JREPath) then Result := True;
      end;
    end;
  end;
  if not Result then begin
    MsgBox('You need Java version 1.2 or higher.'#13#10 +
           'You can obtain it from http://java.sun.com/getjava/', mbError, MB_OK);
  end else
   if RegQueryStringValue(Root, 'SOFTWARE\CLASSES\.jar','', Res) then begin
    if not (Res = 'jarfile') then begin
       if MsgBox('CheckJava: Some other application has registered the .jar extension '+
              'with itself, do you wish to reclaim it for Java applications?  If not,'+
              'you must manual restore the association or Kanzi will not run.',
              mbError, MB_YESNO) = idYes then
          RegWriteStringValue(HKLM, 'SOFTWARE\CLASSES\.jar','','jarfile');
    end;
   end else begin
      MsgBox('Cannot find Software\CLASSES\.jar entry in registry, please (re)install Java', mbError, MB_OK);
   end;
end;

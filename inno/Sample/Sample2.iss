; -- Sample2.iss --
; Same as Sample1.iss, but creates its icon in the Programs folder of the
; Start Menu instead of in a subfolder, and also creates a desktop icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=My Program
AppVerName=My Program version 1.5
DefaultDirName={pf}\My Program
DisableProgramGroupPage=yes
; ^ since no icons will be created in "{group}", we don't need the wizard
;   to ask for a group name.
UninstallDisplayIcon={app}\MyProg.exe

[Files]
Source: "MyProg.exe"; DestDir: "{app}"
Source: "MyProg.hlp"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Icons]
Name: "{commonprograms}\My Program"; Filename: "{app}\MyProg.exe"
Name: "{userdesktop}\My Program"; Filename: "{app}\MyProg.exe"

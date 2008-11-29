;This script is used to create the My Inno Setup Extensions installer.
;It is intended as a sample script for some of the new features and will
;probably not compile on your system because of missing files.

[Setup]
AppName=My Inno Setup Extensions 3
AppVerName=My Inno Setup Extensions 3.0.2-beta
AppVersion=3.0.2-beta
AppMutex=MyInnoSetupExtensionsCompilerAppMutex,Global\MyInnoSetupExtensionsCompilerAppMutex
AppPublisher=Martijn Laan
AppPublisherURL=http://www.wintax.nl/isx
AppSupportURL=http://www.wintax.nl/isx
AppUpdatesURL=http://www.wintax.nl/isx
AppCopyright=My Inno Setup Extensions, copyright © 2000-2002 Martijn Laan
DefaultDirName={pf}\My Inno Setup Extensions 3
DefaultGroupName=My Inno Setup Extensions 3
Compression=bzip/9
OutputBaseFilename=Isxsetup
OutputDir=d:\www\www.wintax.nl\isx\new
UninstallDisplayIcon={app}\compil32.exe
AllowNoIcons=yes
LicenseFile=license.txt

[Messages]
DiskSpaceMBLabel=The program requires at least [kb] KB of disk space.
ComponentsDiskSpaceMBLabel=Current selection requires at least [kb] KB of disk space.

[Types]
Name: full; Description: Full installation
Name: compact; Description: Compact installation
Name: custom; Description: Custom installation; Flags: iscustom

[Components]
Name: main; Description: My Inno Setup Extensions application files; Types: full compact custom; Flags: fixed
Name: docs; Description: My Inno Setup Extensions documentation; Types: full compact
Name: sample; Description: My Inno Setup Extensions sample scripts; Types: full compact

[Tasks]
Name: desktopicon; Description: "Create a &desktop icon"; GroupDescription: "Additional icons:"; Components: main
Name: quicklaunchicon; Description: "Create a &Quick Launch icon"; GroupDescription: "Additional icons:"; Components: main; Flags: unchecked
Name: fileassoc; Description: "&Associate My Inno Setup Extensions with the .iss file extension"; GroupDescription: "Other tasks:"; Flags: unchecked

[Files]
Source: Compil32.exe; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: ISCC.exe; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: Iscmplr.dll; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: Wiz*.bmp; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: Default*.isl; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: isbzip.dll; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: isbunzip.dll; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: SetupLdr.e32; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: Uninst.e32; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: Regsvr.e32; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: Setup.e32; DestDir: {app}; CopyMode: alwaysoverwrite; Components: main
Source: isetup.hlp; DestDir: {app}; CopyMode: alwaysoverwrite; Components: docs
Source: isetup.cnt; DestDir: {app}; CopyMode: alwaysoverwrite; Components: docs
Source: isfaq.htm; DestDir: {app}; CopyMode: alwaysoverwrite; Components: docs
Source: Samples\*.*; DestDir: {app}\Sample; CopyMode: alwaysoverwrite; Components: sample; Flags: recursesubdirs
Source: Setup.iss; DestDir: {app}\Sample; CopyMode: alwaysoverwrite; Components: sample
Source: whatsnew.htm; DestDir: {app}; CopyMode: alwaysoverwrite
Source: license.txt; DestDir: {app}; CopyMode: alwaysoverwrite

[Registry]
Root: HKCU; Subkey: "Software\Martijn Laan"; Flags: uninsdeletekeyifempty
Root: HKCU; Subkey: "Software\Martijn Laan\My Inno Setup Extensions"; Flags: uninsdeletekey
Root: HKCU; Subkey: "Software\Martijn Laan\My Inno Setup Extensions\ScriptFileHistoryNew"; ValueType: string; ValueName: "History0"; ValueData: "{app}\Sample\Sample1.iss"; Flags: createvalueifdoesntexist
Root: HKCU; Subkey: "Software\Martijn Laan\My Inno Setup Extensions\ScriptFileHistoryNew"; ValueType: string; ValueName: "History1"; ValueData: "{app}\Sample\ScriptSample1.iss"; Flags: createvalueifdoesntexist
Root: HKCU; Subkey: "Software\Martijn Laan\My Inno Setup Extensions\ScriptFileHistoryNew"; ValueType: string; ValueName: "History2"; ValueData: "{app}\Sample\ScriptDlg.iss"; Flags: createvalueifdoesntexist
Root: HKCU; Subkey: "Software\Martijn Laan\My Inno Setup Extensions\ScriptFileHistoryNew"; ValueType: string; ValueName: "History3"; ValueData: "{app}\Sample\ScriptDll.iss"; Flags: createvalueifdoesntexist

[Icons]
Name: {group}\Inno Setup with My Inno Setup Extensions; Filename: {app}\Compil32.exe; Components: main
Name: {group}\My Inno Setup Extensions documentation; Filename: {app}\ISetup.hlp; Components: docs
Name: {group}\My Inno Setup Extensions sample script; Filename: {app}\Compil32.exe; Parameters: """{app}\Sample\Setup.iss"""; IconFilename: {app}\Compil32.exe; IconIndex: 1; Components: sample
Name: {userdesktop}\Inno Setup with My Inno Setup Extensions; Filename: {app}\Compil32.exe; Components: main; Tasks: desktopicon
Name: {userappdata}\Microsoft\Internet Explorer\Quick Launch\Inno Setup with My Inno Setup Extensions; Filename: {app}\Compil32.exe; Components: main; Tasks: quicklaunchicon

[Run]
Filename: {app}\Compil32.exe; Parameters: "/ASSOC"; StatusMsg: "Associating My Inno Setup Extensions with the .iss file extension..."; Tasks: fileassoc
Filename: {app}\Compil32.exe; Description: Launch the My Inno Setup Extensions compiler; Flags: nowait postinstall skipifsilent

[UninstallRun]
Filename: {app}\Compil32.exe; Parameters: "/UNASSOC"; RunOnceId: "RemoveISSAssoc"

[InstallDelete]
;Obsolete files
Type: files; Name: {app}\SetupModern.e32
Type: files; Name: {app}\SetupModernDebug.e32
Type: files; Name: {app}\SetupClassic.e32
Type: files; Name: {app}\WizImage.bmp
Type: files; Name: {app}\WizClassicImage.bmp
Type: files; Name: {app}\isxhowto.htm
Type: files; Name: {app}\isxscripthowto.htm
Type: files; Name: {app}\isx.gif
Type: files; Name: {app}\Sample\ScriptDebug.iss
Type: files; Name: {app}\Sample\Sample1.ifs

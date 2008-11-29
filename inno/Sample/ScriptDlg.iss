; -- ScriptDlg.iss --
;
; This script shows how to insert custom wizard pages into Setup and how to handle
; navigation between those pages if multiple custom pages are inserted after each
; other. Furthermore it shows how to 'communicate' between the [Code] section and
; the regular Inno Setup sections using {code:...} constants and finally it shows
; how to customize the settings text on the 'Ready To Install' page.

[Setup]
AppName=My Program
AppVerName=My Program version 1.5
DefaultDirName={pf}\My Program
DisableProgramGroupPage=yes

[Files]
Source: "MyProg.exe"; DestDir: "{app}"
Source: "MyProg.hlp"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Registry]
Root: HKCU; Subkey: "Software\My Company"; Flags: uninsdeletekeyifempty
Root: HKCU; Subkey: "Software\My Company\My Program"; Flags: uninsdeletekey
Root: HKCU; Subkey: "Software\My Company\My Program\Settings"; ValueType: string; ValueName: "Name"; ValueData: "{code:GetUser|Name}"
Root: HKCU; Subkey: "Software\My Company\My Program\Settings"; ValueType: string; ValueName: "Company"; ValueData: "{code:GetUser|Company}"
Root: HKCU; Subkey: "Software\My Company\My Program\Settings"; ValueType: string; ValueName: "DataDir"; ValueData: "{code:GetDataDir}"
; etc.

[Dirs]
Name: {code:GetDataDir}; Flags: uninsneveruninstall

[Code]
var
  UserPrompts, UserValues: TArrayOfString;
  UsagePrompts, UsageValues: TArrayOfString;
  Key: String;
  DataDir: String;
  
function InitializeSetup(): Boolean;
begin
  { Set prompts used on custom wizard pages }
  SetArrayLength(UserPrompts, 2);
  UserPrompts[0] := 'Name:';
  UserPrompts[1] := 'Company:';

  SetArrayLength(UsagePrompts, 3)
  UsagePrompts[0] := 'Light mode (no ads, limited functionality)';
  UsagePrompts[1] := 'Sponsored mode (with ads, full functionality)';
  UsagePrompts[2] := 'Paid mode (no ads, full functionality)';

  { Set default values }
  SetArrayLength(UserValues, 2);
  RegQueryStringValue(HKLM, 'Software\Microsoft\Windows\CurrentVersion', 'RegisteredOwner', UserValues[0]);
  RegQueryStringValue(HKLM, 'Software\Microsoft\Windows\CurrentVersion', 'RegisteredOrganization', UserValues[1]);
  if (UserValues[0] = '') and (UserValues[1] = '') then begin
    RegQueryStringValue(HKCU, 'Software\Microsoft\MS Setup (ACME)\User Info', 'DefName', UserValues[0]);
    RegQueryStringValue(HKCU, 'Software\Microsoft\MS Setup (ACME)\User Info', 'DefCompany', UserValues[1]);
  end;

  SetArrayLength(UsageValues, 3)
  UsageValues[1] := '1';

  { Try to find the settings that were stored last time (also see below). }
  UserValues[0] := GetPreviousData('Name', UserValues[0]);
  UserValues[1] := GetPreviousData('Company', UserValues[1]);
  UsageValues[0] := GetPreviousData('Light mode', UsageValues[0]);
  UsageValues[1] := GetPreviousData('Sponsored mode', UsageValues[1]);
  UsageValues[2] := GetPreviousData('Paid mode', UsageValues[2]);
  DataDir := GetPreviousData('DataDir', '');

  { Let Setup run }
  Result := True;
end;

procedure RegisterPreviousData(PreviousDataKey: Integer);
begin
  { Store the settings so we can restore them next time }
  SetPreviousData(PreviousDataKey, 'Name', UserValues[0]);
  SetPreviousData(PreviousDataKey, 'Company', UserValues[1]);
  SetPreviousData(PreviousDataKey, 'Light mode', UsageValues[0]);
  SetPreviousData(PreviousDataKey, 'Sponsored mode', UsageValues[1]);
  SetPreviousData(PreviousDataKey, 'Paid mode', UsageValues[2]);
  SetPreviousData(PreviousDataKey, 'DataDir', DataDir);
end;

function ScriptDlgPages(CurPage: Integer; BackClicked: Boolean): Boolean;
var
  I, CurSubPage: Integer;
  Next, NextOk: Boolean;
begin
  if (not BackClicked and (CurPage = wpSelectDir)) or (BackClicked and (CurPage = wpReady)) then begin
    { Insert a custom wizard page between two non custom pages }
    { First open the custom wizard page }
    ScriptDlgPageOpen();
    { Set some captions }
    ScriptDlgPageSetCaption('Select Personal Data Directory');
    ScriptDlgPageSetSubCaption1('Where should personal data files be installed?');
    ScriptDlgPageSetSubCaption2('Select the folder you would like Setup to install personal data files to, then click Next.');
    { Initialize the DataDir if necessary }
    if DataDir = '' then
      DataDir := 'C:\'+UserValues[0];
    { Ask for a dir until the user has entered one or click Back or Cancel }
    Next := InputDir(UserValues[0], DataDir);
    while Next and (DataDir = '') do begin
      MsgBox(SetupMessage(msgInvalidPath), mbError, MB_OK);
      Next := InputDir(UserValues[0], DataDir);
    end;
    { See NextButtonClick and BackButtonClick: return True if the click should be allowed }
    if not BackClicked then
      Result := Next
    else
      Result := not Next;
    { Close the wizard page. Do a FullRestore only if the click (see above) is not allowed }
    ScriptDlgPageClose(not Result);
  end else if (not BackClicked and (CurPage = wpWelcome)) or (BackClicked and (CurPage = wpSelectDir)) then begin
    { Insert multiple custom wizard page between two non custom pages }
    { Now we must handle navigation between the custom pages ourselves }
    { First find out on which page we should start }
    if not BackClicked then
      CurSubPage := 0
    else
      CurSubPage := 2;
    { Then open the custom wizard page }
    ScriptDlgPageOpen();
    { Set the main caption }
    ScriptDlgPageSetCaption('Personal Information');
    { Loop while we are still on a custom page and Setup has not been terminated }
    while (CurSubPage >= 0) and (CurSubPage <= 2) and not Terminated do begin
      case CurSubPage of
        0:
          begin
            { First ask for some user info }
            ScriptDlgPageSetSubCaption1('Who are you?');
            ScriptDlgPageSetSubCaption2('Please specify your name and the company for whom you work, then click Next.');
            Next := InputQueryArray(UserPrompts, UserValues);
            if Next then begin
              NextOk := UserValues[0] <> '';
              if not NextOk then
                MsgBox('You must enter your name.', mbError, MB_OK);
            end;
          end;
        1:
          begin
            { Then ask for the usage mode }
            ScriptDlgPageSetSubCaption1('How will you use My Progam?');
            ScriptDlgPageSetSubCaption2('Please specify how you would like to use My Program, then click Next.');
            Next := InputOptionArray(UsagePrompts, UsageValues, True, False);
            NextOk := True;
          end;
        2:
          begin
            if UsageValues[0] = '1' then begin
              { Show a message if 'light mode' was chosen above }
              { Skip this page when the user just clicked the Back button }
              ScriptDlgPageSetSubCaption1('How will you use My Progam?');
              Next := OutputMsg('Note: to enjoy all features My Program can offer and to support its development, you can switch to sponsored or paid mode at any time by selecting ''Usage Mode'' in the ''Help'' menu of My Program after the installation has completed.'#13#13'Click Back if you want to change your usage mode setting now, or click Next to continue with the installation.', True);
              NextOk := True;
            end else if UsageValues[2] = '1' then begin
              { Ask for a registration key if 'paid mode' was chosen above }
              { This demo accepts only one key: 'isx' (without quotes) }
              ScriptDlgPageSetSubCaption1('What''s your registration key?');
              ScriptDlgPageSetSubCaption2('Please specify your registration key, ' + UserValues[0] + '. Click Next to continue. If you don''t have a valid registration key, click Back to choose a different usage mode.');
              Next := InputQuery('Registration key:', Key);
              if Next then begin
                { Just to show how OutputProgress works }
                for I := 0 to 10 do begin
                  OutputProgress('Authorizing registration key...', '', I, 10);
                  Sleep(100);
                  if Terminated then
                    Break;
                end;
                NextOk := Key = 'isx';
                if not NextOk and not Terminated then
                  MsgBox('You must enter a valid registration key.', mbError, MB_OK);
              end;
            end;
          end;
      end;
      if Next then begin
        { Go to the next page, but only if the user entered correct information }
        if NextOk then
          CurSubPage := CurSubPage + 1;
      end else
        CurSubPage := CurSubPage - 1;
    end;
    { See NextButtonClick and BackButtonClick: return True if the click should be allowed }
    if not BackClicked then
      Result := Next
    else
      Result := not Next;
    { Close the wizard page. Do a FullRestore only if the click (see above) is not allowed }
    ScriptDlgPageClose(not Result);
  end else begin
    Result := True;
  end;
end;

function NextButtonClick(CurPage: Integer): Boolean;
begin
  Result := ScriptDlgPages(CurPage, False);
end;

function BackButtonClick(CurPage: Integer): Boolean;
begin
  Result := ScriptDlgPages(CurPage, True);
end;

function UpdateReadyMemo(Space, NewLine, MemoUserInfoInfo, MemoDirInfo, MemoTypeInfo, MemoComponentsInfo, MemoGroupInfo, MemoTasksInfo: String): String;
var
  S: String;
begin
  { Fill the 'Ready Memo' with the normal settings and the custom settings }
  S := '';
  S := S + 'Personal Information:' + NewLine;
  S := S + Space + UserValues[0] + NewLine;
  if UserValues[1] <> '' then
    S := S + Space + UserValues[1] + NewLine;
  S := S + NewLine;
  
  S := S + 'Usage Mode:' + NewLine;
  if UsageValues[0] = '1' then
    S := S + Space + UsagePrompts[0] + NewLine
  else if UsageValues[1] = '1' then
    S := S + Space + UsagePrompts[1] + NewLine
  else
    S := S + Space + UsagePrompts[2] + NewLine;
  S := S + NewLine;
  
  S := S + MemoDirInfo + NewLine;
  S := S + Space + DataDir + ' (personal data files)' + NewLine;

  Result := S;
end;

function GetUser(S: String): String;
begin
  { Return a user value }
  { Could also be splitted into separate GetUserName and GetUserCompany functions }
  if S = 'Name' then
    Result := UserValues[0]
  else if S = 'Company' then
    Result := UserValues[1];
end;

function GetDataDir(S: String): String;
begin
  { Return the selected DataDir }
  Result := DataDir;
end;

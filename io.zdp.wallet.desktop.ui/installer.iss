; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=ZDP Wallet
AppVersion=1.0
DefaultDirName={pf}\ZDP
DefaultGroupName=ZDP
UninstallDisplayIcon={app}\zdp.exe
Compression=lzma2
LicenseFile="LICENSE"
SolidCompression=no
OutputBaseFilename="zdp-wallet-installer-1.0"

[Files]
Source: "zdp.exe"; DestDir: "{app}"
Source: "jre\**"; DestDir: "{app}\jre"; Flags: recursesubdirs
Source: "LICENSE"; DestDir: "{app}"


[Icons]
Name: "{group}\ZDP"; Filename: "{app}\zdp.exe"; WorkingDir: "{app}"
Name: "{group}\Uninstall"; Filename: "{uninstallexe}"
Name: "{commondesktop}\ZDP"; Filename: "{app}\zdp.exe"
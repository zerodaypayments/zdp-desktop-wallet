;--------------------------------
;Include Modern UI

	!include "MUI2.nsh"
  

	!define MUI_ICON "C:\zdp\github\zdp-desktop-wallet\io.zdp.wallet.desktop.ui\dist\zdp.ico"

;--------------------------------
;General

  ;Name and file
  Name "ZDP Wallet"
  OutFile "zdp-wallet-installer.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES64\ZDP\ZDP Wallet"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\ZDP\ZDP Wallet" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

	!insertmacro MUI_PAGE_LICENSE "C:\zdp\github\zdp-desktop-wallet\io.zdp.wallet.desktop.ui\dist\license.txt"
	!insertmacro MUI_PAGE_DIRECTORY
	!insertmacro MUI_PAGE_INSTFILES
  
    # These indented statements modify settings for MUI_PAGE_FINISH
    !define MUI_FINISHPAGE_NOAUTOCLOSE
    !define MUI_FINISHPAGE_RUN
    !define MUI_FINISHPAGE_RUN_CHECKED
    !define MUI_FINISHPAGE_RUN_TEXT "Start ZDP Wallet"
    !define MUI_FINISHPAGE_RUN_FUNCTION "LaunchLink"
	!insertmacro MUI_PAGE_FINISH  
  
	!insertmacro MUI_UNPAGE_CONFIRM
	!insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
	File "zdp.exe"
	File "splash.bmp"
	File "zdp.ico"
	File "license.txt"
	File /r "jre"

  ;Store installation folder
  WriteRegStr HKCU "Software\ZDP\ZDP Wallet" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
;Descriptions

	;Language strings
	LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

	;Assign language strings to sections
	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
	!insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...


	Delete "$INSTDIR\zdp.exe"
	Delete "$INSTDIR\splash.bmp"
	Delete "$INSTDIR\license.txt"
	Delete "$INSTDIR\zdp.ico"
	RMDir /r  "$INSTDIR\jre" 
	
	Delete "$INSTDIR\Uninstall.exe"
	RMDir  "$INSTDIR"
    
	DeleteRegKey /ifempty HKCU "Software\ZDP\ZDP Wallet"

SectionEnd

Function LaunchLink
	ExecShell "open" "$INSTDIR\zdp.exe"
FunctionEnd
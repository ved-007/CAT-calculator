Set WshShell = CreateObject("WScript.Shell")
strCurrentDir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
exePath = strCurrentDir & "\dist\CAT-Calculator\CAT-Calculator.exe"

Dim fso
Set fso = CreateObject("Scripting.FileSystemObject")
If fso.FileExists(exePath) Then
    WshShell.Run """" & exePath & """", 0, False
Else
    WshShell.Run "javaw -jar """ & strCurrentDir & "\CatCalculator.jar""", 0, False
End If

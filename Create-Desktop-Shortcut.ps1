$WshShell = New-Object -ComObject WScript.Shell
$DesktopPath = [Environment]::GetFolderPath("Desktop")
$ShortcutPath = Join-Path $DesktopPath "CAT Calculator.lnk"

$TargetExe = Join-Path $PSScriptRoot "dist\CAT-Calculator\CAT-Calculator.exe"
if (!(Test-Path $TargetExe)) {
    $TargetExe = Join-Path $PSScriptRoot "Launch-CatCalculator.bat"
}

$Shortcut = $WshShell.CreateShortcut($ShortcutPath)
$Shortcut.TargetPath = $TargetExe
$Shortcut.WorkingDirectory = $PSScriptRoot
$Shortcut.Description = "CAT Exam On-Screen Calculator"
$Shortcut.Save()

Write-Host "Desktop shortcut created successfully at: $ShortcutPath"

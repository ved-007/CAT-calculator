@echo off
if exist "%~dp0dist\CAT-Calculator\CAT-Calculator.exe" (
    start "" "%~dp0dist\CAT-Calculator\CAT-Calculator.exe"
) else (
    start "" javaw -jar "%~dp0CatCalculator.jar"
)
exit

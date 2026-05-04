@echo off
chcp 65001 >nul

set SCRIPT_DIR=%~dp0
set SRC=%SCRIPT_DIR%src
set OUT=%SCRIPT_DIR%out
set LIB=%SCRIPT_DIR%lib

if not exist "%OUT%\employeedb\Main.class" (
    echo Kompiluji zdrojove soubory...
    if not exist "%OUT%" mkdir "%OUT%"
    dir /s /b "%SRC%\*.java" > "%TEMP%\sources.txt"
    if exist "%LIB%\*.jar" (
        javac --release 17 -cp "%LIB%\*" -d "%OUT%" @"%TEMP%\sources.txt"
    ) else (
        javac --release 17 -d "%OUT%" @"%TEMP%\sources.txt"
    )
    echo Kompilace dokoncena.
)

if exist "%LIB%\*.jar" (
    java -cp "%OUT%;%LIB%\*" -Dfile.encoding=UTF-8 employeedb.Main
) else (
    java -cp "%OUT%" -Dfile.encoding=UTF-8 employeedb.Main
)
pause

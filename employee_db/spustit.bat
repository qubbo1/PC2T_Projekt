@echo off
chcp 65001 >nul
REM Spuštění aplikace Databázový systém zaměstnanců
REM Vyžaduje Java 17+

set SCRIPT_DIR=%~dp0
set SRC=%SCRIPT_DIR%src
set OUT=%SCRIPT_DIR%out
set LIB=%SCRIPT_DIR%lib

REM Kompilace pokud chybí
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

REM Spuštění
if exist "%LIB%\*.jar" (
    java -cp "%OUT%;%LIB%\*" -Dfile.encoding=UTF-8 employeedb.Main
) else (
    java -cp "%OUT%" -Dfile.encoding=UTF-8 employeedb.Main
)
pause

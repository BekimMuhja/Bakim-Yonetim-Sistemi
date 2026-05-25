@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set JAVAC=C:\Program Files\Android\Android Studio1\jbr\bin\javac.exe
set JAVA=C:\Program Files\Android\Android Studio1\jbr\bin\java.exe
set BASE=%~dp0
set JAR=%BASE%lib\junit-platform-console-standalone-1.10.2.jar

echo === Derleniyor ===
set SOURCES=
for %%f in ("%BASE%src\*.java") do set SOURCES=!SOURCES! "%%f"
for %%f in ("%BASE%test\*.java") do set SOURCES=!SOURCES! "%%f"
"%JAVAC%" -encoding UTF-8 -cp "%JAR%" -d "%BASE%bin" !SOURCES!

if %ERRORLEVEL% neq 0 (
    echo.
    echo HATA: Derleme basarisiz!
    pause
    exit /b 1
)

echo.
echo === Testler calistiriliyor ===
echo.
"%JAVA%" -jar "%JAR%" execute --class-path "%BASE%bin" --select-class=KismiTamamlamaVeEksikAtamaTest

pause
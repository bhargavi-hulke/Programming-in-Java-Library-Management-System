@echo off
REM ========================================================
REM VITyarthi - Smart Library Management System Runner
REM ========================================================

echo [1/2] Compiling Java source files...
if not exist "bin" mkdir bin
javac -d bin src\model\*.java src\exception\*.java src\storage\*.java src\service\*.java src\app\*.java src\test\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please check Java installation.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Starting Library Management Application...
echo.
java -cp bin app.LibraryApp
pause

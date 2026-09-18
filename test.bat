@echo off
REM ========================================================
REM VITyarthi - Smart Library Management System Test Runner
REM ========================================================

echo Compiling test and source classes...
if not exist "bin" mkdir bin
javac -d bin src\model\*.java src\exception\*.java src\storage\*.java src\service\*.java src\app\*.java src\test\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Executing automated validation test suite...
echo.
java -cp bin test.LibrarySystemTest
pause

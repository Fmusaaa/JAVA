@echo off
REM ===================================================================
REM Telegram Task Manager Bot - Environment Setup Script
REM Run this as Administrator to set environment variables
REM ===================================================================

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   Telegram Task Manager Bot - Setup Script                     ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Check if running as Administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo ERROR: This script must be run as Administrator!
    echo.
    echo Instructions:
    echo 1. Right-click this script file (setup.bat)
    echo 2. Select "Run as Administrator"
    echo 3. Click "Yes" on the permission prompt
    echo.
    pause
    exit /b 1
)

echo Setting up environment variables...
echo.

REM Detect Java installation
echo Detecting Java 11 installation...
if exist "C:\Program Files\Java\jdk-11" (
    setx JAVA_HOME "C:\Program Files\Java\jdk-11"
    echo ✓ Found Java 11 at C:\Program Files\Java\jdk-11
) else if exist "C:\Program Files\Java\jdk-11.0.1" (
    setx JAVA_HOME "C:\Program Files\Java\jdk-11.0.1"
    echo ✓ Found Java 11 at C:\Program Files\Java\jdk-11.0.1
) else (
    echo ⚠ Java 11 not found in default location
    echo Please install Java 11 first or set JAVA_HOME manually
)

echo.
echo Detecting Maven installation...
if exist "C:\Maven\apache-maven-3.8" (
    setx MAVEN_HOME "C:\Maven\apache-maven-3.8"
    echo ✓ Found Maven at C:\Maven\apache-maven-3.8
) else if exist "C:\Maven" (
    setx MAVEN_HOME "C:\Maven"
    echo ✓ Found Maven at C:\Maven
) else (
    echo ⚠ Maven not found in default location
    echo Please extract Maven to C:\Maven\ or set MAVEN_HOME manually
)

echo.
echo Setting up PATH...
REM Add Maven to PATH if not already there
for /f "tokens=*" %%A in ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path ^| find "Path"') do set "PATH=%%A"
echo %PATH% | find /i "maven" >nul
if errorlevel 1 (
    setx PATH "%PATH%;%%MAVEN_HOME%%\bin"
    echo ✓ Added Maven\bin to PATH
) else (
    echo ✓ Maven already in PATH
)

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   Telegram Bot Credentials Setup                               ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

set /p BOT_USERNAME="Enter your Telegram bot username (without @): "
set /p BOT_TOKEN="Enter your Telegram bot token from @BotFather: "

if "%BOT_USERNAME%"=="" (
    echo ERROR: Bot username cannot be empty!
    pause
    exit /b 1
)

if "%BOT_TOKEN%"=="" (
    echo ERROR: Bot token cannot be empty!
    pause
    exit /b 1
)

echo.
echo Setting environment variables for bot...
setx BOT_USERNAME "%BOT_USERNAME%"
setx BOT_TOKEN "%BOT_TOKEN%"

echo ✓ BOT_USERNAME = %BOT_USERNAME%
echo ✓ BOT_TOKEN = %BOT_TOKEN% (truncated for security)

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   ✅ Setup Complete!                                            ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo Next steps:
echo 1. Close this window and any open Command Prompts
echo 2. Open a new Command Prompt
echo 3. Navigate to: c:\Users\F MUSA\PBO\LATIHA
echo 4. Run: mvn clean install
echo 5. Run: mvn exec:java -Dexec.mainClass="com.taskbot.TaskBotApplication"
echo.

echo Verification commands:
echo - Check Java: java -version
echo - Check Maven: mvn -version
echo - Check environment variables: set | findstr /i "JAVA MAVEN BOT"
echo.

pause

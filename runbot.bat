@echo off
REM ===================================================================
REM Telegram Task Manager Bot - Run Script
REM ===================================================================

cls
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   Telegram Task Manager Bot - Starting                         ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Check if environment variables are set
if "%BOT_USERNAME%"=="" (
    echo ERROR: BOT_USERNAME environment variable not set!
    echo.
    echo Please follow setup instructions:
    echo 1. Run setup.bat with Administrator privileges
    echo 2. Or manually set environment variables:
    echo    - Open System Properties ^> Environment Variables
    echo    - Add BOT_USERNAME and BOT_TOKEN
    echo.
    pause
    exit /b 1
)

if "%BOT_TOKEN%"=="" (
    echo ERROR: BOT_TOKEN environment variable not set!
    echo.
    echo Please run setup.bat with Administrator privileges
    echo.
    pause
    exit /b 1
)

echo Checking Java installation...
java -version >nul 2>&1
if errorLevel 1 (
    echo ERROR: Java not found!
    echo Please install Java 11 first
    pause
    exit /b 1
)
echo ✓ Java found

echo.
echo Checking Maven installation...
mvn -version >nul 2>&1
if errorLevel 1 (
    echo ERROR: Maven not found!
    echo Please install Maven and set MAVEN_HOME
    pause
    exit /b 1
)
echo ✓ Maven found

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   Bot Configuration                                            ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
echo Bot Username: %BOT_USERNAME%
echo Bot Token: [HIDDEN for security]
echo.

echo Starting bot...
echo.
echo ────────────────────────────────────────────────────────────────
echo Press Ctrl+C to stop the bot
echo ────────────────────────────────────────────────────────────────
echo.

REM Run the bot
mvn exec:java -Dexec.mainClass="com.taskbot.TaskBotApplication"

if errorLevel 1 (
    echo.
    echo ❌ Bot crashed or exited with error
    echo.
    echo Troubleshooting:
    echo - Check if BOT_TOKEN is valid at @BotFather
    echo - Check internet connection
    echo - Check if port/resources are available
    echo.
    pause
    exit /b 1
)

pause

@echo off

set BASE_DIR=%KAFKA_HOME%

rem 检查是否传入了参数
if "%1"=="" (
    echo USAGE: %0 [start/stop]
    exit /b 1
)

rem 检查传入的参数是 start 还是 stop
if /i "%1"=="start" (
    rem 调用启动脚本
    call :start_zookeeper
) else if /i "%1"=="stop" (
    rem 调用停止脚本
    call :stop_zookeeper
) else (
    echo Invalid argument. USAGE: %0 [start/stop]
    exit /b 1
)

exit /b 0

:start_zookeeper
echo Starting Zookeeper...
call %BASE_DIR%\bin\windows\zookeeper-server-start.bat %BASE_DIR%\config\zookeeper.properties
rem 调用启动脚本的命令

exit /b 0

:stop_zookeeper
echo Stopping Zookeeper...
call %BASE_DIR%\bin\windows\zookeeper-server-stop.bat
rem 调用停止脚本的命令

exit /b 0




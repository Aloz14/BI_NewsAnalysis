@echo off

set BASE_DIR=%KAFKA_HOME%

rem 检查是否传入了参数
if "%1"=="" (
    echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
    exit /b 1
)

rem 检查传入的参数是 start 还是 stop
if /i "%1"=="start" (
    rem 调用启动脚本
    echo Starting Kafka...
    call %BASE_DIR%\bin\windows\kafka-server-start.bat %BASE_DIR%\config\server.properties
    exit /b 0
) else if /i "%1"=="stop" (
    rem 调用停止脚本
    echo Stopping Kafka...
    call %BASE_DIR%\bin\windows\kafka-server-stop.bat
    exit /b 0
) else if /i "%1"=="kc" (
    rem 检查 kc 后是否跟随了 topic
    if "%2"=="" (
        echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
        exit /b 1
    ) else (
        rem 消费
        echo Executing Kafka Control for topic %2...
        call %BASE_DIR%\bin\windows\kafka-console-consumer.bat --bootstrap-server 43.142.45.216:9092 --topic %2 --from-beginning
        exit /b 0
    )
) else if /i "%1"=="kp" (
      rem 检查 kp 后是否跟随了 topic
      if "%2"=="" (
          echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
          exit /b 1
      ) else (
          rem 生产
          echo Executing Kafka Control for topic %2...
          call %BASE_DIR%\bin\windows\kafka-console-producer.bat --bootstrap-server 43.142.45.216:9092 --topic %2
          exit /b 0
      )
) else if /i "%1"=="list" (
    rem 列出所有的 topic
    echo Listing all topics...
    call %BASE_DIR%\bin\windows\kafka-topics.bat --bootstrap-server 43.142.45.216:9092 --list
    exit /b 0
) else if /i "%1"=="create" (
    rem 创建新的 topic
    if "%2"=="" (
        echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
        exit /b 1
    ) else (
        echo Creating topic %2...
        call %BASE_DIR%\bin\windows\kafka-topics.bat --bootstrap-server 43.142.45.216:9092 --create --topic %2 --partitions 3 --replication-factor 1
        exit /b 0
    )
) else if /i "%1"=="delete" (
    rem 删除指定 topic
    if "%2"=="" (
        echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
        exit /b 1
    ) else (
        echo Deleting topic %2...
        call %BASE_DIR%\bin\windows\kafka-topics.bat --bootstrap-server 43.142.45.216:9092 --delete --topic %2
        exit /b 0
    )
) else if /i "%1"=="describe" (
    rem 描述指定 topic
    if "%2"=="" (
        echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
        exit /b 1
    ) else (
        echo Describing topic %2...
        call %BASE_DIR%\bin\windows\kafka-topics.bat --bootstrap-server 43.142.45.216:9092 --describe --topic %2
        exit /b 0
    )
) else (
        echo "USAGE: %0 {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}"
    exit /b 1
)

exit /b 0

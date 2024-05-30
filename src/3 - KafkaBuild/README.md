## Kafka 控制脚本文档

该脚本是一个用于管理 Apache Kafka 服务器的控制脚本，可以执行启动、停止、消费、生产、列出、创建、删除和描述 Kafka 主题等操作。

### 注意事项

- 请确保正确配置`KAFKA_HOME`环境变量，以便脚本正确执行。
- 在执行`kc`和`kp`命令时，请确保 Kafka 服务器已启动并在本地主机的默认端口（9092）上运行。

### 使用方法

```
kafka-control.bat {start|stop|kc [topic]|kp [topic]|list|create [topic]|delete [topic]|describe [topic]}
```

### 参数说明

- `start`: 启动 Kafka 服务器。
- `stop`: 停止 Kafka 服务器。
- `kc [topic]`: 执行 Kafka 控制台消费者，监听指定主题的消息。
- `kp [topic]`: 执行 Kafka 控制台生产者，向指定主题发送消息。
- `list`: 列出所有 Kafka 主题。
- `create [topic]`: 创建一个新的 Kafka 主题。
- `delete [topic]`: 删除指定的 Kafka 主题。
- `describe [topic]`: 描述指定的 Kafka 主题。
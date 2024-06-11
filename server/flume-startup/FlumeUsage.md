实现日志采集使用了Flume框架。

**Requirement**：

- Flume 1.11.0
- Java JDK 1.8以上

将形如`apache-flume-1.11.0-bin\bin`的路径添加到系统路径

并添加`JAVA_HOME`至jdk目录

如需修改输入输出源，请修改`BI.conf`中的参数配置。

运行`run.bat`即可开始采集日志。

默认代码如下：

```
# 指定Flume source(要监听的路径)
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = C://Users//Zheng//Desktop//BI_NewsAnalysis//log

# 指定Flume sink
agent.sinks.sparkSink.type = org.apache.spark.streaming.flume.sink.SparkSink
agent.sinks.sparkSink.hostname = localhost
agent.sinks.sparkSink.port = 9999
```

**！！！！！！！！！！！！！！！！！！！！！！！！！！！！**

监听路径最好是修改为相对路径，但懒得改了。**因此不好适配其他机器**，该模块可以暂时不用管不启动。

**！！！！！！！！！！！！！！！！！！！！！！！！！！！！**

sink为输出源，可以是本地目录或者一个端口，通过修改type和其他参数进行修改。
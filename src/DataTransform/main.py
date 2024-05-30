from pyspark import SparkContext
from pyspark.sql import SparkSession

# 创建一个本地SparkContext
sc = SparkContext("local[2]", "FlumeSparkIntegration")

# 创建一个StreamingContext，并设置批处理间隔为5秒
ssc = StreamingContext(sc, 0.5)

# 设置Flume的主机和端口，用于接收数据流
flume_host = "localhost"
flume_port = 9999

# 创建一个Flume数据流
flumeStream = FlumeUtils.createStream(ssc, flume_host, flume_port)

# 对接收到的数据流进行简单处理示例，可以根据具体需求进行修改
lines = flumeStream.map(lambda x: x[1])

# 打印处理后的数据
lines.pprint()

# 启动StreamingContext
ssc.start()

# 等待StreamingContext终止
ssc.awaitTermination()

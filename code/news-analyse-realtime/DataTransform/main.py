from pyspark.sql import SparkSession
import os
import happybase
import json
import time
from datetime import datetime 

# environment variables
os.environ['PYSPARK_SUBMIT_ARGS'] = '--packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.1 pyspark-shell'

KAFKA_HOST = "43.142.45.216"
KAFKA_PORT = 9092
KAFKA_SUBSCRIBE_TOPIC = "RAWLOG"

HBASE_HOST = "122.51.75.129"
HBASE_PORT = 9090


# spark and hbase integration is not ELEGANT at all !!!!

# PREDEFINES
NEWS_CLICKS_KEYS = ["news_id",
                    "exposure_time",
                    "dwelltime"]

USER_HISTORY_KEYS = [ "user_id",
                      "news_id",
                      "exposure_time",
                      "is_clicked",
                      "dwelltime"]

NEWS_CLICKS_CF = "info"
USER_HISTORY_CF = "info"

NEWS_CLICKS_TNAME = "news_clicks"
USER_HISTORY_TNAME = "user_history"

def create_hbase_connection(host, port):
    connection = happybase.Connection(host=host, port=port)
    connection.open()
    return connection

def write_to_hbase(df, connection):
    raw_data = ''
    for row in df.select("value").collect():
        raw_data = raw_data+row.value.decode("utf-8")

    data = {}
    try:
        data = json.loads(raw_data)
    except:
        return
    
    user_id = data['UserID']
    history_news_clicks = data['ClicknewsID'].split()
    history_dwelltimes = list(map(int, data['dwelltime'].split()))
    history_exposure_times = [datetime.strptime(ts, "%m/%d/%Y %I:%M:%S %p") for ts in data['exposure_time'].split('#TAB#')]
    cur_positive_clicks = data['pos'].split()
    cur_negative_clicks = data['neg'].split()
    cur_start_time = datetime.strptime(data['start'], "%m/%d/%Y %I:%M:%S %p")
    cur_end_time = datetime.strptime(data['end'], "%m/%d/%Y %I:%M:%S %p")
    cur_dwelltime = list(map(int, data['dwelltime_pos'].split()))
 
    table = connection.table(NEWS_CLICKS_TNAME)

    # put news_id, exposure_time, dwelltime into hbase
    for news_id, exposure_time, dwelltime in zip(history_news_clicks, history_exposure_times, history_dwelltimes):
        table.put(news_id.encode() + str(exposure_time).encode() + str(dwelltime).encode(), 
                  {NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[0]: news_id.encode(),
                   NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[1]: str(exposure_time).encode(),
                   NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[2]: str(dwelltime).encode()})
                
    table = connection.table(USER_HISTORY_TNAME)        
    # put user_id, news_id, start_time, dwelltime into hbase
    for news_id, dwelltime in zip(cur_positive_clicks, cur_dwelltime):
        table.put(user_id.encode() + news_id.encode() + str(cur_start_time).encode(), 
                  {USER_HISTORY_CF+":"+USER_HISTORY_KEYS[0]: user_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[1]: news_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[2]: str(cur_start_time).encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[3]: "true".encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[4]: str(dwelltime).encode()})
        
    for news_id, dwelltime in zip(cur_negative_clicks, cur_dwelltime):
        table.put(user_id.encode() + news_id.encode() + str(cur_start_time).encode(), 
                  { USER_HISTORY_CF+":"+USER_HISTORY_KEYS[0]: user_id.encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[1]: news_id.encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[2]: str(cur_start_time).encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[3]: "false".encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[4]: str(dwelltime).encode()})
    
    # history visits
    for news_id, dwelltime, exposure_time in zip(history_news_clicks, history_dwelltimes, history_exposure_times):
        table.put(user_id.encode() + news_id.encode() + str(exposure_time).encode(), 
                  {USER_HISTORY_CF+":"+USER_HISTORY_KEYS[0]: user_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[1]: news_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[2]: str(exposure_time).encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[3]: "true".encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[4]: str(dwelltime).encode()})

        
    connection.close()
        


if __name__ == "__main__":
    print("Starting Spark Application")
    spark = SparkSession.builder.appName("SimpleApp").getOrCreate()

    sdf = spark \
      .readStream \
      .format("kafka") \
      .option("kafka.bootstrap.servers", KAFKA_HOST + ':' +str(KAFKA_PORT)) \
      .option("subscribe", KAFKA_SUBSCRIBE_TOPIC) \
      .load()

    streaming_query = sdf \
        .writeStream \
        .foreachBatch(lambda df, epoch_id: write_to_hbase(df, create_hbase_connection(HBASE_HOST, HBASE_PORT))) \
        .start()
    streaming_query.awaitTermination()


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
KAFKA_SUBSCRIBE_TOPIC = "RAW"

HBASE_HOST = "122.51.75.129"
HBASE_PORT = 9090


# spark and hbase integration is not ELEGANT at all !!!!

# PREDEFINES
NEWS_CLICKS_KEYS = [
    "news_id",                    
    "user_id",                    
    "exposure_time",                    
    "dwelltime"
    ]

USER_HISTORY_KEYS = [ 
    "user_id",                      
    "news_id",                      
    "exposure_time",                      
    "dwelltime"
    ]

CATEGORY_CLICKS_KEYS = [
    "category",
    "news_id",
    "exposure_time",
    ]

NEWS_INFO_KEYS=[
    "category"
]

NEWS_CLICKS_CF = "info"
USER_HISTORY_CF = "info"
CATEGORY_CLICKS_CF = "info"
NEWS_INFO_CF = "info"

NEWS_CLICKS_TNAME = "news_clicks"
USER_HISTORY_TNAME = "user_history"
CATEGORY_CLICKS_TNAME = "category_clicks"
NEWS_INFO_TNAME = "news_info"

def create_hbase_connection(host, port):
    connection = happybase.Connection(host=host, port=port)
    connection.open()
    return connection

def write_to_hbase(df, connection):

    # print("Writing to HBase")

    raw_data = ''
    # print(df.select("value").collect())

    for row in df.select("value").collect():
        raw_data = raw_data+row.value.decode("utf-8")
        
    data = {}
    try:
        data = json.loads(raw_data)
    except:
        return
    
    # print('Start data processing')
    
    user_id = data['UserID']
    history_news_clicks = data['ClicknewsID'].split()
    history_dwelltimes = list(map(int, data['dwelltime'].split()))
    history_exposure_times = [datetime.strptime(ts, "%m/%d/%Y %I:%M:%S %p") for ts in data['exposure_time'].split('#TAB#')]
    cur_positive_clicks = data['pos'].split()
    cur_negative_clicks = data['neg'].split()
    cur_start_time = datetime.strptime(data['start'], "%m/%d/%Y %I:%M:%S %p")
    cur_end_time = datetime.strptime(data['end'], "%m/%d/%Y %I:%M:%S %p")
    cur_dwelltime = list(map(int, data['dwelltime_pos'].split()))

    # print('Data processing finished')

    # print('Start writing to News Clicks Table')

    # 写入news_clicks表
    news_clicks_table = connection.table(NEWS_CLICKS_TNAME)

    # put news_id, exposure_time, dwelltime into hbase
    for news_id, exposure_time, dwelltime in zip(history_news_clicks, history_exposure_times, history_dwelltimes):
        news_clicks_table.put(news_id.encode() + str(exposure_time).encode() + str(dwelltime).encode(), 
                  {NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[0]: news_id.encode(),
                   NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[1]: user_id.encode(),
                   NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[2]: str(exposure_time).encode(),
                   NEWS_CLICKS_CF+":"+NEWS_CLICKS_KEYS[3]: str(dwelltime).encode()})
        
    # print('Writing to News Clicks Table finished')

    # print('Start writing to User History Table')

    # 写入user_history表            
    user_history_table = connection.table(USER_HISTORY_TNAME)        
    # put user_id, news_id, start_time, dwelltime into hbase
    for news_id, dwelltime in zip(cur_positive_clicks, cur_dwelltime):
        user_history_table.put(user_id.encode() + str(cur_start_time).encode(), 
                  {USER_HISTORY_CF+":"+USER_HISTORY_KEYS[0]: user_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[1]: news_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[2]: str(cur_start_time).encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[3]: str(dwelltime).encode()})
    '''
    for news_id, dwelltime in zip(cur_negative_clicks, cur_dwelltime):
        table.put(user_id.encode() + str(cur_start_time).encode(), 
                  { USER_HISTORY_CF+":"+USER_HISTORY_KEYS[0]: user_id.encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[1]: news_id.encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[2]: str(cur_start_time).encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[3]: "false".encode(),
                    USER_HISTORY_CF+":"+USER_HISTORY_KEYS[4]: str(dwelltime).encode()})
    '''
    # history visits
    for news_id, dwelltime, exposure_time in zip(history_news_clicks, history_dwelltimes, history_exposure_times):
        user_history_table.put(user_id.encode() + str(exposure_time).encode(), 
                  {USER_HISTORY_CF+":"+USER_HISTORY_KEYS[0]: user_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[1]: news_id.encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[2]: str(exposure_time).encode(),
                   USER_HISTORY_CF+":"+USER_HISTORY_KEYS[3]: str(dwelltime).encode()})
        
    # print('Writing to User History Table finished')

    # print('Start writing to Category Clicks Table')

    # 写入category_clicks表
    category_clicks_table = connection.table(CATEGORY_CLICKS_TNAME)
    news_info_table = connection.table(NEWS_INFO_TNAME)
    # get category from news_info table
    # put news_id,exposure_time,category into hbase
    for news_id,exposure_time in zip(history_news_clicks,history_exposure_times):
        # get category
        row_key = news_id.encode()
        column = NEWS_INFO_CF + ":" + NEWS_INFO_KEYS[0]
        row = news_info_table.row(row_key, columns=[column])
        category = row.get(column.encode()).decode()
        # Regular
        category_clicks_table.put(
            news_id.encode()+str(exposure_time).encode(),
            {
                CATEGORY_CLICKS_CF+":"+CATEGORY_CLICKS_KEYS[0]:str(category).encode(),
                CATEGORY_CLICKS_CF+":"+CATEGORY_CLICKS_KEYS[1]:news_id.encode(),
                CATEGORY_CLICKS_CF+":"+CATEGORY_CLICKS_KEYS[2]:str(exposure_time).encode()
            }
        )

    # print('Writing to Category Clicks Table finished')

    connection.close()

    # print("Writing to HBase finished")
        


if __name__ == "__main__":
    # print("Starting Spark Application")
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


from pyspark.sql import SparkSession
import os
import happybase
import json
import time
from datetime import datetime 
import pandas as pd
# environment variables
os.environ['PYSPARK_SUBMIT_ARGS'] = '--packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.1 pyspark-shell'

KAFKA_HOST = "43.142.45.216"
KAFKA_PORT = 9092
KAFKA_SUBSCRIBE_TOPIC = "RAW"

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
CATEGORY_CLICKS_KEYS =[ "news_id",
                        "exposure_time",
                        "category"]
NEWS_INFO_KEYS=[
    "category"
]
NEWS_CLICKS_CF = "info"
USER_HISTORY_CF = "info"
CATEGORY_CLICKS_CF = "info"
NEWS_INFO_CF = "info"

NEWS_CLICKS_TNAME = "news_clicks"
USER_HISTORY_TNAME = "user_history"
CATEGORY_CLICKS_TNAME ="category_clicks"
NEWS_INFO_TNAME="news_info"

def create_hbase_connection(host, port):
    connection = happybase.Connection(host=host, port=port)
    connection.open()
    return connection

def write_to_hbase(df, connection):
    data =df
    
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
    
    table = connection.table(CATEGORY_CLICKS_TNAME)
    # get category from news_info table
    news_info_table=connection.table(NEWS_INFO_TNAME)
    # put news_id,exposure_time,category into hbase
    for news_id,exposure_time in zip(history_news_clicks,history_exposure_times):
        # get category
        row_key = news_id.encode()
        print("Row key is"+row_key)
        column = NEWS_INFO_CF+":"+NEWS_INFO_KEYS[0]
        row = news_info_table.row(row_key, columns=[column])
        category_value = row.get(column.encode())
        print("Category value is"+category_value)
        # Regular
        table.put(news_id.encode()+str(exposure_time).encode(),
                  {CATEGORY_CLICKS_CF+":"+CATEGORY_CLICKS_KEYS[0]:news_id.encode(),
                   CATEGORY_CLICKS_CF+":"+CATEGORY_CLICKS_KEYS[1]:str(exposure_time).encode(),
                   CATEGORY_CLICKS_CF+":"+CATEGORY_CLICKS_KEYS[2]:str(category_value).encode()})

        
    connection.close()
        


if __name__ == "__main__":
    df2 = pd.read_csv("./train.tsv",sep='\t')
    write_to_hbase(df2,create_hbase_connection(HBASE_HOST, HBASE_PORT))


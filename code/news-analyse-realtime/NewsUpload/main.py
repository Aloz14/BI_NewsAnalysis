import pandas as pd
import happybase

HBASE_HOST = "122.51.75.129"
HBASE_PORT = 9090

NEWS_INFO_TNAME = "news_info"
NEWS_INFO_CF = "info"
NEWS_INFO_KEYS = ["news_id",
                    "category",
                    "topic",
                    "headline",
                    "news_body",
                    "title_entity",
                    "entity_content"]

df = pd.read_csv("./news.tsv",sep='\t')

# transform every row to a object

connection = happybase.Connection(HBASE_HOST,HBASE_PORT)
connection.open()
table = connection.table(NEWS_INFO_TNAME)

for index, row in df.iterrows():
    news_id = row['News ID']
    category = row['Category']
    topic = row['Topic']
    headline = row['Headline']
    news_body = row['News body']
    title_entity = row['Title entity']
    entity_content = row['Entity content']
    
    # store to hbase
    table.put(  news_id.encode(), 
                {NEWS_INFO_CF+":"+NEWS_INFO_KEYS[0]: news_id.encode(),
                NEWS_INFO_CF+":"+NEWS_INFO_KEYS[1]: category.encode(),
                NEWS_INFO_CF+":"+NEWS_INFO_KEYS[2]: topic.encode(),
                NEWS_INFO_CF+":"+NEWS_INFO_KEYS[3]: headline.encode(),
                NEWS_INFO_CF+":"+NEWS_INFO_KEYS[4]: news_body.encode(),
                NEWS_INFO_CF+":"+NEWS_INFO_KEYS[5]: title_entity.encode(),
                NEWS_INFO_CF+":"+NEWS_INFO_KEYS[6]: entity_content.encode()})
    
    
connection.close()

    
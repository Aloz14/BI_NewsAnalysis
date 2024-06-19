## 1. 特定种类新闻热度变化情况查询

有15类的新闻，统计该15类新闻每一类的点击量变化。

输入：
- category：种类
- startTime：时间段左边界
- endTime：时间段右边界

业务逻辑可以参考Pt3用户历史数据查询。

需要实现的点在于数据库中没有对应的存储表。

在流处理环节中也没有对应的处理过程。（流处理文件见/code/news-analyse-realtime/DataTransform/main.py）

可以考虑使用Redis或者HBase。

对于Redis，可以为每一种category设计一个Sorted Set，使用timestamp作为分数，从而实现有序排列。

对于HBase，表名为：category_clicks。选取的RowKey可以是：category + exposureTime。同时应当有如下列：

- news_id
- exposure_time
- category

使用`scan`进行筛选时，使用category + timestamp的方式来筛选范围。

注意：**不要**使用HBase自带的筛选器（Filter）。尽量使用RowKey单值筛选或范围筛选的方式进行。若要进行筛选，请在本地进行处理。

## 2. 特定查询

- 时间
  - 时间点
  - 时间段
- 新闻主题
- 新闻标题长度
- 新闻长度
- 用户
  - 单用户
  - 多用户

## 3. 推荐

## 4. 用户历史记录 优化

使用多线程并法连接到Redis/HBase以提高性能。


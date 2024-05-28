import os
import time
import LogUploader
import pandas as pd
import TsvSort
from datetime import datetime, timedelta


# for testing
def test_tsv_generate():
    if not os.path.exists('../../dataset/train.tsv'):
        print('Target file is not exist')
        return

    if os.path.exists('./test.tsv'):
        return

    data = pd.read_csv('../../dataset/train.tsv', sep='\t')
    data = data.head(1000)
    # 保存到tsv文件
    data.to_csv('./test.tsv', sep='\t', index=False)


if __name__ == '__main__':
    test_tsv_generate()
    data = pd.read_csv('./test.tsv', sep='\t')

    remote_log = LogUploader.LogUploader('localhost', 22, 'root', 'root')
    remote_log.connect()

    index = 0
    start_time = datetime(2019, 6, 15, 10, 19, 50)  # 6/15/2019 10:19:50 PM
    current_time = start_time
    while True:
        print(f"\rCurrent time: {current_time}", end='', flush=True)
        current_time += timedelta(seconds=1)
        if index < len(data):
            if current_time >= datetime.strptime(data['end'][index], "%m/%d/%Y %I:%M:%S %p"):

                # 日志生成
                # 选择是生成到本地日志文件还是发送到服务器
                remote_log.write_data_to_file(data.loc[index], '/root/log/impression.log')

                index += 1
        time.sleep(1)


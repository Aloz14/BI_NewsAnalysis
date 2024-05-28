import pandas as pd

if __name__ == '__main__':
    data = pd.read_csv('./test.tsv', sep='\t')
    print(data.loc[0].to_json())
import pandas as pd

df = pd.read_csv("./news.tsv",sep='\t')

categories = df['Category'].unique()

print(categories)
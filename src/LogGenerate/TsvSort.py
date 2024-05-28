import os
import pandas as pd
def sort_tsv(path):
    data = pd.read_csv(path, sep='\t')
    sorted_data = data.sort_values(by='end')
    sorted_data.to_csv("./sorted.tsv", sep='\t', index=False)

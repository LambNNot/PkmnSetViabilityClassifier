import pandas as pd
import json
import random

df = pd.read_csv('src/main/cleanedExpandedDataWithSpecies.csv')

print(df)
attributes = list(df.columns)

trimmed_df = df.loc[:, "hp":"hasStatus"]
trimmed_df['format'] = df['format']
print(trimmed_df)

trimmed_df.to_csv('src/main/trimmedData.csv', index=False, header=False)

import json
import pandas as pd

allSets = pd.read_json('src/main/resources/smogonData/sets.json')
print(len(allSets))
print(allSets.columns)


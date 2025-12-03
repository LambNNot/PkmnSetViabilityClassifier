import json

with open('src/main/resources/smogonData/sets.json', 'r') as f:
    allSets = json.load(f)
print(len(allSets))

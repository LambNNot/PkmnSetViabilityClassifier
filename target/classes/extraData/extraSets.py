import json, os

curr_dir = os.path.dirname(__file__)

calcSets: dict = {}
with open(os.path.join(curr_dir, 'showdownSets.json'), 'r') as f:
    calcSets = json.load(f) # calcSets will be a dictionary that maps Pokemon to existing sets

print(calcSets.keys())
print(sum([len(calcSets[mon]) for mon in calcSets.keys()]))




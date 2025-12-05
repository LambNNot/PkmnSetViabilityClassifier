import json, os
from typing import Dict
from ...resources import data as pkmn

RELEVANT_FORMATS = ["ZU", "PU", "NU", "RU", "UU", "OU", "Uber", "AG"]
STANDARD_MOVES_DATA = {move['name'] : move
                       for move in pkmn.getAllMoves() if move['isNonstandard'] == "Standard"}


allMons = [mon['name'] for mon in pkmn.getAllStandardMons()]

calcSets: dict = {}
with open(os.path.join('src/main/resources/extraData/showdownSets.json'), 'r') as f:
    calcSets:Dict[str, Dict[str, Dict]] = json.load(f) # calcSets will be a dictionary that maps Pokemon to existing sets

# print(calcSets.keys())
# print(sum([len(calcSets[mon]) for mon in calcSets.keys()]))


if __name__ == "__main__": 
    extraSets = []
    for mon in calcSets.keys():
        if not mon in allMons:
            continue

        for name, data in calcSets[mon].items():
            format_matches = [f for f in RELEVANT_FORMATS if f in name]
            if not format_matches:
                continue
            else:
                format = format_matches[0] 
            if any(data.get(field) is None for field in ['ability',
                                           'item',
                                           'nature',
                                           'teraType',
                                           'evs',
                                           'moves']):
                continue
            extraSets.append(
                {
                    'name' : name,
                    'pokemon' : mon,
                    'shiny' : False,
                    'gender' : "DC",
                    'abilities' : [data['ability']],
                    'levels': [],
                    'description': "",
                    'items': [data['item']],
                    'teratypes': [data['teraType']],
                    'moveslots': [
                        [{
                            "move": move,
                            "type": None
                        }]
                        for move in data['moves'] if move in STANDARD_MOVES_DATA
                    ],
                    'evconfigs': [
                        {
                            stat: data['evs'].get(abbr, 0)
                            for stat, abbr in zip(
                                ['hp', 'atk', 'def', 'spa', 'spd', 'spe'],
                                ['hp', 'at', 'df', 'sa', 'sd', 'sp'])}
                    ],
                    'ivconfigs': [
                        {
                            stat: data['ivs'].get(abbr, 31)
                            for stat, abbr in zip(
                                ['hp', 'atk', 'def', 'spa', 'spd', 'spe'],
                                ['hp', 'at', 'df', 'sa', 'sd', 'sp'])}
                    ] if data.get('ivs') else [],
                    'natures': [data['nature']],
                    'format': format
                }
            )

    print(extraSets)
    with open('src/main/resources/extraData/extraSets.json', 'w') as f:
        f.write(json.dumps(extraSets, indent=2))

        # print(f"{len(calcSets[mon])}")
        # extraSets.extend(list(calcSets[mon].values()))

    # print(extraSets)





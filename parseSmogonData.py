import json

# Parse data
with open('smogonData.json', 'r') as file:
    smogonData:dict  = json.load(file)

smogonData = smogonData['injectRpcs']
genData, smogonData = smogonData[0], smogonData[1]
smogonData = smogonData[1]
pokemonData = smogonData['pokemon']
formatData = smogonData['formats']
natureData = smogonData['natures']
abilityData = smogonData['abilities']
moveFlags = smogonData['moveflags']
moveData = smogonData['moves']
typeData = smogonData['types']
itemData = smogonData['items']

print(smogonData['pokemon'])
print(len(pokemonData))

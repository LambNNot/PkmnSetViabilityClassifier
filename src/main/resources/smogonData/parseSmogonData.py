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

# Write pokemon data
with open("pokemonData.json" , 'w') as f:
    f.write(json.dumps(pokemonData, indent=2))

# Write format data
with open("formatData.json" , 'w') as f:
    f.write(json.dumps(formatData, indent=2))


# Write nature data
with open("natureData.json" , 'w') as f:
    f.write(json.dumps(natureData, indent=2))


# Write ability data
with open("abilityData.json" , 'w') as f:
    f.write(json.dumps(abilityData, indent=2))

# Write moveFlags data
with open("moveFlags.json" , 'w') as f:
    f.write(json.dumps(moveFlags, indent=2))


# Write move data
with open("moveData.json" , 'w') as f:
    f.write(json.dumps(moveData, indent=2))


# Write type data
with open("typeData.json" , 'w') as f:
    f.write(json.dumps(typeData, indent=2))


# Write item data
with open("itemData.json" , 'w') as f:
    f.write(json.dumps(itemData, indent=2))




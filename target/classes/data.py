"""This file is used to retrieve data"""
import json

def getAllStandardMons():
    print("Fetching all standard pokemon...")
    with open('src/main/resources/smogonData/pokemonData.json') as f:
        mons = json.load(f)

    return [mon for mon in mons
            if mon.get('isNonstandard') == "Standard"
                    or mon.get('isNonstandard') == "NatDex"]

def getAllSets():
    print("Fetching all sets...")
    with open('src/main/resources/smogonData/sets.json', 'r') as f:
        return json.load(f)
    
def getExtraSets():
    print("Fetching extra sets...")
    with open('src/main/resources/extraData/extraSets.json', 'r') as f:
        return json.load(f)
    
def getAllTypes():
    print("Fetching all types...")
    with open('src/main/resources/smogonData/typeData.json') as f:
        return json.load(f)
    
def getAllAbilities():
    print("Fetching all abilities...")
    with open('src/main/resources/smogonData/abilityData.json') as f:
        return json.load(f)
    
def getAllItems():
    print("Fetching all items...")
    with open('src/main/resources/smogonData/itemData.json') as f:
        return json.load(f)
    
def getAllNatures():
    print("Fetching all natures...")
    with open('src/main/resources/smogonData/natureData.json') as f:
        return json.load(f)
    
def getAllMoves():
    print("Fetching all moves...")
    with open('src/main/resources/smogonData/moveData.json') as f:
        return json.load(f)
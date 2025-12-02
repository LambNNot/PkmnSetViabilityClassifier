import json
import requests
from bs4 import BeautifulSoup

URL = "https://www.smogon.com/dex/sv/pokemon/"

if __name__ == "__main__":
    response = requests.get(URL)
    soup = BeautifulSoup(response.content, 'html.parser')

    dex_script = soup.find('script', type='text/javascript')
    script_text:str = dex_script.text

    json_start = script_text.find("{")
    parsed_dex_settings:dict = json.loads(script_text[json_start:])
    
    smogonData = parsed_dex_settings.get('injectRpcs')[1][1]
    pokemonData = smogonData['pokemon']
    formatData = smogonData['formats']
    natureData = smogonData['natures']
    abilityData = smogonData['abilities']
    moveFlags = smogonData['moveflags']
    moveData = smogonData['moves']
    typeData = smogonData['types']
    itemData = smogonData['items']

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




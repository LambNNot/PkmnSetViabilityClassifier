from bs4 import BeautifulSoup
import requests
import json

response = requests.get("https://www.smogon.com/dex/sv/pokemon/abomasnow/")

soup = BeautifulSoup(response.content, 'html.parser')

dex_script = soup.find('script', type='text/javascript')
script_text:str = dex_script.text

json_start = script_text.find("{")

parsed_dex_settings:dict = json.loads(script_text[json_start:])
# print(parsed_dex_settings.keys())
# print(len(parsed_dex_settings.get('injectRpcs')[2]))
pkmn_dump:dict = parsed_dex_settings.get('injectRpcs')[2][1]

# print(pkmn_dump.keys())
strategies:list = pkmn_dump.get('strategies')

relevant_formats = ["ZU", "PU", "NU", "RU", "NU", "OU", "Uber", "AG"]

for strat in strategies:
    strat:dict
    #print(f"\n-----\n{strat}")
    #print(f"\n{strat.get('format')} --> {strat.get('movesets')}")
    if strat.get('format') in relevant_formats:
        print(f"\n-----\n{strat.get('movesets')}")
    else:
        print(f"\n-----\nIRRELEVANT STRAT DETECT: {strat.get("format")}")

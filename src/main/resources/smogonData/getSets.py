from bs4 import BeautifulSoup
import requests
import json
from time import sleep
from typing import List

RELEVANT_FORMATS = ["ZU", "PU", "NU", "RU", "NU", "OU", "Uber", "AG"]

def getAllStandardMons() -> List[str]:
    with open('smogonData/pokemonData.json', 'r') as f:
        pkmn_data = json.load(f)

    return [pkmn.get('name') for pkmn in pkmn_data if pkmn.get('isNonstandard') == "Standard"]

def getStrats(pokemon: str) -> List[dict]:
    """
    Scrape documented strategies of a given pokemon from Smogon for relevant formats.
    Strategy written descriptions are modified to be blank for data processing.
    """
    response = requests.get(f"https://www.smogon.com/dex/sv/pokemon/{pkmn.lower()}/")

    soup = BeautifulSoup(response.content, 'html.parser')

    dex_script = soup.find('script', type='text/javascript')
    script_text:str = dex_script.text

    json_start = script_text.find("{")
    parsed_dex_settings:dict = json.loads(script_text[json_start:])
    pkmn_dump:dict = parsed_dex_settings.get('injectRpcs')[2][1]
    strategies:list = pkmn_dump.get('strategies')

    for strat in strategies:
        strat:dict
        if strat.get('format') in RELEVANT_FORMATS:
            for set in strat.get('movesets'):
                set:dict
                set.update({'description' : ''})
                # print(f"\n-----\nFormat: {strat.get('format')}\n{set}")

if __name__ == "__main__":
    # Load all saved Standard Pokemon
    standardMons = getAllStandardMons()
    print(len(standardMons))

    # Clean Pokemon Forms

    # Actual Scraping Logic

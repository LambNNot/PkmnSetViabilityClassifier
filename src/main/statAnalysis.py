"""
This file is intended to statistically determine
breakpoints for determining categorization in dataCleaning.py
"""
import json
import numpy as np
from typing import Dict

"""
Categories:
- Low <= 10% < Middling < 70% <= Competitive < 90% <= High 
"""

LOW_THRESHOLD = 0.1
STANDARD_THRESHOLD = 0.7
HIGH_THRESHOLD = 0.9


def getBreakPoints(stat: str, monsList: list) -> Dict[str, int]:
    stats = [mon.get(stat) for mon in monsList]
    return {
        "low": round(np.quantile(stats, LOW_THRESHOLD)),
        "standard": round(np.quantile(stats, STANDARD_THRESHOLD)),
        "high": round(np.quantile(stats, HIGH_THRESHOLD))
    }

if __name__ == "__main__":
    with open('src/main/resources/smogonData/pokemonData.json') as f:
        mons = json.load(f)

    print(f"No. of Mons in Data: {len(mons)}")

    standard_mons = [mon for mon in mons
                    if mon.get('isNonstandard') == "Standard"
                    or mon.get('isNonstandard') == "NatDex"]

    print(f"No. of Relevant Mons: {len(standard_mons)}")

    breakpoints = {}
    for stat in ['hp', 'atk', 'def', 'spa', 'spd', 'spe']:
        calculated_breakpoints = getBreakPoints(stat, standard_mons)
        # print(f"{stat.upper()} Breakpoints: {calculated_breakpoints}")
        breakpoints.update({stat : calculated_breakpoints})
    print(breakpoints)

    with open("src/main/resources/statBreakpoints.json", 'w') as f:
        f.write(json.dumps(breakpoints))
"""
This file handles encoding for every feature.
"""
import resources.data as pkmn
from typing import List, Dict

STAT_BREAKPOINTS = {"hp": {"low": 40, "standard": 80, "high": 104},
                    "atk": {"low": 45, "standard": 100, "high": 125},
                    "def": {"low": 40, "standard": 90, "high": 115},
                    "spa": {"low": 35, "standard": 90, "high": 120},
                    "spd": {"low": 40, "standard": 86, "high": 110},
                    "spe": {"low": 32, "standard": 90, "high": 110}}
ALL_TYPES = ['Bug', 'Dark', 'Dragon', 'Electric', 'Fairy', 'Fighting', 'Fire', 'Flying', 'Ghost', 'Grass', 'Ground', 'Ice', 'Normal', 'Poison', 'Psychic', 'Rock', 'Steel', 'Stellar', 'Water']
TYPES_TO_ID = {ty : id for ty, id in zip(ALL_TYPES, range(19))}
PRIMES = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67]
TYPE_TO_PRIME = {ty : prime for ty, prime in zip(ALL_TYPES, PRIMES)}
ABILITIES = [a['name'] for a in pkmn.getAllAbilities()]
ABILITIES_TO_ID = {ability : id for ability, id in zip(ABILITIES, range(1, len(ABILITIES)+ 1))} # 0 is reserved for no item
ITEMS = [i['name'] for i in pkmn.getAllItems()]
ITEMS_TO_ID = {item: id for item, id in zip(ITEMS, range(len(ITEMS)))}
NATURES = [n['name'] for n in pkmn.getAllNatures()]
NATURES_TO_ID = {nature : id for nature, id in zip(NATURES, range(len(NATURES)))}
QUATERNARY_CATS = ["low", "middling", "competitive", "high"]
STANDARD_MOVES_DATA = {move['name'] : move
                       for move in pkmn.getAllMoves() if move['isNonstandard'] == "Standard"}
POKEMON_TYPES_DATA = {mon['name'] : mon['types'] for mon in pkmn.getAllStandardMons()}


# Encode Stats
def encodeStat(stat: str, val: int) -> int:
    """
    Encodes val into one of 4 categories:
    Low, Middling, Competitive, High
    """
    bps = STAT_BREAKPOINTS.get(stat)
    if bps is None:
        raise ValueError(f"Invalid stat: {stat}")
    if val <= bps['low']:
        return 0
    elif val < bps['standard']:
        return 1
    elif val >= bps['high']:
        return 3
    else:
        return 2
    

# Encode Types
def encodeType(type_: List[str]) -> int:
    if len(type_) == 1:
        types = type_ + [type_[0]]
    elif len(type_) != 2:
        raise ValueError(f"Invalid types: {type_}")
    else:
        types = type_
    return TYPE_TO_PRIME[types[0]] * TYPE_TO_PRIME[types[1]]

# Encode Gender
def encodeGender(gender: str) -> int:
    map = {"DC" : 0, "F": 1, "M": 2}
    if map.get(gender) is None:
        raise ValueError(f"Invalid gender: {gender}")
    return map[gender]

# Encode Level
def encodeLevel(level: List[int]) -> int:
    if not level:
        return 0
    else:
        level = level[0]
    if level == 100:
        return 0
    elif level > 96:
        return 1
    elif level > 0:
        return 2
    else:
        raise ValueError(f"Invalid level: {level}")

# Encode Ability
def encodeAbility(abilities: List[str]):
    if not abilities:
        raise ValueError("Missing abilities!")
    return ABILITIES_TO_ID[abilities[0]]

# Encode Item
def encodeItem(items: List[str]) -> int:
    if len(items) < 1:
        return 0
    return ITEMS_TO_ID[items[0]]

# Encode Tera
def encodeTera(types: List[str]) -> int:
    if len(types) < 1:
        raise ValueError("Missing tera type!")
    return TYPES_TO_ID[types[0]]

# Encode Moves

"""
MOVES: 
Consider only standard moves.
"""
def encodeMoves(attr: str, moves: List[List[Dict[str, str]]], species: str) -> int:
    move_names = [move[0]['move'] for move in moves]
    match attr:
        case "hitsPhysical":
            return hitsPhysical(move_names)
        case "hitsSpecial":
            return hitsSpecial(move_names)
        case "basePower":
            return basePower(move_names)
        case "hasPriority":
            return hasPriority(move_names)
        case "hasSTAB":
            return hasSTAB(move_names, species)
        case "hasCoverage":
            return hasCoverage(move_names, species)
        case "hitsPivot":
            return hitsPivot(move_names)
        case "hitsSetUp":
            return hitsSetUp(move_names)
        case "hitsHazards":
            return hitsHazards(move_names)
        case "hasRemoval":
            return hasRemoval(move_names)
        case "hasKnockOff":
            return hasKnockOff(move_names)
        case "hasStatus":
            return hasStatus(move_names)
        case _:
            raise ValueError(f"Unknown move attribute: {attr}")



def hitsPhysical(moves: List[str]) -> int:
    # Check for physical moves
    return int(
        any(STANDARD_MOVES_DATA[move]['category'] == "Physical" for move in moves))
def hitsSpecial(moves: List[str]) -> int:
    # Check for special moves
    return int(
        any(STANDARD_MOVES_DATA[move]['category'] == "Special" for move in moves))
def basePower(moves: List[str]) -> int:
    # Check average base power of damaging moves
    bps = [STANDARD_MOVES_DATA[move]['power']
           for move in moves
           if STANDARD_MOVES_DATA[move]['category'] != "Non-Damaging"]
    avg_bp = sum(bps)/len(bps) if len(bps) > 0 else 0
    if avg_bp < 60:
        return 0
    elif avg_bp <= 80:
        return 1
    elif avg_bp <= 100:
        return  2
    else:
        return 3
def hasPriority(moves: List[str]) -> int:
    # Check for any priority
    return int(
        any(STANDARD_MOVES_DATA[move]['priority'] > 0 for move in moves)
    )
def hasSTAB(moves: List[str], species: str) -> int:
    # Check for STAB
    types = POKEMON_TYPES_DATA[species]
    return int(
        any(STANDARD_MOVES_DATA[move]['type'] in types
            for move in moves
            if STANDARD_MOVES_DATA[move]['category'] != "Non-Damaging"))
def hasCoverage(moves: List[str], species: str) -> int:
    types = POKEMON_TYPES_DATA[species]
    return int(
        any(not STANDARD_MOVES_DATA[move]['type'] in types
            and STANDARD_MOVES_DATA[move]['power'] > 70
            for move in moves
            if STANDARD_MOVES_DATA[move]['category'] != "Non-Damaging")
    )
def hitsPivot(moves: List[str]) -> int:
    # Check for term "user switches out"
    return int(
        any("user switches out" in STANDARD_MOVES_DATA[move]['description'].lower()
            for move in moves)
    )
def hitsSetUp(moves: List[str]) -> int:
    # Check for term "raises the user's"
    return int(
        any("raises the user's" in STANDARD_MOVES_DATA[move]['description'].lower()
            for move in moves)
    )
def hitsHazards(moves: List[str]) -> int:
    # Look for term "switch-in"
    return int(
        any("switch-in" in STANDARD_MOVES_DATA[move]['description'].lower()
            for move in moves)
    )
def hasRemoval(moves: List[str]) -> int:
    # Look for term "hazards"
    return int(
        any("hazards" in STANDARD_MOVES_DATA[move]['description'].lower()
            for move in moves)
    )
def hasKnockOff(moves: List[str]) -> int:
    # Check move name
    return int(
        any(move == "Knock Off"
            for move in moves)
    )
def hasStatus(moves: List[str]) -> int:
    # Check move type
    return int(
        any(STANDARD_MOVES_DATA[move]['category'] == "Non-Damaging"
            for move in moves)
    )
    


# Encode EVs
def encodeEVs(stat: str, evs: List[Dict[str, int]]) -> int:
    if not evs:
        raise ValueError("Missing EVs!")
    ev = evs[0].get(stat)
    if ev is None:
        raise ValueError(f"Invalid ev stat: {stat}")
    if 0 <= ev <= 8:
        return 0
    elif ev < 100:
        return 1
    elif ev < 248:
        return 2
    elif ev <= 252:
        return 3
    else:
        raise ValueError(f"Invalid ev spread: {ev} evs in {stat}")

# Encode IVs
def encodeIVs(stat: str, ivs: List[Dict[str, int]]) -> int:
    if not ivs:
        return 3
    iv = ivs[0].get(stat)
    if iv is None:
        raise ValueError(f"Invalid iv stat: {iv}")
    elif iv == 0:
        return 0
    elif iv < 26:
        return 1
    elif iv < 30:
        return 2
    elif iv >= 30:
        return 3
    else:
        raise ValueError(f"Invalid iv spread: {iv} ivs in {stat}")
    

# Encode Nature
def encodeNature(natures: List[str]) -> int:
    if len(natures) < 1:
        raise ValueError("Missing Nature!")
    return NATURES_TO_ID[natures[0]]

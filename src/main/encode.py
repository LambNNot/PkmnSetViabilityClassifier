"""
This file handles encoding for every feature.
"""

# Encode Stats
# Encode Types
# Encode Gender
# Encode Level
# Encode Ability
# Encode Item
# Encode Tera
# Encode Moves
"""
MOVES
Consider only standard moves.
"""
def hitsPhysical(moves) -> int:
    # Check for physical moves
    pass
def hitsSpecial(moves) -> int:
    # Check for special moves
    pass
def basePower(moves) -> int:
    # Check average base power of damaging moves
    pass
def hasPriority(moves) -> int:
    # Check for any priority
    pass
def hasSTAB(moves) -> int:
    # Check for STAB
    pass
def hasCoverage(moves) -> int:
    # Check for high-damage non-stab
    pass
def hitsPivot(moves) -> int:
    # Check for term "user switches out"
    pass
def hitsSetUp(moves) -> int:
    # Check for term "raises the user's"
    pass
def hitsHazards(moves) -> int:
    # Look for term "switch-in"
    pass
def hasRemoval(moves) -> int:
    # Look for term "hazards"
    pass
def hasKnockOff(moves) -> int:
    # Check move name
    pass
def hasStatus(moves) -> int:
    # Check move type
    pass

# Encode EVs
# Encode IVs
# Encode Nature

import json
import pandas as pd
import resources.data as pkmn
import encode

STATS = ['hp', 'atk', 'def', 'spa', 'spd', 'spe']

MOVE_ATTRS = [
        "hitsPhysical",
        "hitsSpecial",
        "basePower",
        "hasPriority",
        "hasSTAB",
        "hasCoverage",
        "hitsPivot",
        "hitsSetUp",
        "hitsHazards",
        "hasRemoval",
        "hasKnockOff",
        "hasStatus"]

def create_df():
    allSets:dict = pd.DataFrame(pkmn.getAllSets())
    extraSets: dict = pd.DataFrame(pkmn.getExtraSets())
    allSets = pd.concat([allSets, extraSets])

    allSets = allSets.rename(columns={
        "name" : "set_name",

    })
    allMons = pd.DataFrame(pkmn.getAllStandardMons())
    allMons = allMons.drop('abilities', axis=1)

    df = pd.merge(allSets,
                        allMons,
                        how="left",
                        left_on="pokemon",
                        right_on="name")
    
    return df[['pokemon',
             'hp', 'atk', 'def', 'spa', 'spd', 'spe',
             'types',
             'gender', 'levels',
             'abilities',
             'items',
             'teratypes',
             'moveslots',
             'evconfigs', 'ivconfigs',
             'natures',
             'format']]

def categorize_stats(df):
    print("Encoding stats...")
    for stat in STATS:
        df[stat] = df[stat].map(lambda x : encode.encodeStat(stat, x))

def categorize_types(df):
    print("Encoding types...")
    df["type"] = df["types"].map(encode.encodeType)
    df.drop("types", axis=1, inplace=True)

def categorize_gender(df):
    print("Encoding gender...")
    df["gender"] = df["gender"].map(encode.encodeGender)

def categorize_level(df):
    print("Encoding levels...")
    df["level"] = df["levels"].map(encode.encodeLevel)
    df.drop("levels", axis=1, inplace=True)

def categorize_abilities(df):
    print("Encoding abilities...")
    df['ability'] = df['abilities'].map(encode.encodeAbility)
    df.drop("abilities", axis=1, inplace=True)

def categorize_items(df):
    print("Encoding items...")
    df['item'] = df['items'].map(encode.encodeItem)
    df.drop('items', axis=1, inplace=True)

def categorize_tera(df):
    print("Encoding tera types...")
    df['teratype'] = df['teratypes'].map(encode.encodeTera)
    df.drop('teratypes', axis=1, inplace=True)

def categorize_moves(df):
    for new_attr in MOVE_ATTRS:
        df[new_attr] = df.apply(
            lambda row: encode.encodeMoves(new_attr, row['moveslots'], row['pokemon']),
            axis=1
        )
    df.drop('moveslots', axis=1, inplace=True)

def categorize_evs(df):
    for stat in STATS:
        df[stat+"_ev"] = df['evconfigs'].map(
            lambda ev_configs: encode.encodeEVs(stat, ev_configs))
    df.drop('evconfigs', axis=1, inplace=True)

def categorize_ivs(df):
    for stat in STATS:
        df[stat+"_iv"] = df['ivconfigs'].map(
            lambda iv_configs: encode.encodeIVs(stat, iv_configs))
    df.drop('ivconfigs', axis=1, inplace=True)

def categorize_natures(df):
    print("Encoding natures...")
    df['nature'] = df['natures'].map(encode.encodeNature)
    df.drop('natures', axis=1, inplace=True)

def categorize_df(df):

    categorize_stats(df)
    # print([df[stat].value_counts(normalize=True) for stat in STATS])
    categorize_types(df)
    # print(df["type"])
    categorize_gender(df)
    # print(df['gender'])
    categorize_level(df)
    # print(df['level'])
    categorize_abilities(df)
    # print(df['ability'])
    categorize_items(df)
    # print(df['item'])
    categorize_tera(df)
    # print(df['teratype'])
    categorize_moves(df)
    # print(df[[
    #     "hitsPhysical",
    #     "hitsSpecial",
    #     "basePower",
    #     "hasPriority",
    #     "hasSTAB",
    #     "hasCoverage",
    #     "hitsPivot",
    #     "hitsSetUp",
    #     "hitsHazards",
    #     "hasRemoval",
    #     "hasKnockOff",
    #     "hasStatus"]])
    categorize_evs(df)
    # print(df[[stat+"_ev" for stat in STATS]])
    categorize_ivs(df)
    # print(df[[stat+"_iv" for stat in STATS]])
    categorize_natures(df)
    # print(df['nature'])
    
if __name__ == "__main__":
    df = create_df()
    print(f"\nInitial Columns: {df.columns}")
    categorize_df(df)
    attributes = list(df.columns)
    attributes.remove('format')
    print(f"\nFinalized Attributes: {attributes}")
    final_df = df[attributes+['format']]
    print(f"\nFinal Columns: {final_df.columns}")
    
    final_df.to_csv('src/main/cleanedExpandedDataWithSpecies.csv')
    final_df.drop('pokemon', axis=1).to_csv('src/main/cleanedExpandedData.csv', index=False, header=False)


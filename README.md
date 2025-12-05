# PkmnSetViabilityClassifier

Using maching learning to build a Classifier to predict the viability of homebrew Pokemon sets in the context of Competitive Singles.

## Collecting Data

Steps:

```
1. Scrape a list of pokemon from https://www.smogon.com/dex/sv/pokemon/

2. Scrape all written strategies for each pokemon:
https://www.smogon.com/dex/sv/pokemon/<POKEMON-NAME>/

3. Store as CSV
```

## Machine Learning Algorithm

Random Forest

## Expected Features (Codebook):

The following are features on which the model can be trained:

#### 1. Stats (e.g. HP, Atk, Def, etc.)

We choose to consider stats of species instead of treating species as a categorical attribute.

This category actually spans 6 features, one for each stat.
Every stat is encoded into 4 categories:

```
0 for Low (stat <= 0.1>)
1 for Middling (0.1 < stat < 0.7)
2 for Competitive (0.7 <= stat < 0.9)
3 for High (stat >= 0.9)
```

#### 2. Type

Every Pokemon has 1-2 types. Every type is encoded such that the order of the types are irrelevant.
Each of the 19 types (Base + Stellar) are associated with one of the first 19 prime numbers. A Pokemon's type encoding is equal to the product of the Prime encoding of their numbers. All mono-type Pokemon are treated to be dual-types with the same type twice.

#### 3. Gender (0-2)

Encoded as:

```
0 for DC (Does not matter)
1 for Female
2 for Male
```

#### 4. Level (0-2)

Encoded as:

```
0 for Level 100
1 for Levels 96-100
2 for Levels < 96
```

#### 5. Ability

Every ability is encoded as a numerical id.

#### 6. Item

Every item is encoded as a numerical id.

#### 7. Tera (1-19)

Every individual type (+ Stellar) is encoded as a numerical id.

#### 8. Moves (Revisit)
Since our data must be categorical, each Pokemon's moveset will be abstracted into its own list of features.
1. hitsPhysical (Binary)
2. hitsSpecial (Binary)
3. basePower (Quaternary, 0-3)
4. hasPriority (Binary)
5. hasSTAB (Binary)
6. hasCoverage (Binary)
7. hasPivot (Binary)
8. hasSetUp (Binary)
9. hasHazards (Binary)
10. hasRemoval (Binary)
11. hasStatus (Binary)

#### 9. EVs

As with stats, this category actually spans 6 features, again, one for each stat.
Every feature is encoded into 4 categories:

```
0 for minimal investment (ev <= 8)
1 for low investment (8 < ev < 100)
2 for moderate investment (100 <= ev < 248)
3 for high investment (ev >= 248)
```

#### 10. IVs

Similar to EVs.
Every feature is encoded into 4 categories:

```
0 for minimal investment (iv == 0)
1 for low investment (0 < ev < 26)
2 for moderate investment (26 <= iv < 30)
3 for high investment (iv >= 30)
```

#### 11. Nature
Every nature is encoded as a numerical id.

#### 12. Format (Not actually an attribute)
This attribute is not actually an attribute at all, and is, instead, our classification label.
Each relevant format is encded into one of 8 categories:
```
0 for ZU
1 for PU
2 for NU
3 for RU
4 for UU
5 for OU
6 for Ubers
7 for AG
```

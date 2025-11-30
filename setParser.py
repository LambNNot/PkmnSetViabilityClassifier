from pydantic import BaseModel, model_validator, field_validator
import monTypes

class Pokemon(BaseModel):
    base_hp : int 
    base_atk : int
    base_def : int
    base_spa : int
    base_spd : int
    base_spe : int
    baseType : str
    weight : float
    height : float
    isNFE : bool

    @field_validator(base_hp, base_atk, base_def, base_spa, base_spd, base_spe)
    def check_valid_stats(cls, stat: int) -> int:
        if stat <= 0:
            raise ValueError("Pokemon stats must be a natural number")
        return stat

    @field_validator(type)
    def check_valid_type(cls, baseType: str) -> str:
        return baseType

class PokemonSet(BaseModel):
    ev_hp : int
    ev_atk : int
    ev_def : int
    ev_spa : int
    ev_spd : int
    ev_spe : int
    iv_hp : int
    iv_atk : int
    iv_def : int
    iv_spa : int
    iv_spd : int
    iv_spe : int

    @field_validator(ev_hp, ev_atk, ev_def, ev_spa, ev_spd, ev_spe)
    def validate_ev_spread(cls, ev : int) -> int:
        if not (0 <= ev <= 252):
            raise ValueError("Invalid EV spread")
        return ev
    
    @field_validator(iv_hp, iv_atk, iv_def, iv_spa, iv_spd, iv_spe)
    def validate_ev_spread(cls, iv : int) -> int:
        if not (0 <= iv <= 31):
            raise ValueError("Invalid IV spread")
        return iv

    @model_validator(mode="after")
    def validate_ev_total(self) -> 'PokemonSet':
        ev_total = self.ev_hp + self.ev_atk + self.ev_def + self.ev_spa + self.ev_spd + self.ev_spe
        if not (0 <= ev_total <= (2 * 252) + 4):
            raise ValueError("Invalid EV total")
        return self
    
    if __name__ == "__main__":
        Pokemon(
            base_hp=1,
            base_atk=1,
            base_def=1,
            base_spa=1,
            base_spd=1,
            base_spe=1,
            baseType="Water",
            weight=1,
            height=1,
            isNFE=True
        )
        pass
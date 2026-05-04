from abc import ABC, abstractmethod
from enum import Enum
import json


class UrovenSpolurace(Enum):
    SPATNA = "špatná"
    PRUMERNA = "průměrná"
    DOBRA = "dobrá"

    @staticmethod
    def z_retezce(s: str):
        mapa = {"špatná": UrovenSpolurace.SPATNA,
                "průměrná": UrovenSpolurace.PRUMERNA,
                "dobrá": UrovenSpolurace.DOBRA}
        return mapa.get(s.lower())

    def na_cislo(self) -> int:
        return {UrovenSpolurace.SPATNA: 1,
                UrovenSpolurace.PRUMERNA: 2,
                UrovenSpolurace.DOBRA: 3}[self]


class Spoluprace:
    pass

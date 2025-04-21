package com.e_fit.enities;

public enum ExerciseType {
    MANCUERNAS,
    BARRA,
    MÁQUINA,
    POLEA,
    BANDA,
    LIBRE,
    TRX;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

package com.e_fit.enities;

public enum MuscularGroup {
    TODOS,
    HOMBRO,
    PECHO,
    ESPALDA,
    BÍCEPS,
    TRÍCEPS,
    PIERNAS,
    ABDOMEN,
    FULLBODY;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

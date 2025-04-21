package com.e_fit.enities;

public enum SetType {
    STANDARD,
    CALENTAMIENTO,
    DROP,
    UNILATERAL,
    VARIANTE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

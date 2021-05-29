/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.gts.utils;

import com.m1zark.gts.guis.Trade;

public class PokeRequest {
    private String name;
    private String nature;
    private String ability;
    private String gender;
    private boolean shiny;
    private int level;

    public PokeRequest(Trade trade) {
        this.name = trade.requestedPokemon;
        this.level = trade.level;
        this.nature = trade.nature;
        this.ability = trade.ability;
        this.gender = trade.gender;
        this.shiny = trade.shiny;
    }

    public String getName() {
        return this.name;
    }

    public String getNature() {
        return this.nature;
    }

    public String getAbility() {
        return this.ability;
    }

    public String getGender() {
        return this.gender;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean getShiny() {
        return this.shiny;
    }
}


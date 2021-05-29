/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.gts.utils;

import com.m1zark.gts.GTS;
import com.m1zark.gts.utils.PokeRequest;

public class Pokemon {
    private String nbt;
    private boolean trade = false;
    private String price;
    private String requiredPokemon;
    private String expires;

    public Pokemon(String nbt) {
        this.nbt = nbt;
        this.price = "";
        this.requiredPokemon = "";
        this.expires = "";
    }

    public Pokemon(String nbt, String price, String expires) {
        this.nbt = nbt;
        this.price = price;
        this.requiredPokemon = "";
        this.expires = expires;
    }

    public Pokemon(String nbt, boolean trade, PokeRequest requiredPokemon, String expires) {
        this.nbt = nbt;
        this.trade = trade;
        this.requiredPokemon = GTS.getInstance().gson.toJson((Object)requiredPokemon);
        this.expires = expires;
    }

    public String getNBT() {
        return this.nbt;
    }

    public PokeRequest getRequiredPokemon() {
        return (PokeRequest)GTS.getInstance().gson.fromJson(this.requiredPokemon, PokeRequest.class);
    }

    public boolean getIsTrade() {
        return this.trade;
    }

    public String getPrice() {
        return this.price;
    }

    public String getExpires() {
        return this.expires;
    }
}


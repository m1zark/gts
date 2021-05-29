/*
 * Decompiled with CFR 0.151.
 */
package com.m1zark.gts.utils.configuration.Tokens;

public enum Tokens {
    Player("player"),
    Lot_Type("lot_type"),
    Expires("expires"),
    Price("price"),
    MinPrice("min_price"),
    Currency_Symbol("curr_symbol"),
    PokeLookedFor("poke_looked_for"),
    Buyer("buyer"),
    Seller("seller"),
    Max_Pokemon("max_pokemon"),
    Slot("slot"),
    Pokemon("pokemon"),
    Nickname("nickname"),
    Ability("ability"),
    Gender("gender"),
    Growth("growth"),
    Nature("nature"),
    Level("level"),
    EV_Percentage("EV%"),
    EVTotal("evtotal"),
    EVHP("evhp"),
    EVAtk("evatk"),
    EVDef("evdef"),
    EVSpAtk("evspatk"),
    EVSpDef("evspdef"),
    EVSpeed("evspeed"),
    IV_Percentage("IV%"),
    IVTotal("ivtotal"),
    IVHP("ivhp"),
    IVAtk("ivatk"),
    IVDef("ivdef"),
    IVSpAtk("ivspatk"),
    IVSpDef("ivspdef"),
    IVSpeed("ivspeed"),
    Form("form"),
    Shiny("shiny");

    private String token;

    private Tokens(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTException
 */
package com.m1zark.gts.utils;

import com.m1zark.gts.utils.PokeRequest;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.PokemonData;
import com.pixelmonmod.pixelmon.Pixelmon;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class Listing {
    private int listingID;
    private UUID owner;
    private String nbt;
    private String price;
    private Date expires;
    private String expiresRaw;
    private Pokemon pkm;
    private PokeRequest request;

    public Listing(int listingID, UUID owner, Pokemon pkm, String price, String expires) {
        this.listingID = listingID;
        this.owner = owner;
        this.pkm = pkm;
        this.nbt = pkm.getNBT();
        this.request = pkm.getRequiredPokemon();
        this.price = price;
        this.expires = Date.from(Instant.parse(expires));
        this.expiresRaw = expires;
    }

    public boolean checkHasExpired() {
        return this.getExpires().before(Date.from(Instant.now()));
    }

    public com.pixelmonmod.pixelmon.api.pokemon.Pokemon getPokemon() {
        try {
            return Pixelmon.pokemonFactory.create(JsonToNBT.getTagFromJson((String)this.nbt));
        }
        catch (NBTException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PokemonData getPkmItem() {
        return new PokemonData(this.listingID, this.getPokemon(), this.price, this.owner, this.expires, this.getisTrade());
    }

    public boolean getisTrade() {
        return this.pkm.getIsTrade();
    }

    public int getListingID() {
        return this.listingID;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public String getNbt() {
        return this.nbt;
    }

    public String getPrice() {
        return this.price;
    }

    public Date getExpires() {
        return this.expires;
    }

    public String getExpiresRaw() {
        return this.expiresRaw;
    }

    public Pokemon getPkm() {
        return this.pkm;
    }

    public PokeRequest getRequest() {
        return this.request;
    }
}


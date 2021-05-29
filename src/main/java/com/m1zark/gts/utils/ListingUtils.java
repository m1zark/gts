/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  javax.annotation.Nullable
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagCompound
 *  org.apache.commons.lang3.StringUtils
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.service.user.UserStorageService
 */
package com.m1zark.gts.utils;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

public class ListingUtils {
    @Nullable
    public static PlayerPartyStorage getPlayerStorage(EntityPlayerMP player) {
        return Pixelmon.storageManager.getParty(player);
    }

    public static boolean isHiddenAbility(Pokemon p, String ability) {
        return p.getAbilitySlot() == 2;
    }

    public static boolean isHiddenAbility(BaseStats stats, String ability) {
        return stats.getAbilitiesArray()[2] != null && !stats.getAbilitiesArray()[2].isEmpty() && stats.getAbilitiesArray()[2].equalsIgnoreCase(ability) && !stats.getAbilitiesArray()[0].equalsIgnoreCase(ability);
    }

    public static String updatePokemonName(String name) {
        if (name.equalsIgnoreCase("MrMime")) {
            return "Mr. Mime";
        }
        if (name.equalsIgnoreCase("MimeJr")) {
            return "Mime Jr.";
        }
        if (name.equalsIgnoreCase("Nidoranfemale")) {
            return "Nidoran&d\u2640&r";
        }
        if (name.equalsIgnoreCase("Nidoranmale")) {
            return "Nidoran&b\u2642&r";
        }
        if (name.equalsIgnoreCase("Farfetchd")) {
            return "Farfetch'd";
        }
        if (name.contains("Alolan")) {
            return StringUtils.join((Object[])StringUtils.splitByCharacterTypeCamelCase((String)name.replaceAll("\\d+", "")), (String)" ");
        }
        return name;
    }

    public static Optional<String> getNameFromUUID(UUID uuid){
        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> oUser = uss.get(uuid);

        if (oUser.isPresent()){
            // the name with which that player has been online the last time
            String name = oUser.get().getName();
            return Optional.of(name);
        } else {
            // a player with that uuid has never been on your server
            return Optional.empty();
        }
    }

    public Optional<User> getUser(UUID uuid) {
        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorage.get().get(uuid);
    }

    public static NBTTagCompound getNbt(Pokemon pokemon) {
        NBTTagCompound nbt = new NBTTagCompound();
        pokemon.writeToNBT(nbt);
        return nbt;
    }

    public static Pokemon getPokemon(String nbt) {
        try {
            return Pixelmon.pokemonFactory.create(JsonToNBT.getTagFromJson((String)nbt));
        }
        catch (NBTException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String getNatureShorthand(StatsType type) {
        switch (type) {
            case Accuracy: {
                return "Acc";
            }
            case HP: {
                return "HP";
            }
            case Speed: {
                return "Speed";
            }
            case Attack: {
                return "Atk";
            }
            case Defence: {
                return "Def";
            }
            case Evasion: {
                return "Eva";
            }
            case SpecialAttack: {
                return "SpAtk";
            }
            case SpecialDefence: {
                return "SpDef";
            }
            case None: {
                return "None";
            }
        }
        return "";
    }
}


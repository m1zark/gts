/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.filter.Getter
 *  org.spongepowered.api.event.network.ClientConnectionEvent$Disconnect
 *  org.spongepowered.api.event.network.ClientConnectionEvent$Join
 */
package com.m1zark.gts.listeners;

import com.m1zark.gts.GTS;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.util.List;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerListeners {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter(value="getTargetEntity") Player player) {
        List<Pokemon> pokemon = GTS.getInstance().getSql().getCacheListing(player.getUniqueId());
        if (!pokemon.isEmpty()) {
            pokemon.forEach(pkm -> {
                PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
                if (storage != null) {
                    storage.add(pkm);
                }
                Chat.sendMessage((CommandSource)player, (String)MessageConfig.getMessages("Messages.General.Returned-Pokemon").replace("{pokemon}", ListingUtils.updatePokemonName(pkm.getSpecies().name())));
            });
            GTS.getInstance().getSql().removeCacheListings(player.getUniqueId());
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
    }
}


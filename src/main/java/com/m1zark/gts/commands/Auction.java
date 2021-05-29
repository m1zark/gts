/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec
 *  com.pixelmonmod.pixelmon.battles.BattleRegistry
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Event
 *  org.spongepowered.api.event.cause.Cause
 *  org.spongepowered.api.event.cause.EventContext
 *  org.spongepowered.api.event.cause.EventContextKeys
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.GTS;
import com.m1zark.gts.auctions.Auctions;
import com.m1zark.gts.events.AuctionEvent;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.text.DecimalFormat;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.text.Text;

public class Auction
implements CommandExecutor {
    private static final PokemonSpec UNTRADABLE = new PokemonSpec("untradeable");

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException((Text)Text.of((String)"Only players can auction items!"));
        }
        Player player = (Player)src;
        int count = (int)GTS.getInstance().getCurrentAuctions().stream().filter(auc -> auc.getSeller().equals(player.getUniqueId())).count();
        if (ConfigManager.maxAuctions != -1 && count >= ConfigManager.maxAuctions) {
            throw new CommandException(Chat.embedColours((String)MessageConfig.getMessages("Messages.auctions.Max-auctions")));
        }
        Optional slot = args.getOne((Text)Text.of((String)"slot"));
        Optional price = args.getOne((Text)Text.of((String)"price"));
        Optional increment = args.getOne((Text)Text.of((String)"increment"));
        if (BattleRegistry.getBattle((EntityPlayer)((EntityPlayer)player)) != null) {
            throw new CommandException(Chat.embedColours((String)"&cYou cannot auction a Pok\u00e9mon while in battle."));
        }
        if (slot.isPresent()) {
            if ((Integer)slot.get() < 1 || (Integer)slot.get() > 6) {
                throw new CommandException(Chat.embedColours((String)"&cSlot number must be between 1 and 6."));
            }
            PlayerPartyStorage storage = ListingUtils.getPlayerStorage((EntityPlayerMP)player);
            if (storage != null) {
                Pokemon pokemon = storage.get((Integer)slot.get() - 1);
                if (pokemon != null) {
                    if (storage.getTeam().size() == 1 && !pokemon.isEgg()) {
                        throw new CommandException(Chat.embedColours((String)"&cYou cannot auction off your last Pok\u00e9mon."));
                    }
                    if (pokemon.isEgg()) {
                        throw new CommandException(Chat.embedColours((String)"&cYou cannot auction a Pok\u00e9mon Egg!"));
                    }
                    if (UNTRADABLE.matches(pokemon)) {
                        throw new CommandException(Chat.embedColours((String)"&cThis Pok\u00e9mon is marked as untradeable, and cannot be auctioned..."));
                    }
                    if (this.checkPrices(pokemon, (Integer)price.get())) {
                        int p = EnumSpecies.legendaries.contains(pokemon.getSpecies().name()) ? ConfigManager.minLegend : (ListingUtils.isHiddenAbility(pokemon, pokemon.getAbility().getName()) ? ConfigManager.minHA : ConfigManager.minNormal);
                        throw new CommandException(Chat.embedColours((String)MessageConfig.getMessages("Messages.auctions.Minimum-Price").replace("{price}", String.valueOf(p))));
                    }
                    if (increment.isPresent() && (Integer)increment.get() < ConfigManager.minBidIncrement) {
                        throw new CommandException(Chat.embedColours((String)("&4Bid Increment must be at least P" + ConfigManager.minBidIncrement)));
                    }
                    Auctions auc2 = Auctions.builder().seller(player.getUniqueId()).price((Integer)price.get()).bidIncrement((Integer)increment.get()).pokemon(pokemon).build();
                    AuctionEvent auctionEvent = new AuctionEvent(auc2, Cause.of((EventContext)EventContext.builder().add(EventContextKeys.PLUGIN, GTS.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, player.getProfile()).build(), (Object)GTS.getInstance()));
                    Sponge.getEventManager().post((Event)auctionEvent);
                    if (!auctionEvent.isCancelled()) {
                        GTS.getInstance().getCurrentAuctions().add(auc2);
                        Chat.sendBroadcastMessage((CommandSource)player, (String)MessageConfig.getMessages("Messages.auctions.New-Auction-Broadcast").replace("{player}", player.getName()).replace("{ability}", pokemon.getAbility().getLocalizedName()).replace("{IV%}", this.IVPercent(pokemon)).replace("{shiny}", pokemon.isShiny() ? "Shiny " : "").replace("{pokemon}", ListingUtils.updatePokemonName(pokemon.getSpecies().name())));
                        Chat.sendMessage((CommandSource)player, (String)MessageConfig.getMessages("Messages.auctions.New-Auction-Player").replace("{pokemon}", ListingUtils.updatePokemonName(pokemon.getSpecies().name())));
                        storage.retrieveAll();
                        storage.set((Integer)slot.get() - 1, null);
                    }
                } else {
                    Chat.sendMessage((CommandSource)src, (String)("&cThere doesn't appear to be Pokemon in slot " + slot.get() + "."));
                }
            } else {
                Chat.sendMessage((CommandSource)src, (String)"&cThere was an issue adding the auction. Please try again.");
            }
        }
        return CommandResult.success();
    }

    private boolean checkPrices(Pokemon pixelmon, int price) {
        if (EnumSpecies.legendaries.contains(pixelmon.getDisplayName())) {
            return price < ConfigManager.minLegend;
        }
        if (ListingUtils.isHiddenAbility(pixelmon, pixelmon.getAbility().getName())) {
            return price < ConfigManager.minHA;
        }
        return price < ConfigManager.minNormal;
    }

    private String IVPercent(Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#0.##");
        int totalIVs = pokemon.getIVs().hp + pokemon.getIVs().attack + pokemon.getIVs().defence + pokemon.getIVs().specialAttack + pokemon.getIVs().specialDefence + pokemon.getIVs().speed;
        return df.format((double)totalIVs / 186.0 * 100.0) + "%";
    }
}


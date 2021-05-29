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
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.GTS;
import com.m1zark.gts.events.ListEvent;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.text.DecimalFormat;
import java.time.Instant;
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
import org.spongepowered.api.text.format.TextColors;

public class AddCommand
implements CommandExecutor {
    private static final PokemonSpec UNTRADABLE = new PokemonSpec("untradeable");

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "Only players can add items to the gts!"));
        }
        Player player = (Player)src;
        if (BattleRegistry.getBattle((EntityPlayer)player) != null) {
            throw new CommandException(Chat.embedColours("&4You cannot add a Pokemon to GTS while in battle."));
        }
        int total = (int)GTS.getInstance().getSql().getAllListings().stream().filter(listing -> listing.getOwner().equals(player.getUniqueId())).count();
        if (ConfigManager.maxListings != -1 && total >= ConfigManager.maxListings) {
            throw new CommandException(Chat.embedColours(MessageConfig.getMessages("Messages.Sell-Item.Has-Too-Many")));
        }
        Optional<Integer> oprice = args.getOne(Text.of("price"));
        Optional<Integer> oslot = args.getOne(Text.of("slot"));
        if (oslot.isPresent()) {
            if (oslot.get() < 1 || oslot.get() > 6) {
                throw new CommandException(Text.of(TextColors.RED, "Slot number must be between 1 and 6!"));
            }
        } else {
            throw new CommandException(Text.of(TextColors.RED, "You must include a slot number!"));
        }
        oprice.ifPresent(price -> {
            if (price < 1) {
                Chat.sendMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Negative-Price"));
            } else {
                int slot = oslot.get() - 1;
                PlayerPartyStorage storage = ListingUtils.getPlayerStorage((EntityPlayerMP)player);
                com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon = storage.get(slot);
                if (pokemon == null) {
                    Chat.sendMessage(player, "&cUnable to find a Pok\u00e9mon in the specified slot.");
                    return;
                }
                if (storage.getTeam().size() == 1 && !pokemon.isEgg()) {
                    Chat.sendMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Last-Member"));
                    return;
                }
                if (pokemon.isEgg() && !ConfigManager.allowEgg) {
                    Chat.sendMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Sell-Egg"));
                    return;
                }
                if (UNTRADABLE.matches(pokemon)) {
                    player.sendMessage(Text.of(TextColors.GRAY, "This Pok\u00e9mon is marked as untradeable, and cannot be sold..."));
                    return;
                }
                if (this.checkPrices(pokemon, price)) {
                    int p = EnumSpecies.legendaries.contains(pokemon.getDisplayName()) ? ConfigManager.minLegend : (ListingUtils.isHiddenAbility(pokemon, pokemon.getAbility().getName()) ? ConfigManager.minHA : ConfigManager.minNormal);
                    Chat.sendMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Minimum-Price").replace("{price}", String.valueOf(p)));
                } else {
                    Pokemon pkm = new Pokemon(ListingUtils.getNbt(pokemon).toString(), Integer.toString(price), Instant.now().plusSeconds(ConfigManager.listingsTime).toString());
                    ListEvent listEvent = new ListEvent(player, pkm, Cause.of(EventContext.builder().add(EventContextKeys.PLUGIN, GTS.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, player.getProfile()).build(), GTS.getInstance()));
                    Sponge.getEventManager().post(listEvent);
                    if (!listEvent.isCancelled()) {
                        GTS.getInstance().getSql().addListing(player.getUniqueId(), pkm, Integer.toString(price), ConfigManager.listingsTime);
                        if (MessageConfig.getServerAnnounce()) {
                            if (pokemon.isEgg()) {
                                Chat.sendBroadcastMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Announce-Added-Egg").replace("{player}", player.getName()).replace("{pokemon}", pokemon.isEgg() ? "Pok\u00e9mon Egg" : ListingUtils.updatePokemonName(pokemon.getSpecies().name())).replace("{price}", Integer.toString(price)));
                            } else {
                                Chat.sendBroadcastMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Announce-Added").replace("{player}", player.getName()).replace("{ability}", pokemon.getAbility().getLocalizedName()).replace("{IV%}", this.IVPercent(pokemon)).replace("{shiny}", pokemon.isShiny() ? "Shiny " : "").replace("{pokemon}", pokemon.isEgg() ? "Pok\u00e9mon Egg" : ListingUtils.updatePokemonName(pokemon.getSpecies().name())).replace("{price}", Integer.toString(price)));
                            }
                        }
                        Chat.sendMessage(player, MessageConfig.getMessages("Messages.Sell-Item.Success").replace("{pokemon}", pokemon.isEgg() ? "Pok\u00e9mon Egg" : ListingUtils.updatePokemonName(pokemon.getSpecies().name())).replace("{price}", Integer.toString(price)));
                        storage.retrieveAll();
                        storage.set(slot, null);
                    }
                }
            }
        });
        return CommandResult.success();
    }

    private boolean checkPrices(com.pixelmonmod.pixelmon.api.pokemon.Pokemon pixelmon, int price) {
        if (EnumSpecies.legendaries.contains(pixelmon.getDisplayName())) {
            return price < ConfigManager.minLegend;
        }
        if (ListingUtils.isHiddenAbility(pixelmon, pixelmon.getAbility().getName())) {
            return price < ConfigManager.minHA;
        }
        return price < ConfigManager.minNormal;
    }

    private String IVPercent(com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#0.##");
        int totalIVs = pokemon.getIVs().getStat(StatsType.HP) + pokemon.getIVs().getStat(StatsType.Attack) + pokemon.getIVs().getStat(StatsType.Defence) + pokemon.getIVs().getStat(StatsType.SpecialAttack) + pokemon.getIVs().getStat(StatsType.SpecialDefence) + pokemon.getIVs().getStat(StatsType.Speed);
        return df.format((double)totalIVs / 186.0 * 100.0) + "%";
    }
}


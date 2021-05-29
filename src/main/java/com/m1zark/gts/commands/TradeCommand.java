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
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.GTS;
import com.m1zark.gts.guis.Trade;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class TradeCommand
implements CommandExecutor {
    private static final PokemonSpec UNTRADABLE = new PokemonSpec("untradeable");

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException((Text)Text.of((String)"Only players can add items to the gts!"));
        }
        Player player = (Player)src;
        if (BattleRegistry.getBattle((EntityPlayer)((EntityPlayer)player)) != null) {
            throw new CommandException(Chat.embedColours((String)"&4You cannot add a Pokemon to GTS while in battle."));
        }
        int total = (int)GTS.getInstance().getSql().getAllListings().stream().filter(listing -> listing.getOwner().equals(player.getUniqueId())).count();
        if (ConfigManager.maxListings != -1 && total >= ConfigManager.maxListings) {
            throw new CommandException((Text)Text.of((String)MessageConfig.getMessages("Messages.Sell-Item.Has-Too-Many")));
        }
        int slot = (Integer)args.getOne("slot").get() - 1;
        if (slot < 0 || slot > 5) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "Slot number must be between 1 and 6!"}));
        }
        String pokemon = (String)args.getOne("pokemon").get();
        if (!EnumSpecies.hasPokemonAnyCase((String)pokemon)) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "Your requested Pok\u00e9mon was not valid. Try again!"}));
        }
        pokemon = ((EnumSpecies)EnumSpecies.getFromName((String)pokemon).get()).name;
        PlayerPartyStorage storage = ListingUtils.getPlayerStorage((EntityPlayerMP)player);
        Pokemon pkm = storage.get(slot);
        if (pkm == null) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "Unable to find a Pok\u00e9mon in the specified slot."}));
        }
        if (storage.getTeam().size() == 1 && !pkm.isEgg()) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "You can't sell your last non-egg party member!"}));
        }
        if (pkm.isEgg()) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "Unable to offer a trade using a Pok\u00e9mon Egg!"}));
        }
        if (UNTRADABLE.matches(pkm)) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "This Pok\u00e9mon is marked as untradeable, and cannot be sold..."}));
        }
        player.openInventory(new Trade(player, pkm, pokemon).getInventory());
        return CommandResult.success();
    }
}


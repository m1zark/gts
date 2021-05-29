/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Help
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.YELLOW, "-------------------", TextColors.AQUA, "GTS Help", TextColors.YELLOW, "--------------------"}));
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.YELLOW, "{}", TextColors.GRAY, "= Required   ", TextColors.YELLOW, "<>", TextColors.GRAY, "= Optional   ", TextColors.YELLOW, "... ", TextColors.GRAY, "= Accepts more than 1"}));
        src.sendMessage(Text.EMPTY);
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Open the GTS market"}));
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts add|sell {slot} {price} ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Add a Pok\u00e9mon to the GTS"}));
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts trade {slot} {pokemon} ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Add a trade listing to the GTS."}));
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts search {pokemon} {pokemon...} ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Search for Pok\u00e9mon in the GTS"}));
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts search {player} {player_name} ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Search for players in the GTS"}));
        src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts auc {slot} {price} {increment} ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Auction Pok\u00e9mon."}));
        if (src.hasPermission("gts.admin")) {
            src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts clear ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Clear all entries in the GTS"}));
            src.sendMessage(Text.of((Object[])new Object[]{TextColors.AQUA, "/gts reload ", TextColors.GRAY, "\u00bb", TextColors.WHITE, " Reload configuration related to GTS"}));
        }
        src.sendMessage(Text.EMPTY);
        return CommandResult.success();
    }
}


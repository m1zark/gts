/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.GTS;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.m1utilities.api.Chat;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class Clear
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional type = args.getOne((Text)Text.of((String)"type"));
        if (type.isPresent()) {
            if (((String)type.get()).equals("all")) {
                GTS.getInstance().getSql().getAllListings().forEach(listing -> GTS.getInstance().getSql().addCacheListing(listing.getOwner(), new Pokemon(ListingUtils.getNbt(listing.getPokemon()).toString())));
                GTS.getInstance().getSql().clearTables();
            } else if (((String)type.get()).equals("logs")) {
                GTS.getInstance().getSql().removeAllLogs();
            }
        }
        Chat.sendMessage((CommandSource)src, (String)"&7All data has been deleted.");
        return CommandResult.success();
    }
}


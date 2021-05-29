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
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.guis.MainUI;
import com.m1zark.m1utilities.api.Chat;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class Search
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException((Text)Text.of((String)"Only players may use this command."));
        }
        Chat.sendMessage((CommandSource)src, (String)"&a/market search <item|player> <search>");
        return CommandResult.success();
    }

    public static class NameSearch
    implements CommandExecutor {
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException((Text)Text.of((String)"Only players can open the GTS listings!"));
            }
            Optional ou = args.getOne((Text)Text.of((String)"player"));
            if (ou.isPresent()) {
                ((Player)src).openInventory(new MainUI((Player)src, 1, true, "player", ((User)ou.get()).getUniqueId().toString()).getInventory());
            } else {
                Chat.sendMessage((CommandSource)src, (String)"&cInvalid player name.");
            }
            return CommandResult.success();
        }
    }

    public static class PokemonSearch
    implements CommandExecutor {
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (!(src instanceof Player)) {
                throw new CommandException((Text)Text.of((String)"Only players can open the GTS listings!"));
            }
            Optional oit = args.getOne((Text)Text.of((String)"pokemon"));
            if (oit.isPresent()) {
                ((Player)src).openInventory(new MainUI((Player)src, 1, true, "pokemon", (String)oit.get()).getInventory());
            } else {
                Chat.sendMessage((CommandSource)src, (String)"&cYou must enter something to search for!");
            }
            return CommandResult.success();
        }
    }
}


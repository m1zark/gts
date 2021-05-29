/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.guis.MainUI;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class GTSOpen
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException((Text)Text.of((String)"Only players can open the GTS listings!"));
        }
        ((Player)src).openInventory(new MainUI((Player)src, 1).getInventory());
        return CommandResult.success();
    }
}


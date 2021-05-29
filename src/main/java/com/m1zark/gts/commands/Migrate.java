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
 */
package com.m1zark.gts.commands;

import com.m1zark.m1utilities.api.Chat;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Migrate
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        com.m1zark.gts.utils.storage.Migrate.migrateMain();
        com.m1zark.gts.utils.storage.Migrate.migrateCache();
        Chat.sendMessage((CommandSource)src, (String)"&7All GTS data has been migrated to H2.");
        return CommandResult.success();
    }
}


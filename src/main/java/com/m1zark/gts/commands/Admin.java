/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.service.user.UserStorageService
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.guis.AdminUI;
import com.m1zark.m1utilities.api.Chat;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

public class Admin
implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException((Text)Text.of((String)"Only players may use this command."));
        }
        Optional<Player> player = args.getOne((Text)Text.of((String)"player"));
        player.ifPresent(p -> {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            Optional<User> oUser = userStorage.get(p.getUniqueId());
            if (oUser.isPresent()) {
                ((Player)src).openInventory(new AdminUI((Player)src, 1, (User)oUser.get()).getInventory());
            } else {
                Chat.sendMessage((CommandSource)src, (String)("&7Unable to locate player &b" + p));
            }
        });
        return CommandResult.success();
    }
}


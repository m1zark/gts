/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandCallable
 *  org.spongepowered.api.command.args.CommandElement
 *  org.spongepowered.api.command.args.GenericArguments
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.command.spec.CommandSpec
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.commands;

import com.m1zark.gts.GTS;
import com.m1zark.gts.GTSInfo;
import com.m1zark.gts.commands.AddCommand;
import com.m1zark.gts.commands.Admin;
import com.m1zark.gts.commands.Auction;
import com.m1zark.gts.commands.Clear;
import com.m1zark.gts.commands.GTSOpen;
import com.m1zark.gts.commands.Help;
import com.m1zark.gts.commands.Logs;
import com.m1zark.gts.commands.Migrate;
import com.m1zark.gts.commands.Reload;
import com.m1zark.gts.commands.Search;
import com.m1zark.gts.commands.TradeCommand;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {
    private Map<String, Integer> arg1 = new HashMap<String, Integer>(){
        {
            this.put("1", 1);
            this.put("2", 2);
            this.put("3", 3);
            this.put("4", 4);
            this.put("5", 5);
            this.put("6", 6);
        }
    };
    private Map<String, String> arg2 = new HashMap<String, String>(){
        {
            this.put("all", "all");
            this.put("logs", "logs");
        }
    };
    CommandSpec admin = CommandSpec.builder().permission("gts.admin.listings").arguments(GenericArguments.string((Text)Text.of((String)"player"))).executor((CommandExecutor)new Admin()).build();
    CommandSpec logs = CommandSpec.builder().permission("gts.admin.logs").arguments(GenericArguments.string((Text)Text.of((String)"player"))).executor((CommandExecutor)new Logs()).build();
    CommandSpec add = CommandSpec.builder().arguments(new CommandElement[]{GenericArguments.choices((Text)Text.of((String)"slot"), this.arg1), GenericArguments.integer((Text)Text.of((String)"price"))}).executor((CommandExecutor)new AddCommand()).permission("gts.user.sell").description((Text)Text.of((String)"Adds new listing to gts.")).build();
    CommandSpec auction = CommandSpec.builder().arguments(new CommandElement[]{GenericArguments.choices((Text)Text.of((String)"slot"), this.arg1), GenericArguments.integer((Text)Text.of((String)"price")), GenericArguments.integer((Text)Text.of((String)"increment"))}).executor((CommandExecutor)new Auction()).permission("gts.user.auctions").description((Text)Text.of((String)"Starts a new Pokemon auction.")).build();
    CommandSpec trade = CommandSpec.builder().executor((CommandExecutor)new TradeCommand()).arguments(new CommandElement[]{GenericArguments.choices((Text)Text.of((String)"slot"), this.arg1), GenericArguments.withSuggestions((CommandElement)GenericArguments.string((Text)Text.of((String)"pokemon")), (Iterable)EnumSpecies.getNameList())}).permission("gts.user.trade").description((Text)Text.of((String)"Adds new trade listing to gts.")).build();
    CommandSpec pokemonSearch = CommandSpec.builder().executor((CommandExecutor)new Search.PokemonSearch()).arguments(GenericArguments.remainingJoinedStrings((Text)Text.of((String)"pokemon"))).description((Text)Text.of((String)"List all gts listings for a specific pokemon.")).build();
    CommandSpec nameSearch = CommandSpec.builder().executor((CommandExecutor)new Search.NameSearch()).arguments(GenericArguments.user((Text)Text.of((String)"player"))).description((Text)Text.of((String)"List all gts listings for a specific player.")).build();
    CommandSpec search = CommandSpec.builder().permission("gts.user.search").description((Text)Text.of((String)"List all search options.")).child((CommandCallable)this.pokemonSearch, new String[]{"pokemon"}).child((CommandCallable)this.nameSearch, new String[]{"player"}).executor((CommandExecutor)new Search()).build();
    CommandSpec help = CommandSpec.builder().permission("gts.user.help").description((Text)Text.of((String)"Displays command info in chat.")).executor((CommandExecutor)new Help()).build();
    CommandSpec reload = CommandSpec.builder().permission("gts.admin.reload").description((Text)Text.of((String)"Reload configuration related to GTS")).executor((CommandExecutor)new Reload()).build();
    CommandSpec clear = CommandSpec.builder().permission("gts.admin.clear").arguments(GenericArguments.choices((Text)Text.of((String)"type"), this.arg2)).description((Text)Text.of((String)"Clear all entries in the GTS")).executor((CommandExecutor)new Clear()).build();
    CommandSpec migrate = CommandSpec.builder().permission("gts.admin.migrate").description((Text)Text.of((String)"")).executor((CommandExecutor)new Migrate()).build();
    CommandSpec main = CommandSpec.builder().description((Text)Text.of((String)"GTS main command.")).child((CommandCallable)this.reload, new String[]{"reload"}).child((CommandCallable)this.clear, new String[]{"clear"}).child((CommandCallable)this.add, new String[]{"add", "sell"}).child((CommandCallable)this.trade, new String[]{"trade"}).child((CommandCallable)this.search, new String[]{"search"}).child((CommandCallable)this.help, new String[]{"help"}).child((CommandCallable)this.migrate, new String[]{"migrate"}).child((CommandCallable)this.admin, new String[]{"listings"}).child((CommandCallable)this.auction, new String[]{"auc"}).executor((CommandExecutor)new GTSOpen()).build();

    public void registerCommands(GTS plugin) {
        Sponge.getCommandManager().register((Object)plugin, (CommandCallable)this.main, new String[]{"gts"});
        Sponge.getCommandManager().register((Object)plugin, (CommandCallable)this.auction, new String[]{"pixelauction", "pauc", "pixelauc", "auction"});
        GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.PREFIX, "Registering commands..."})}));
    }
}


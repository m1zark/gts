/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.inject.Inject
 *  org.slf4j.Logger
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.source.ConsoleSource
 *  org.spongepowered.api.config.ConfigDir
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.Order
 *  org.spongepowered.api.event.cause.Cause
 *  org.spongepowered.api.event.cause.EventContext
 *  org.spongepowered.api.event.game.GameReloadEvent
 *  org.spongepowered.api.event.game.state.GameInitializationEvent
 *  org.spongepowered.api.event.game.state.GameStartedServerEvent
 *  org.spongepowered.api.event.game.state.GameStoppingEvent
 *  org.spongepowered.api.event.game.state.GameStoppingServerEvent
 *  org.spongepowered.api.plugin.Plugin
 *  org.spongepowered.api.plugin.PluginContainer
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.m1zark.gts.GTSInfo;
import com.m1zark.gts.auctions.Auctions;
import com.m1zark.gts.commands.CommandManager;
import com.m1zark.gts.listeners.PlayerListeners;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.gts.utils.storage.DataSource;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

@Plugin(id="gts", name="GTS", version="1.4.0-S7.2", authors={"m1zark"}, description="Buying and selling of Pokemon via in-game market UI.")
public class GTS {
    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot=false)
    private Path configDir;
    private ConfigManager config;
    private MessageConfig msgConfig;
    private static GTS instance;
    @Inject
    private PluginContainer pluginContainer;
    private DataSource sql;
    private Cause gtsCause;
    private boolean enabled = true;
    private List<Auctions> currentAuctions = Lists.newArrayList();
    public Gson gson = new Gson();

    @Listener
    public void onInitialization(GameInitializationEvent e) {
        instance = this;
        this.gtsCause = Cause.builder().append((Object)this).build(EventContext.builder().build());
        GTSInfo.startup();
        this.enabled = GTSInfo.dependencyCheck();
        if (this.enabled) {
            this.config = new ConfigManager();
            this.msgConfig = new MessageConfig();
            this.sql = new DataSource("GTS_Listings", "GTS_Cache", "GTS_Logs");
            this.sql.createTables();
            this.getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.PREFIX, "Initializing database..."})}));
            Sponge.getEventManager().registerListeners((Object)this, (Object)new PlayerListeners());
            new CommandManager().registerCommands(this);
            this.getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.PREFIX, "Initialization complete!"})}));
        }
    }

    @Listener(order=Order.POST)
    public void postGameStart(GameStartedServerEvent event) {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (!this.getCurrentAuctions().isEmpty() && this.getCurrentAuctions().get(0).getAuctionTime().equals("Expired")) {
                this.getCurrentAuctions().get(0).processAuction();
            }
        }).interval(1L, TimeUnit.SECONDS).name("auctions").submit((Object)this);
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        if (this.enabled) {
            this.config = new ConfigManager();
            this.msgConfig = new MessageConfig();
            this.getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.PREFIX, "Configurations have been reloaded"})}));
        }
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent e) {
        if (!this.getCurrentAuctions().isEmpty()) {
            this.getCurrentAuctions().forEach(auction -> this.getSql().addCacheListing(auction.getSeller(), new Pokemon(ListingUtils.getNbt(auction.getPokemon()).toString())));
        }
    }

    @Listener
    public void onServerStop(GameStoppingEvent e) {
        try {
            this.sql.shutdown();
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static GTS getInstance() {
        return instance;
    }

    public Optional<ConsoleSource> getConsole() {
        return Optional.ofNullable(Sponge.isServerAvailable() ? Sponge.getServer().getConsole() : null);
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Path getConfigDir() {
        return this.configDir;
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public MessageConfig getMsgConfig() {
        return this.msgConfig;
    }

    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    public DataSource getSql() {
        return this.sql;
    }

    public Cause getGtsCause() {
        return this.gtsCause;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<Auctions> getCurrentAuctions() {
        return this.currentAuctions;
    }

    public Gson getGson() {
        return this.gson;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  ninja.leaping.configurate.ConfigurationNode
 *  ninja.leaping.configurate.ConfigurationOptions
 *  ninja.leaping.configurate.commented.CommentedConfigurationNode
 *  ninja.leaping.configurate.hocon.HoconConfigurationLoader
 *  ninja.leaping.configurate.hocon.HoconConfigurationLoader$Builder
 *  ninja.leaping.configurate.loader.ConfigurationLoader
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.utils.configuration;

import com.m1zark.gts.GTS;
import com.m1zark.gts.GTSInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.Text;

public class ConfigManager {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    public static CommentedConfigurationNode main;
    public static int maxListings;
    public static long listingsTime;
    public static boolean allowEgg;
    public static int minNormal;
    public static int minLegend;
    public static int minHA;
    public static long auctionLength;
    public static int minBidIncrement;
    public static int maxAuctions;
    public static int removeExpired;
    public static String mysqlURL;
    public static String mysqlUsername;
    public static String mysqlPassword;

    public ConfigManager() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(GTS.getInstance().getConfigDir() + "/settings.conf", new String[0]);
        loader = ((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().setPath(configFile)).build();
        try {
            if (!Files.exists(GTS.getInstance().getConfigDir(), new LinkOption[0])) {
                Files.createDirectory(GTS.getInstance().getConfigDir(), new FileAttribute[0]);
            }
            if (!Files.exists(configFile, new LinkOption[0])) {
                Files.createFile(configFile, new FileAttribute[0]);
            }
            if (main == null) {
                main = (CommentedConfigurationNode)loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            }
            CommentedConfigurationNode storage = main.getNode(new Object[]{"Storage"});
            storage.getNode(new Object[]{"storageType"}).setComment("Types: h2, mysql").getString("h2");
            mysqlURL = storage.getNode(new Object[]{"MYSQL", "URL"}).getString("[host]:[port]/[database]");
            mysqlUsername = storage.getNode(new Object[]{"MYSQL", "Username"}).getString("");
            mysqlPassword = storage.getNode(new Object[]{"MYSQL", "Password"}).getString("");
            CommentedConfigurationNode general = main.getNode(new Object[]{"General"});
            listingsTime = general.getNode(new Object[]{"ListingsTime"}).setComment("Sets the default duration of a listing (In Minutes)").getLong(4320L) * 60L;
            maxListings = general.getNode(new Object[]{"MaxListings"}).setComment("Set how many listings a player is allowed to have, -1 for no limit").getInt(-1);
            allowEgg = general.getNode(new Object[]{"AllowEggs"}).setComment("Whether players can add Pokemon Eggs to the GTS").getBoolean(true);
            removeExpired = general.getNode(new Object[]{"removeExpiredLogs"}).setComment("Sets the time (in days) in which to auto delete older logs.").getInt(30);
            general.getNode(new Object[]{"Min-Prices"}).setComment("Use these values to enforce minimum prices.");
            minNormal = general.getNode(new Object[]{"Min-Prices", "Normal Pokemon"}).getInt(1000);
            minHA = general.getNode(new Object[]{"Min-Prices", "HAs"}).getInt(10000);
            minLegend = general.getNode(new Object[]{"Min-Prices", "Legendary Pokemon"}).getInt(30000);
            CommentedConfigurationNode auctions = main.getNode(new Object[]{"auctions"});
            auctionLength = auctions.getNode(new Object[]{"ListingsTime"}).setComment("Sets the default duration of an auction (In Seconds)").getLong(60L);
            maxAuctions = auctions.getNode(new Object[]{"MaxAuctions"}).setComment("Max number of auctions a player can have at one time").getInt(3);
            minBidIncrement = auctions.getNode(new Object[]{"minBidIncrement"}).getInt(500);
            loader.save((ConfigurationNode)main);
        }
        catch (IOException e) {
            GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.ERROR_PREFIX, "There was an issue loading the config..."})}));
            e.printStackTrace();
        }
        GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.PREFIX, "Loading configuration..."})}));
    }

    public static void saveConfig() {
        try {
            loader.save((ConfigurationNode)main);
        }
        catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = (CommentedConfigurationNode)loader.load();
        }
        catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public static String getStorageType() {
        return main.getNode(new Object[]{"Storage", "storageType"}).getString();
    }
}


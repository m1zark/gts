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

public class MessageConfig {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    public static CommentedConfigurationNode main;

    public MessageConfig() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(GTS.getInstance().getConfigDir() + "/messages.conf", new String[0]);
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
            CommentedConfigurationNode messages = main.getNode(new Object[]{"Messages"});
            messages.getNode(new Object[]{"ServerAnnounce"}).getBoolean(true);
            messages.getNode(new Object[]{"General", "Expired"}).getString("&dGTS &e\u00bb &7This item has expired. Please refresh the listing.");
            messages.getNode(new Object[]{"General", "Sold"}).getString("&dGTS &e\u00bb &7This item has already been sold. Please refresh the listings.");
            messages.getNode(new Object[]{"General", "Returned-Pokemon"}).getString("&dGTS &e\u00bb &7You have received a Pok\u00e9mon from the GTS. It has been added to your party/pc.");
            messages.getNode(new Object[]{"Admin", "Remove-Listing"}).getString("&dGTS &e\u00bb &cSuccessfully removed the Pok\u00e9mon from the listings. It will be returned to them on there next login.");
            messages.getNode(new Object[]{"Admin", "Remove-Listing-Now"}).getString("&dGTS &e\u00bb &cSuccessfully removed the Pok\u00e9mon from the listings and returned it to them.");
            messages.getNode(new Object[]{"Admin", "Remove-Listing-PlayerMessage"}).getString("&dGTS &e\u00bb &cYour Pok\u00e9mon has been removed from the GTS by an Admin and returned to you.");
            messages.getNode(new Object[]{"Admin", "Mark-Expired"}).getString("&dGTS &e\u00bb &cSuccessfully marked the Pok\u00e9mon as expired.");
            messages.getNode(new Object[]{"Trade-Item", "Announce-Added"}).getString("&dGTS &e\u00bb &7{player} has added a &b{ability} {IV%} IV {shiny}{pokemon} &7to the gts for Trade.");
            messages.getNode(new Object[]{"Trade-Item", "Success-Add"}).getString("&dGTS &e\u00bb &7Your &b{pokemon} &7 has been added to the gts for Trade.");
            messages.getNode(new Object[]{"Trade-Item", "Invalid-Pokemon"}).getString("&dGTS &e\u00bb &7You don't have the requested Pok\u00e9mon in your party.");
            messages.getNode(new Object[]{"Trade-Item", "Success-Player"}).getString("&dGTS &e\u00bb &7You successfully traded your &b{pokemon1} &7for a &b{pokemon2}");
            messages.getNode(new Object[]{"Trade-Item", "Success-Owner"}).getString("&dGTS &e\u00bb &7Your &b{ability} {IV%} IV {shiny}{pokemon} has been successfully traded.");
            messages.getNode(new Object[]{"Sell-Item", "Error"}).getString("&dGTS &e\u00bb &7There was an error trying to create this listing. Please try again.");
            messages.getNode(new Object[]{"Sell-Item", "Success"}).getString("&dGTS &e\u00bb &7Your &b{pokemon} &7 has been added to the gts for {price}.");
            messages.getNode(new Object[]{"Sell-Item", "Has-Too-Many"}).getString("&dGTS &e\u00bb &7You already have the max number of Pokemon in GTS.");
            messages.getNode(new Object[]{"Sell-Item", "Announce-Added"}).getString("&dGTS &e\u00bb &7{player} has added a &b{ability} {IV%} IV {shiny}{pokemon} &7to the gts for {price}.");
            messages.getNode(new Object[]{"Sell-Item", "Announce-Added-Egg"}).getString("&dGTS &e\u00bb &7{player} has added a &b{pokemon} &7to the gts for {price}.");
            messages.getNode(new Object[]{"Sell-Item", "Negative-Price"}).getString("&dGTS &e\u00bb &7You can't sell this Pok\u00e9mon for a negative price. Please try again.");
            messages.getNode(new Object[]{"Sell-Item", "Minimum-Price"}).getString("&dGTS &e\u00bb &7Sorry, but that price is below the minimum price of &a{price} required.");
            messages.getNode(new Object[]{"Sell-Item", "Sell-Egg"}).getString("&dGTS &e\u00bb Unable to add Pok\u00e9mon Eggs to the GTS.");
            messages.getNode(new Object[]{"Sell-Item", "Last-Member"}).getString("&dGTS &e\u00bb You can't sell your last party member!");
            messages.getNode(new Object[]{"Purchase-Items", "Success"}).getString("&dGTS &e\u00bb &7You successfully purchased a {pokemon} for P{price}.");
            messages.getNode(new Object[]{"Purchase-Items", "Own-Item"}).getString("&dGTS &e\u00bb &7You can't purchase your own Pok\u00e9mon. Please use the player listings to remove an Pok\u00e9mon.");
            messages.getNode(new Object[]{"Purchase-Items", "Not-Enough-Money"}).getString("&dGTS &e\u00bb &7Unable to purchase Pok\u00e9mon. Make sure you have enough money to buy it.");
            messages.getNode(new Object[]{"Purchase-Items", "Seller-Message"}).getString("&dGTS &e\u00bb &7Your {pokemon} was purchased for {price}.");
            messages.getNode(new Object[]{"Player-Listings", "Remove-Success"}).getString("&dGTS &e\u00bb &7Your {pokemon} has been removed from the GTS and sent to your party.");
            messages.getNode(new Object[]{"Player-Listings", "Relist-Item"}).getString("&dGTS &e\u00bb &7Your {pokemon} has been successfully re-listed on the GTS.");
            messages.getNode(new Object[]{"Player-Listings", "Not-Expired"}).getString("&dGTS &e\u00bb &7Your {pokemon} hasn't expired yet. Unable to re-list it.");
            messages.getNode(new Object[]{"auctions", "New-Auction-Broadcast"}).getString("&dAuctions &e\u00bb &7{player} has added a &b{ability} {IV%} IV {shiny}{pokemon} &7to the auctions.");
            messages.getNode(new Object[]{"auctions", "New-Auction-Player"}).getString("&dAuctions &e\u00bb &7Your &b{pokemon} &7has been added to the auction que.");
            messages.getNode(new Object[]{"auctions", "Minimum-Price"}).getString("&dAuctions &e\u00bb &7Sorry, but that price is below the minimum price of &a{price} &7required.");
            messages.getNode(new Object[]{"auctions", "Auction-Won-Broadcast"}).getString("&dAuctions &e\u00bb &7{player}'s {pokemon} was sold to {player2} for P{price}.");
            messages.getNode(new Object[]{"auctions", "Auction-NoSale"}).getString("&dAuctions &e\u00bb &7Your auction has ended with no bidders. Your &b{pokemon} &7has been returned.");
            messages.getNode(new Object[]{"auctions", "Max-auctions"}).getString("&dAuctions &e\u00bb &7You have the maximum number of auctions. Wait til they finish before adding new ones.");
            messages.getNode(new Object[]{"auctions", "Not-Enough-Money-Seller"}).getString("&dAuctions &e\u00bb &7Your auction for {pokemon} couldn't complete due to insufficient funds.");
            messages.getNode(new Object[]{"auctions", "Not-Enough-Money-Bidder"}).getString("&dAuctions &e\u00bb &7You could not complete the auction for {pokemon} due to insufficient funds.");
            loader.save((ConfigurationNode)main);
        }
        catch (IOException e) {
            GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.ERROR_PREFIX, "There was an issue loading the config..."})}));
            e.printStackTrace();
        }
        GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.PREFIX, "Loading message configuration..."})}));
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

    public static boolean getServerAnnounce() {
        return main.getNode(new Object[]{"Messages", "ServerAnnounce"}).getBoolean();
    }

    public static String getMessages(String path) {
        return main.getNode((Object[])path.split("\\.")).getString();
    }
}


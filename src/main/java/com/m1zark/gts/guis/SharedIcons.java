/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.Money
 *  com.m1zark.m1utilities.api.Time
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  net.minecraft.util.text.TextFormatting
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.data.type.DyeColor
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.guis;

import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.utils.Listing;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.PokeRequest;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.Money;
import com.m1zark.m1utilities.api.Time;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SharedIcons {
    public static Icon BorderIcon(int slot, DyeColor color, boolean display) {
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aTo list a Pok\u00e9mon on the GTS use the command")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&b/gts sell <slot> <price>")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aTo setup a Pok\u00e9mon Trade use the command")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&b/gts trade <slot> <pokemon>")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aTo setup a Pok\u00e9mon Auction use the command")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&b/gts auc <slot> <price> <bid increment>")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aTo search for an Pok\u00e9mon, or for specific")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aplayer listings, use the command:")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&b/gts search <pokemon | name> <pokemon name | player name>")}));
        ItemStack icon = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).build();
        icon.offer(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{Chat.embedColours((String)(display ? "&l&cHow to list a Pok\u00e9mon:" : "&0"))}));
        icon.offer(Keys.DYE_COLOR, color);
        if (display) {
            icon.offer(Keys.ITEM_LORE, itemLore);
        }
        return new Icon(slot, icon);
    }

    public static Icon confetti(int slot) {
        DyeColor[] colors = new DyeColor[]{DyeColors.BLUE, DyeColors.CYAN, DyeColors.LIME, DyeColors.LIGHT_BLUE, DyeColors.MAGENTA, DyeColors.ORANGE, DyeColors.PINK, DyeColors.PURPLE, DyeColors.RED, DyeColors.YELLOW};
        ItemStack g = ItemStack.builder().itemType(ItemTypes.WOOL).add(Keys.DYE_COLOR, colors[(int)Math.floor(Math.random() * (double)colors.length)]).build();
        g.offer(Keys.DISPLAY_NAME, Text.of((String)("auctions - " + slot)));
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&a" + Instant.now().toEpochMilli()))}));
        g.offer(Keys.ITEM_LORE, itemLore);
        return new Icon(slot, g);
    }

    static Icon pageIcon(int slot, boolean nextOrLast) {
        return new Icon(slot, ItemStack.builder().itemType((ItemType)Sponge.getRegistry().getType(ItemType.class, nextOrLast ? "pixelmon:trade_holder_right" : "pixelmon:trade_holder_left").get()).quantity(1).add(Keys.DISPLAY_NAME, (nextOrLast ? Text.of((Object[])new Object[]{TextColors.GREEN, "\u2192 ", "Next Page", TextColors.GREEN, " \u2192"}) : Text.of((Object[])new Object[]{TextColors.RED, "\u2190 ", "Previous Page", TextColors.RED, " \u2190"}))).build());
    }

    static Icon refreshIcon(int slot) {
        return new Icon(slot, ItemStack.builder().itemType((ItemType)Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_panel").get()).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, "Refresh Listings"})).build());
    }

    static Icon playerListingsIcon(int slot, Player player) {
        ItemStack icon = ItemStack.builder().itemType((ItemType)Sponge.getRegistry().getType(ItemType.class, "pixelmon:pc").get()).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextFormatting.BOLD, "My Listings"})).build();
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Balance: &eP" + Money.getBalance((User)player)))}));
        List<Listing> total = GTS.getInstance().getSql().getAllListings().stream().filter(listing -> listing.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());
        int expired = (int) total.stream().filter(listing-> listing.checkHasExpired()).count();
        String maxListings = ConfigManager.maxListings == -1 ? "\u221e" : String.valueOf(ConfigManager.maxListings);
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Total Listings: &b" + total.size() + "&7/&b" + maxListings))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Total Expired Listings: &b" + expired + "&7/&b" + total.size()))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bClick here to view/edit all the items you are currently selling.")}));
        icon.offer(Keys.ITEM_LORE, itemLore);
        return new Icon(slot, icon);
    }

    static Icon marketInfo(int slot) {
        ItemStack icon = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:water_gem").get()).build();
        icon.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&c&lLooking to buy/sell items?")));
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of(Chat.embedColours("&aClick this icon to open the Market.")));
        icon.offer(Keys.ITEM_LORE, itemLore);
        return new Icon(slot, icon);
    }

    public static Icon listingInformation(int slot, Listing item) {
        Time time = new Time(item.getExpires().toInstant().toEpochMilli());
        ArrayList<Text> itemLore = new ArrayList<Text>();
        //itemLore.add(Text.of(Chat.embedColours(((item.getisTrade() ? "&7Trader: " : "&7Seller: ") + "&b" + ListingUtils.getNameFromUUID(item.getOwner()).get()))));
        if (item.getisTrade()) {
            PokeRequest request = item.getRequest();
            String pokeGender = request.getGender().equals("Male") ? "&b" + request.getGender() + " \u2642" : (request.getGender().equals("Female") ? "&d" + request.getGender() + " \u2640" : (request.getGender().equals("Genderless") ? "&8Genderless \u26a5" : ""));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7Looking for: &b")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("    &b" + ListingUtils.updatePokemonName(request.getName()) + " &7| &eLvl " + request.getLevel() + (request.getLevel() < 100 ? "+" : "") + (request.getShiny() ? " &7(&6Shiny&7)&r" : "")))}));
            if (!request.getAbility().equals("")) {
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("    &7Ability: &e" + request.getAbility() + (ListingUtils.isHiddenAbility(Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase((String)request.getName())).getBaseStats(), request.getAbility()) ? " &7(&6HA&7)&r" : "")))}));
            }
            if (!request.getNature().equals("")) {
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("    &7Nature: &e" + request.getNature()))}));
            }
            if (!request.getGender().equals("")) {
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("    &7Gender: " + pokeGender))}));
            }
        } else {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Price: &bP" + item.getPrice()))}));
        }
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Expires: &b" + time.toString("%1$dd %2$dh %3$dm")))}));
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.PAPER).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{Chat.embedColours((String)"&bListing Information")})).add(Keys.ITEM_LORE, itemLore).build());
    }

    public static Icon confirmIcon(int slot, Listing listing) {
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{TextColors.GRAY, "Click here to confirm purchase."}));
        if (listing.getisTrade()) {
            itemLore.add(Text.of());
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cNote: This will search your party for the required Pok\u00e9mon.")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cNote: It will choose the 1st Pok\u00e9mon that matches the criteria.")}));
        }
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.STAINED_HARDENED_CLAY).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GREEN, "Confirm Purchase"})).add(Keys.DYE_COLOR, DyeColors.LIME).add(Keys.ITEM_LORE, itemLore).build());
    }

    public static Icon cancelIcon(int slot) {
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.STAINED_HARDENED_CLAY).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.RED, "Cancel Purchase"})).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.ITEM_LORE, Lists.newArrayList(Text.of((Object[])new Object[]{TextColors.GRAY, "Click here to cancel purchase."}))).build());
    }

    static Icon lastMenu(int slot) {
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.STAINED_HARDENED_CLAY).add(Keys.DYE_COLOR, DyeColors.RED).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.RED, "Back to Main Menu"})).build());
    }
}


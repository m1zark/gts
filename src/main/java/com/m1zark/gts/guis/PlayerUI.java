/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.DataQuery
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Primary
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Secondary
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStackSnapshot
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.guis;

import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.guis.MainUI;
import com.m1zark.gts.guis.SharedIcons;
import com.m1zark.gts.utils.Listing;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public class PlayerUI
extends InventoryManager {
    private Player player;
    private int page;
    private int maxPage;
    private boolean searching;
    private String searchType;
    private String parameters;

    public PlayerUI(Player player, int page) {
        super(player, 6, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lGTS &l&0\u27a5&r &8Player Listings")}));
        this.page = page;
        this.player = player;
        this.searching = false;
        this.searchType = null;
        this.parameters = null;
        ArrayList valid = Lists.newArrayList();
        GTS.getInstance().getSql().getAllListings().forEach(lot -> {
            if (lot.getOwner().equals(player.getUniqueId())) {
                valid.add(lot);
            }
        });
        this.maxPage = valid.size() % 36 == 0 && valid.size() / 36 != 0 ? valid.size() / 36 : valid.size() / 36 + 1;
        this.setupInventory();
        this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
        this.setupListings(player);
    }

    public PlayerUI(Player player, int page, boolean searching, String searchType, String parameters) {
        super(player, 6, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lGTS &l&0\u27a5&r &8Player Listings")}));
        this.page = page;
        this.player = player;
        this.searching = searching;
        this.searchType = searchType;
        this.parameters = parameters;
        ArrayList valid = Lists.newArrayList();
        GTS.getInstance().getSql().getAllListings().forEach(lot -> {
            if (lot.getOwner().equals(player.getUniqueId())) {
                valid.add(lot);
            }
        });
        this.maxPage = valid.size() % 36 == 0 && valid.size() / 36 != 0 ? valid.size() / 36 : valid.size() / 36 + 1;
        this.setupInventory();
        this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
        this.setupListings(player);
    }

    private void setupInventory() {
        int y = 4;
        for (int x = 0; x < 9; ++x) {
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.RED, true));
        }
        this.addIcon(SharedIcons.playerListingsIcon(45, this.player));
        Icon previousPage = SharedIcons.pageIcon(48, false);
        previousPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(false)).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(previousPage);
        Icon refresh = SharedIcons.refreshIcon(49);
        refresh.addListener(clickable -> {
            Player p = clickable.getPlayer();
            this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
            this.setupListings(p);
            this.updateContents();
        });
        this.addIcon(refresh);
        Icon nextPage = SharedIcons.pageIcon(50, true);
        nextPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(true)).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(nextPage);
        Icon lastMenuIcon = SharedIcons.lastMenu(53);
        lastMenuIcon.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            Player p = clickable.getPlayer();
            p.openInventory(new MainUI(p, 1, this.searching, this.searchType, this.parameters).getInventory());
        }).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(lastMenuIcon);
    }

    private void setupListings(Player player) {
        int index = (this.page - 1) * 36;
        List listings = GTS.getInstance().getSql().getAllListings().stream().filter(listing -> listing.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 9 && index < listings.size(); ++x, ++index) {
                Listing listing2 = (Listing)listings.get(index);
                Icon item = new Icon(x + 9 * y, listing2.getPkmItem().getSprite(1, (Listing)listings.get(index)));
                item.addListener(clickable -> {
                    Player p = clickable.getPlayer();
                    if (!((ItemStackSnapshot)clickable.getEvent().getCursorTransaction().getFinal()).getType().equals(ItemTypes.NONE)) {
                        String ListingID = (String)((ItemStackSnapshot)clickable.getEvent().getCursorTransaction().getFinal()).toContainer().getString(DataQuery.of((String[])new String[]{"UnsafeData", "ListingID"})).get();
                        Optional<Listing> itemCache = GTS.getInstance().getSql().getAllListings().stream().filter(listing1 -> listing1.getListingID() == Integer.valueOf(ListingID).intValue()).findFirst();
                        if (!itemCache.isPresent()) {
                            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.General.Sold"));
                        } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary) {
                            if (itemCache.get().checkHasExpired()) {
                                GTS.getInstance().getSql().updateListing(itemCache.get().getListingID(), false);
                                Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Player-Listings.Relist-Item").replace("{pokemon}", itemCache.get().getPokemon().isEgg() ? "Pok\u00e9mon Egg" : itemCache.get().getPokemon().getDisplayName()));
                                Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                    this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
                                    this.setupListings(p);
                                    this.updateContents();
                                }).delayTicks(1L).submit((Object)GTS.getInstance());
                            } else {
                                Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Player-Listings.Not-Expired").replace("{pokemon}", itemCache.get().getPokemon().isEgg() ? "Pok\u00e9mon Egg" : itemCache.get().getPokemon().getDisplayName()));
                            }
                        } else if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                            this.returnPokemon(itemCache.get());
                            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Player-Listings.Remove-Success").replace("{pokemon}", itemCache.get().getPokemon().isEgg() ? "Pok\u00e9mon Egg" : itemCache.get().getPokemon().getDisplayName()));
                            GTS.getInstance().getSql().removeListing(itemCache.get().getListingID());
                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
                                this.setupListings(p);
                                this.updateContents();
                            }).delayTicks(1L).submit((Object)GTS.getInstance());
                        }
                    }
                });
                this.addIcon(item);
            }
        }
    }

    private void updatePage(boolean upOrDown) {
        this.page = upOrDown ? (this.page < this.maxPage ? ++this.page : 1) : (this.page > 1 ? --this.page : this.maxPage);
        Icon previousPage = SharedIcons.pageIcon(48, false);
        previousPage.addListener(clickable -> this.updatePage(false));
        this.addIcon(previousPage);
        Icon nextPage = SharedIcons.pageIcon(50, true);
        nextPage.addListener(clickable -> this.updatePage(true));
        this.addIcon(nextPage);
        this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
        this.setupListings(this.player);
        this.updateContents(0, 35);
    }

    private void returnPokemon(Listing item) {
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty(this.player.getUniqueId());
        if (storage != null) {
            storage.add(item.getPokemon());
        }
    }
}


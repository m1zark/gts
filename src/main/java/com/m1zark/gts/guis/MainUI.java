/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  org.apache.commons.lang3.StringUtils
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.DataQuery
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Primary
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Secondary
 *  org.spongepowered.api.event.item.inventory.InteractInventoryEvent$Close
 *  org.spongepowered.api.item.inventory.ItemStackSnapshot
 *  org.spongepowered.api.scheduler.Task
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.guis;

import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.auctions.Auctions;
import com.m1zark.gts.guis.ListingUI;
import com.m1zark.gts.guis.PlayerUI;
import com.m1zark.gts.guis.SharedIcons;
import com.m1zark.gts.utils.Listing;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

public class MainUI
extends InventoryManager {
    private Player player;
    private int page = 1;
    private int maxPage;
    private boolean searching;
    private String searchType;
    private String parameters;

    public MainUI(Player player, int page) {
        super(player, 6, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lGTS &l&0\u27a5&r &8Current Listings")}));
        this.player = player;
        this.page = page;
        this.searching = false;
        this.searchType = null;
        this.parameters = null;
        int size = (int)GTS.getInstance().getSql().getAllListings().stream().filter(listing -> !listing.checkHasExpired()).count();
        this.maxPage = size % 28 == 0 && size / 28 != 0 ? size / 28 : size / 28 + 1;
        this.setupInventory();
        this.setupListings();
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            this.clearIcons(new int[]{8, 17, 26, 35, 44, 53});
            this.setupAuctions();
            this.updateContents(new int[]{8, 17, 26, 35, 44, 53});
        }).interval(1L, TimeUnit.SECONDS).name("MainAuctions-" + player.getName()).submit((Object)GTS.getInstance());
    }

    public MainUI(Player player, int page, boolean searching, String searchType, String parameters) {
        super(player, 6, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lGTS &l&0\u27a5&r &8Current Listings")}));
        int size;
        this.player = player;
        this.page = page;
        this.searching = searching;
        this.searchType = searchType;
        this.parameters = parameters;
        this.maxPage = !this.searching ? ((size = (int)GTS.getInstance().getSql().getAllListings().stream().filter(listing -> !listing.checkHasExpired()).count()) % 28 == 0 && size / 28 != 0 ? size / 28 : size / 28 + 1) : ((size = this.searchResults().size()) % 28 == 0 ? size / 28 : size / 28 + 1);
        this.setupInventory();
        this.setupListings();
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            this.clearIcons(new int[]{8, 17, 26, 35, 44, 53});
            this.setupAuctions();
            this.updateContents(new int[]{8, 17, 26, 35, 44, 53});
        }).interval(1L, TimeUnit.SECONDS).name("MainAuctions-" + player.getName()).submit((Object)GTS.getInstance());
    }

    protected void processClose(InteractInventoryEvent.Close event) {
        Sponge.getScheduler().getTasksByName("MainAuctions-" + this.player.getName()).forEach(Task::cancel);
    }

    private void setupInventory() {
        int x;
        int y = 4;
        for (x = 0; x < 8; ++x) {
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.RED, true));
        }
        x = 7;
        for (y = 0; y < 6; ++y) {
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.RED, true));
        }
        Icon plListings = SharedIcons.playerListingsIcon(45, this.player);
        plListings.addListener(clickable -> {
            Player p = clickable.getPlayer();
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                if (this.searching) {
                    p.openInventory(new PlayerUI(p, 1).getInventory());
                } else {
                    p.openInventory(new PlayerUI(p, 1, this.searching, this.searchType, this.parameters).getInventory());
                }
            }).delayTicks(1L).submit((Object)GTS.getInstance());
        });
        this.addIcon(plListings);
        Icon previousPage = SharedIcons.pageIcon(47, false);
        previousPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(false)).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(previousPage);
        Icon refresh = SharedIcons.refreshIcon(48);
        refresh.addListener(clickable -> {
            this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33});
            this.setupListings();
            this.updateContents(new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33});
        });
        this.addIcon(refresh);
        Icon nextPage = SharedIcons.pageIcon(49, true);
        nextPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(true)).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(nextPage);
        Icon market = SharedIcons.marketInfo(51);
        market.addListener(clickable -> {
            Player p = clickable.getPlayer();
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                p.closeInventory();
                Sponge.getCommandManager().process((CommandSource)p, "market");
            }).delayTicks(1L).submit((Object)GTS.getInstance());
        });
        this.addIcon(market);
    }

    private void setupListings() {
        int index = (this.page - 1) * 28;
        List<Listing> listings = this.searching ? this.searchResults() : GTS.getInstance().getSql().getAllListings();
        listings = listings.stream().filter(listing -> !listing.checkHasExpired()).collect(Collectors.toList());
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 7 && index < listings.size(); ++x, ++index) {
                Icon item = new Icon(x + 9 * y, listings.get(index).getPkmItem().getSprite(0, listings.get(index)));
                item.addListener(clickable -> {
                    int id = (Integer)((ItemStackSnapshot)clickable.getEvent().getCursorTransaction().getFinal()).toContainer().get(DataQuery.of((String[])new String[]{"UnsafeData", "ListingID"})).get();
                    Optional<Listing> l = GTS.getInstance().getSql().getAllListings().stream().filter(list -> list.getListingID() == id).findFirst();
                    if (!l.isPresent()) {
                        Chat.sendMessage((CommandSource)this.player, (String)MessageConfig.getMessages("Messages.General.Sold"));
                    } else if (l.get().checkHasExpired()) {
                        Chat.sendMessage((CommandSource)this.player, (String)MessageConfig.getMessages("Messages.General.Expired"));
                    } else {
                        Sponge.getScheduler().createTaskBuilder().execute(() -> this.player.openInventory(new ListingUI(this.player, (Listing)l.get(), this.page, this.searching, this.searchType, this.parameters).getInventory())).delayTicks(1L).submit((Object)GTS.getInstance());
                    }
                });
                this.addIcon(item);
            }
        }
    }

    private void setupAuctions() {
        List<Auctions> auctions = GTS.getInstance().getCurrentAuctions();
        int[] slots = new int[]{8, 17, 26, 35, 44, 53};
        if (!auctions.isEmpty()) {
            Icon itemAuc = new Icon(slots[0], auctions.get(0).getAuctionData(auctions.get(0)).getSprite(this.player));
            itemAuc.addListener(clickable -> {
                if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                    Sponge.getScheduler().createTaskBuilder().execute(() -> ((Auctions)auctions.get(0)).bid(clickable.getPlayer())).delayTicks(1L).submit((Object)GTS.getInstance());
                }
            });
            this.addIcon(itemAuc);
            for (int i = 1; i <= 5; ++i) {
                try {
                    Auctions auction2 = auctions.get(i);
                    Icon queAuc = new Icon(slots[i], auction2.getAuctionData(auction2).getSprite(this.player));
                    queAuc.addListener(clickable -> {
                        if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary && auction2.getExpires() == null && auction2.getSeller().equals(clickable.getPlayer().getUniqueId())) {
                            auction2.givePokemon(auction2.getSeller(), auction2.getPokemon());
                            GTS.getInstance().getCurrentAuctions().remove(auction2);
                            Chat.sendMessage((CommandSource)clickable.getPlayer(), (String)"&7Your auction has been successfully removed.");
                        }
                    });
                    this.addIcon(queAuc);
                    continue;
                }
                catch (IndexOutOfBoundsException e) {
                    this.addIcon(SharedIcons.BorderIcon(slots[i], DyeColors.BLACK, false));
                }
            }
        } else {
            for (int i = 0; i <= 5; ++i) {
                this.addIcon(SharedIcons.BorderIcon(slots[i], DyeColors.BLACK, false));
            }
        }
    }

    private List<Listing> searchResults() {
        List<Listing> valid = Lists.newArrayList();
        List<Listing> listings =  GTS.getInstance().getSql().getAllListings();
        if (this.searchType.equalsIgnoreCase("pokemon")) {
            List<String> search = Lists.newArrayList(this.parameters.split(" "));
            for (Listing item : listings) {
                for (String s : search) {
                    if (!StringUtils.containsIgnoreCase((CharSequence)item.getPokemon().getDisplayName(), (CharSequence)s)) continue;
                    valid.add(item);
                }
            }
        } else if (this.searchType.equalsIgnoreCase("player")) {
            valid = listings.stream().filter(name -> name.getOwner().toString().equals(this.parameters)).collect(Collectors.toList());
        }
        return valid;
    }

    private void updatePage(boolean upOrDown) {
        this.page = upOrDown ? (this.page < this.maxPage ? ++this.page : 1) : (this.page > 1 ? --this.page : this.maxPage);
        Icon previousPage = SharedIcons.pageIcon(48, false);
        previousPage.addListener(clickable -> this.updatePage(false));
        this.addIcon(previousPage);
        Icon nextPage = SharedIcons.pageIcon(50, true);
        nextPage.addListener(clickable -> this.updatePage(true));
        this.addIcon(nextPage);
        this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33});
        this.setupListings();
        this.updateContents(new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33});
    }
}


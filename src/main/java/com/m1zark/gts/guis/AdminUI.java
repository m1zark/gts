/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.guis;

import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.guis.MainUI;
import com.m1zark.gts.guis.SharedIcons;
import com.m1zark.gts.utils.Listing;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class AdminUI
extends InventoryManager {
    private Player player;
    private int page = 1;
    private int maxPage;
    private User searching;

    public AdminUI(Player player, int page, User searching) {
        super(player, 6, Text.of((Object[])new Object[]{Chat.embedColours((String)("&4&lGTS &l&0\u27a5&r &8Listings for " + searching.getName()))}));
        this.player = player;
        this.page = page;
        this.searching = searching;
        ArrayList valid = Lists.newArrayList();
        GTS.getInstance().getSql().getAllListings().forEach(lot -> {
            if (lot.getOwner().equals(searching.getUniqueId())) {
                valid.add(lot);
            }
        });
        this.maxPage = valid.size() % 36 == 0 && valid.size() / 36 != 0 ? valid.size() / 36 : valid.size() / 36 + 1;
        this.setupInventory();
        this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 45});
        this.setupListings();
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
            this.clearIcons(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
            this.setupListings();
            this.updateContents();
        });
        this.addIcon(refresh);
        Icon nextPage = SharedIcons.pageIcon(50, true);
        nextPage.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.updatePage(true)).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(nextPage);
        Icon lastMenuIcon = SharedIcons.lastMenu(53);
        lastMenuIcon.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> this.player.openInventory(new MainUI(this.player, 1).getInventory())).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(lastMenuIcon);
    }

    private void setupListings() {
        int index = (this.page - 1) * 36;
        List listings = GTS.getInstance().getSql().getAllListings().stream().filter(listing -> listing.getOwner().equals(this.searching.getUniqueId())).collect(Collectors.toList());
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 9 && index < listings.size(); ++x, ++index) {
                Listing listing2 = (Listing)listings.get(index);
                Icon item = new Icon(x + 9 * y, listing2.getPkmItem().getSprite(0, (Listing)listings.get(index)));
                item.addListener(clickable -> {});
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
        this.setupListings();
        this.updateContents(0, 35);
    }
}


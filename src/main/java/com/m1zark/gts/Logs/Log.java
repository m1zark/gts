/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.m1zark.gts.Logs;

import com.m1zark.gts.GTS;
import com.m1zark.gts.Logs.LogAction;
import com.m1zark.gts.auctions.Auctions;
import com.m1zark.gts.utils.Listing;
import java.time.Instant;
import java.util.Date;
import javax.annotation.Nullable;

public class Log {
    private int id;
    private final Date date = Date.from(Instant.now());
    private final String seller;
    private final String buyer;
    @Nullable
    private final Listing listing;
    @Nullable
    private final Auctions auction;
    private final LogAction action;

    public void setId(int id) {
        this.id = id;
    }

    public void addNewLog() {
        GTS.getInstance().getSql().addNewLog(this);
    }

    public int getId() {
        return this.id;
    }

    public Date getDate() {
        return this.date;
    }

    public String getSeller() {
        return this.seller;
    }

    public String getBuyer() {
        return this.buyer;
    }

    @Nullable
    public Listing getListing() {
        return this.listing;
    }

    @Nullable
    public Auctions getAuction() {
        return this.auction;
    }

    public LogAction getAction() {
        return this.action;
    }

    public Log(String seller, String buyer, @Nullable Listing listing, @Nullable Auctions auction, LogAction action) {
        this.seller = seller;
        this.buyer = buyer;
        this.listing = listing;
        this.auction = auction;
        this.action = action;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.spongepowered.api.event.cause.Cause
 */
package com.m1zark.gts.events;

import com.m1zark.gts.auctions.Auctions;
import com.m1zark.gts.events.BaseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.NonNull;
import org.spongepowered.api.event.cause.Cause;

public class AuctionEvent
extends BaseEvent {
    private final Auctions auction;
    @NonNull
    private final Cause cause;
    private final String time = new SimpleDateFormat("MMMMM d yyyy h:mm a z").format(new Date());

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public Auctions getAuction() {
        return this.auction;
    }

    public String getTime() {
        return this.time;
    }

    public AuctionEvent(Auctions auction, @NonNull Cause cause) {
        if (cause == null) {
            throw new NullPointerException("cause is marked non-null but is null");
        }
        this.auction = auction;
        this.cause = cause;
    }
}


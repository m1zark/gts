/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.cause.Cause
 */
package com.m1zark.gts.events;

import com.m1zark.gts.events.BaseEvent;
import com.m1zark.gts.utils.Pokemon;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.NonNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;

public class PurchaseEvent
extends BaseEvent {
    private final Player seller;
    private final Player buyer;
    private final Pokemon listing;
    private final String time = new SimpleDateFormat("MMMMM d yyyy h:mm a z").format(new Date());
    @NonNull
    private final Cause cause;

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public Player getSeller() {
        return this.seller;
    }

    public Player getBuyer() {
        return this.buyer;
    }

    public Pokemon getListing() {
        return this.listing;
    }

    public String getTime() {
        return this.time;
    }

    public PurchaseEvent(Player seller, Player buyer, Pokemon listing, @NonNull Cause cause) {
        if (cause == null) {
            throw new NullPointerException("cause is marked non-null but is null");
        }
        this.seller = seller;
        this.buyer = buyer;
        this.listing = listing;
        this.cause = cause;
    }
}


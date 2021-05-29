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

public class ListEvent
extends BaseEvent {
    private final Player player;
    private final Pokemon listing;
    @NonNull
    private final Cause cause;
    private final String time = new SimpleDateFormat("MMMMM d yyyy h:mm a z").format(new Date());

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Pokemon getListing() {
        return this.listing;
    }

    public String getTime() {
        return this.time;
    }

    public ListEvent(Player player, Pokemon listing, @NonNull Cause cause) {
        if (cause == null) {
            throw new NullPointerException("cause is marked non-null but is null");
        }
        this.player = player;
        this.listing = listing;
        this.cause = cause;
    }
}


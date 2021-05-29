/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.Money
 *  com.m1zark.m1utilities.api.Time
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.entity.living.player.User
 *  org.spongepowered.api.event.Event
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.action.TextActions
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.auctions;

import com.m1zark.gts.GTS;
import com.m1zark.gts.auctions.AuctionData;
import com.m1zark.gts.events.AuctionEvent;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.Money;
import com.m1zark.m1utilities.api.Time;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class Auctions {
    private UUID seller;
    private int price;
    private Date expires;
    private UUID highestBidder;
    private int bidIncrement;
    private com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon;

    public void bid(Player player) {
        if (!this.checkHasExpired()) {
            if (player.getUniqueId().equals(this.seller)) {
                Chat.sendMessage((CommandSource)player, (String)"&7You cannot bid on your own auction.");
            } else if (Money.canPay((User)player, (int)(this.price + this.bidIncrement))) {
                if (player.getUniqueId() != this.getHighestBidder()) {
                    UUID oldHigh = this.highestBidder;
                    Sponge.getServer().getPlayer(oldHigh).ifPresent(p -> {
                        Text message = Text.of((Object[])new Object[]{TextColors.GRAY, TextActions.executeCallback(src -> this.bid((Player)p)), TextActions.showText((Text)Text.of((Object[])new Object[]{TextColors.GRAY, "Click to bid!"})), "You've been ", TextColors.RED, "outbid", TextColors.GRAY, "... Click here to bid again!"});
                        p.sendMessages(new Text[]{message});
                    });
                    this.setHighestBidder(player.getUniqueId());
                    this.setPrice(this.price + this.bidIncrement);
                    if (this.expires.getTime() / 1000L - Date.from(Instant.now()).getTime() / 1000L < 15L) {
                        this.increaseTimeForBid();
                    }
                } else {
                    Chat.sendMessage((CommandSource)player, (String)"&7You are already the highest bidder on this auction.");
                }
            } else {
                Chat.sendMessage((CommandSource)player, (String)"&7Your balance is too low to bid...");
            }
        }
    }

    public void processAuction() {
        if (this.getHighestBidder() == null) {
            this.givePokemon(this.getSeller(), this.getPokemon());
            if (Sponge.getServer().getPlayer(this.getSeller()).isPresent()) {
                Chat.sendMessage((CommandSource)((CommandSource)Sponge.getServer().getPlayer(this.getSeller()).get()), (String)MessageConfig.getMessages("Messages.auctions.Auction-NoSale").replace("{pokemon}", ListingUtils.updatePokemonName(this.getPokemon().getSpecies().name())));
            }
        } else if (Money.transfer((UUID)this.getHighestBidder(), (UUID)this.getSeller(), (int)this.getPrice())) {
            this.givePokemon(this.getHighestBidder(), this.getPokemon());
            Chat.sendServerWideMessage((String)MessageConfig.getMessages("Messages.auctions.Auction-Won-Broadcast").replace("{pokemon}", ListingUtils.updatePokemonName(this.getPokemon().getSpecies().name())).replace("{player}", ListingUtils.getNameFromUUID(this.getSeller()).get()).replace("{player2}", ListingUtils.getNameFromUUID(this.getHighestBidder()).get()).replace("{price}", String.valueOf(this.getPrice())));
            AuctionEvent auctionEvent = new AuctionEvent(this, GTS.getInstance().getGtsCause());
            Sponge.getEventManager().post((Event)auctionEvent);
        } else {
            this.givePokemon(this.getSeller(), this.getPokemon());
            if (Sponge.getServer().getPlayer(this.getSeller()).isPresent()) {
                Chat.sendMessage((CommandSource)((CommandSource)Sponge.getServer().getPlayer(this.getSeller()).get()), (String)MessageConfig.getMessages("Messages.auctions.Not-Enough-Money-Seller").replace("{pokemon}", ListingUtils.updatePokemonName(this.getPokemon().getDisplayName())));
            }
            if (Sponge.getServer().getPlayer(this.getHighestBidder()).isPresent()) {
                Chat.sendMessage((CommandSource)((CommandSource)Sponge.getServer().getPlayer(this.getHighestBidder()).get()), (String)MessageConfig.getMessages("Messages.auctions.Not-Enough-Money-Bidder").replace("{pokemon}", ListingUtils.updatePokemonName(this.getPokemon().getDisplayName())));
            }
        }
        GTS.getInstance().getCurrentAuctions().remove(this);
    }

    public void givePokemon(UUID uuid, com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
        if (Sponge.getServer().getPlayer(uuid).isPresent()) {
            PlayerPartyStorage storage = Pixelmon.storageManager.getParty(uuid);
            if (storage != null) {
                storage.add(pokemon);
            }
        } else {
            GTS.getInstance().getSql().addCacheListing(uuid, new Pokemon(ListingUtils.getNbt(pokemon).toString()));
        }
    }

    private void increaseTimeForBid() {
        this.expires = Date.from(this.expires.toInstant().plusSeconds(15L));
    }

    public String getAuctionTime() {
        if (this.expires == null) {
            this.setExpires(Date.from(Instant.now().plusSeconds(ConfigManager.auctionLength)));
            return "";
        }
        return new Time(this.expires.toInstant().toEpochMilli()).toString("%3$dm %4$ds");
    }

    private boolean checkHasExpired() {
        return this.getExpires().before(Date.from(Instant.now()));
    }

    public AuctionData getAuctionData(Auctions auction) {
        return new AuctionData(auction);
    }

    Auctions(UUID seller, int price, Date expires, UUID highestBidder, int bidIncrement, com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
        this.seller = seller;
        this.price = price;
        this.expires = expires;
        this.highestBidder = highestBidder;
        this.bidIncrement = bidIncrement;
        this.pokemon = pokemon;
    }

    public static AuctionsBuilder builder() {
        return new AuctionsBuilder();
    }

    public UUID getSeller() {
        return this.seller;
    }

    public int getPrice() {
        return this.price;
    }

    public Date getExpires() {
        return this.expires;
    }

    public UUID getHighestBidder() {
        return this.highestBidder;
    }

    public int getBidIncrement() {
        return this.bidIncrement;
    }

    public com.pixelmonmod.pixelmon.api.pokemon.Pokemon getPokemon() {
        return this.pokemon;
    }

    public void setSeller(UUID seller) {
        this.seller = seller;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public void setHighestBidder(UUID highestBidder) {
        this.highestBidder = highestBidder;
    }

    public void setBidIncrement(int bidIncrement) {
        this.bidIncrement = bidIncrement;
    }

    public void setPokemon(com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public static class AuctionsBuilder {
        private UUID seller;
        private int price;
        private Date expires;
        private UUID highestBidder;
        private int bidIncrement;
        private com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon;

        AuctionsBuilder() {
        }

        public AuctionsBuilder seller(UUID seller) {
            this.seller = seller;
            return this;
        }

        public AuctionsBuilder price(int price) {
            this.price = price;
            return this;
        }

        public AuctionsBuilder expires(Date expires) {
            this.expires = expires;
            return this;
        }

        public AuctionsBuilder highestBidder(UUID highestBidder) {
            this.highestBidder = highestBidder;
            return this;
        }

        public AuctionsBuilder bidIncrement(int bidIncrement) {
            this.bidIncrement = bidIncrement;
            return this;
        }

        public AuctionsBuilder pokemon(com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
            this.pokemon = pokemon;
            return this;
        }

        public Auctions build() {
            return new Auctions(this.seller, this.price, this.expires, this.highestBidder, this.bidIncrement, this.pokemon);
        }

        public String toString() {
            return "Auctions.AuctionsBuilder(seller=" + this.seller + ", price=" + this.price + ", expires=" + this.expires + ", highestBidder=" + this.highestBidder + ", bidIncrement=" + this.bidIncrement + ", pokemon=" + this.pokemon + ")";
        }
    }
}


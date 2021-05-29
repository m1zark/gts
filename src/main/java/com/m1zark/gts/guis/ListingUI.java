/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  com.m1zark.m1utilities.api.Money
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  net.minecraft.entity.player.EntityPlayerMP
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Primary
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Secondary
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.guis;

import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.GTSInfo;
import com.m1zark.gts.guis.MainUI;
import com.m1zark.gts.guis.SharedIcons;
import com.m1zark.gts.utils.Listing;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.PokeRequest;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.m1zark.m1utilities.api.Money;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ListingUI
extends InventoryManager {
    private Player player;
    private Listing listing;
    private int page;
    private boolean searching;
    private String searchType;
    private String parameters;
    private boolean admin;

    public ListingUI(Player player, Listing listing, int page, boolean searching, String searchType, String parameters) {
        super(player, 3, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lGTS &l&0\u27a5&r &8Confirm Purchase")}));
        this.player = player;
        this.listing = listing;
        this.page = page;
        this.searching = searching;
        this.parameters = parameters;
        this.searchType = searchType;
        this.admin = player.hasPermission("gts.admin.remove");
        this.setupInventory();
    }

    private void setupInventory() {
        int x = 0;
        int y = 0;
        for (int index = 0; y < 6 && index <= 26; ++index) {
            if (x == 9) {
                x = 0;
                ++y;
            }
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.RED, false));
            ++x;
        }
        Icon confirm = SharedIcons.confirmIcon(10, this.listing);
        confirm.addListener(clickable -> {
            Player p = clickable.getPlayer();
            Optional<Listing> itemCache = GTS.getInstance().getSql().getAllListings().stream().filter(listing1 -> listing1.getListingID() == this.listing.getListingID()).findFirst();
            if (!itemCache.isPresent()) {
                Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.General.Sold"));
            } else if (p.getUniqueId().equals(this.listing.getOwner())) {
                Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Purchase-Items.Own-Item"));
            } else if (itemCache.get().checkHasExpired()) {
                Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.General.Expired"));
            } else {
                if (itemCache.get().getisTrade()) {
                    ListingUI.completeTrade(itemCache.get(), p);
                } else {
                    ListingUI.completePurchase(itemCache.get(), p);
                }
                Sponge.getScheduler().createTaskBuilder().execute(() -> p.openInventory(new MainUI(p, this.page, this.searching, this.searchType, this.parameters).getInventory())).delayTicks(1L).submit((Object)GTS.getInstance());
            }
        });
        this.addIcon(confirm);
        this.addIcon(new Icon(12, this.listing.getPkmItem().getSprite(2, this.listing)));
        this.addIcon(SharedIcons.listingInformation(14, this.listing));
        Icon cancel = SharedIcons.cancelIcon(16);
        cancel.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            Player p = clickable.getPlayer();
            p.openInventory(new MainUI(p, this.page, this.searching, this.searchType, this.parameters).getInventory());
        }).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(cancel);
        if (this.admin) {
            ArrayList lore = Lists.newArrayList((Object[])new Text[]{Text.of((Object[])new Object[]{TextColors.GREEN, "* Left click to mark item as expired."}), Text.of((Object[])new Object[]{TextColors.RED, "* Right click to remove listing and return Pokemon. (Not Added)"})});
            Icon admin = new Icon(22, ItemStack.builder().itemType(ItemTypes.BARRIER).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.RED, "Admin Actions"})).add(Keys.ITEM_LORE, lore).build());
            admin.addListener(clickable -> {
                Player p = clickable.getPlayer();
                Optional<Listing> itemCache = GTS.getInstance().getSql().getAllListings().stream().filter(listing1 -> listing1.getListingID() == this.listing.getListingID()).findFirst();
                if (itemCache.isPresent()) {
                    if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary) {
                        if (Sponge.getServer().getPlayer(itemCache.get().getOwner()).isPresent()) {
                            PlayerPartyStorage storage = Pixelmon.storageManager.getParty(itemCache.get().getOwner());
                            if (storage != null) {
                                storage.add(itemCache.get().getPokemon());
                            }
                            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Admin.Remove-Listing-Now"));
                            Chat.sendMessage((CommandSource)((CommandSource)Sponge.getServer().getPlayer(itemCache.get().getOwner()).get()), (String)MessageConfig.getMessages("Messages.Admin.Remove-Listing-PlayerMessage"));
                        } else {
                            GTS.getInstance().getSql().addCacheListing(itemCache.get().getOwner(), itemCache.get().getPkm());
                            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Admin.Remove-Listing"));
                        }
                        GTS.getInstance().getSql().removeListing(itemCache.get().getListingID());
                    } else if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                        GTS.getInstance().getSql().updateListing(itemCache.get().getListingID(), true);
                        Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Admin.Mark-Expired"));
                    }
                    Sponge.getScheduler().createTaskBuilder().execute(() -> p.openInventory(new MainUI(p, this.page).getInventory())).delayTicks(1L).submit((Object)GTS.getInstance());
                } else {
                    Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.General.Sold"));
                }
            });
            this.addIcon(admin);
        }
    }

    private static void completePurchase(Listing listing, Player p) {
        com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon = ListingUI.getPokemon(p, listing);
        if (pokemon == null) {
            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Purchase-Items.Not-Enough-Money"));
        } else {
            GTS.getInstance().getSql().removeListing(listing.getListingID());
            PlayerPartyStorage storage = Pixelmon.storageManager.getParty(p.getUniqueId());
            if (storage != null) {
                storage.add(pokemon);
            }
            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Purchase-Items.Success").replace("{pokemon}", listing.getPokemon().isEgg() ? "Pok\u00e9mon Egg" : listing.getPokemon().getSpecies().name()).replace("{price}", listing.getPrice()));
            if (Sponge.getServer().getPlayer(listing.getOwner()).isPresent()) {
                Chat.sendMessage((CommandSource)((CommandSource)Sponge.getServer().getPlayer(listing.getOwner()).get()), (String)MessageConfig.getMessages("Messages.Purchase-Items.Seller-Message").replace("{pokemon}", listing.getPokemon().isEgg() ? "Pok\u00e9mon Egg" : listing.getPokemon().getSpecies().name()).replace("{ability}", listing.getPokemon().getAbility().getLocalizedName()).replace("{IV%}", ListingUI.IVPercent(listing.getPokemon())).replace("{shiny}", listing.getPokemon().isShiny() ? "Shiny " : "").replace("{price}", listing.getPrice()));
            }
        }
    }

    private static void completeTrade(Listing listing, Player p) {
        com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon = ListingUI.getPokemonTrade(listing, p);
        if (pokemon == null) {
            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Trade-Item.Invalid-Pokemon"));
        } else {
            GTS.getInstance().getSql().removeListing(listing.getListingID());
            PlayerPartyStorage player = Pixelmon.storageManager.getParty(p.getUniqueId());
            if (player != null) {
                player.retrieveAll();
                player.set(pokemon.getPosition(), null);
                player.add(listing.getPokemon());
            }
            Chat.sendMessage((CommandSource)p, (String)MessageConfig.getMessages("Messages.Trade-Item.Success-Player").replace("{pokemon1}", pokemon.getSpecies().name()).replace("{pokemon2}", listing.getPokemon().getSpecies().name()));
            if (Sponge.getServer().getPlayer(listing.getOwner()).isPresent()) {
                PlayerPartyStorage owner = Pixelmon.storageManager.getParty(listing.getOwner());
                if (owner != null) {
                    owner.add(pokemon);
                }
                Chat.sendMessage((CommandSource)((CommandSource)Sponge.getServer().getPlayer(listing.getOwner()).get()), (String)MessageConfig.getMessages("Messages.Trade-Item.Success-Owner").replace("{pokemon}", listing.getPokemon().isEgg() ? "Pok\u00e9mon Egg" : listing.getPokemon().getSpecies().name()).replace("{ability}", listing.getPokemon().getAbility().getLocalizedName()).replace("{IV%}", ListingUI.IVPercent(listing.getPokemon())).replace("{shiny}", listing.getPokemon().isShiny() ? "Shiny " : ""));
            } else {
                GTS.getInstance().getSql().addCacheListing(listing.getOwner(), new Pokemon(ListingUtils.getNbt(pokemon).toString()));
            }
        }
    }

    private static com.pixelmonmod.pixelmon.api.pokemon.Pokemon getPokemonTrade(Listing listing, Player p) {
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty((EntityPlayerMP)p);
        GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.DEBUG_PREFIX, "Checking Trade..."})}));
        for (com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon : storage.getTeam()) {
            PokeRequest request = listing.getRequest();
            GTS.getInstance().getConsole().ifPresent(console -> {
                console.sendMessages(new Text[]{Text.of((Object[])new Object[]{GTSInfo.DEBUG_PREFIX, "Checking: Level " + pokemon.getLevel() + " " + pokemon.getSpecies().name()})});
                console.sendMessage(Text.of((Object[])new Object[]{GTSInfo.DEBUG_PREFIX, "Level " + request.getLevel() + " " + request.getName()}));
            });
            if (pokemon.isEgg() || !pokemon.getSpecies().name().equalsIgnoreCase(request.getName()) || pokemon.getLevel() < request.getLevel()) continue;
            if (!request.getGender().equals("") && !pokemon.getGender().name().equalsIgnoreCase(request.getGender())) {
                return null;
            }
            if (!request.getAbility().equals("") && !pokemon.getAbility().getName().equalsIgnoreCase(request.getAbility())) {
                return null;
            }
            if (!request.getNature().equals("") && !pokemon.getNature().name().equalsIgnoreCase(request.getNature())) {
                return null;
            }
            if (request.getShiny() && !pokemon.isShiny()) {
                return null;
            }
            return pokemon;
        }
        return null;
    }

    private static com.pixelmonmod.pixelmon.api.pokemon.Pokemon getPokemon(Player player, Listing listing) {
        if (Money.transfer((UUID)player.getUniqueId(), (UUID)listing.getOwner(), (int)Integer.valueOf(listing.getPrice()))) {
            return listing.getPokemon();
        }
        return null;
    }

    private static String IVPercent(com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#0.##");
        int totalIVs = pokemon.getIVs().hp + pokemon.getIVs().attack + pokemon.getIVs().defence + pokemon.getIVs().specialAttack + pokemon.getIVs().specialDefence + pokemon.getIVs().speed;
        return df.format((double)totalIVs / 186.0 * 100.0) + "%";
    }
}


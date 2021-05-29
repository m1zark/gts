/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.GUI.Icon
 *  com.m1zark.m1utilities.api.GUI.InventoryManager
 *  com.pixelmonmod.pixelmon.Pixelmon
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.client.gui.GuiResources
 *  com.pixelmonmod.pixelmon.config.PixelmonConfig
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender
 *  com.pixelmonmod.pixelmon.enums.EnumNature
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  com.pixelmonmod.pixelmon.storage.PlayerPartyStorage
 *  net.minecraft.entity.player.EntityPlayerMP
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.data.DataQuery
 *  org.spongepowered.api.data.DataView
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.data.type.DyeColors
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Event
 *  org.spongepowered.api.event.cause.Cause
 *  org.spongepowered.api.event.cause.EventContext
 *  org.spongepowered.api.event.cause.EventContextKeys
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Primary
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent$Secondary
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.ItemTypes
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 *  org.spongepowered.api.text.format.TextStyles
 */
package com.m1zark.gts.guis;

import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.enums.Abilities;
import com.m1zark.gts.events.TradeEvent;
import com.m1zark.gts.guis.SharedIcons;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.PokeRequest;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.configuration.MessageConfig;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.GUI.Icon;
import com.m1zark.m1utilities.api.GUI.InventoryManager;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class Trade
extends InventoryManager {
    private Player player;
    private com.pixelmonmod.pixelmon.api.pokemon.Pokemon playerPokemon;
    public String requestedPokemon;
    public int level = 10;
    public String nature = "";
    private int natureIndex = 4;
    public String ability = "";
    private int abilityIndex = -1;
    public String gender = "";
    public boolean shiny = false;

    public Trade(Player player, com.pixelmonmod.pixelmon.api.pokemon.Pokemon playerPokemon, String pokemon) {
        super(player, 3, Text.of((Object[])new Object[]{Chat.embedColours((String)"&4&lGTS &l&0\u27a5&r &8Requested Pok\u00e9mon")}));
        this.player = player;
        this.playerPokemon = playerPokemon;
        this.requestedPokemon = pokemon;
        this.setupInventory();
    }

    private void setupInventory() {
        int index = 0;
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9 && index < 27; ++x, ++index) {
                this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.PURPLE, false));
            }
        }
        this.addIcon(this.pokemonIcon(9));
        Icon confirm = this.confirmIcon(17);
        confirm.addListener(clickable -> Sponge.getScheduler().createTaskBuilder().execute(() -> {
            PokeRequest request = new PokeRequest(this);
            Pokemon pokeTrade = new Pokemon(ListingUtils.getNbt(this.playerPokemon).toString(), true, request, Instant.now().plusSeconds(ConfigManager.listingsTime).toString());
            TradeEvent tradeEvent = new TradeEvent(this.player, pokeTrade, Cause.of((EventContext)EventContext.builder().add(EventContextKeys.PLUGIN, GTS.getInstance().getPluginContainer()).add(EventContextKeys.PLAYER_SIMULATED, this.player.getProfile()).build(), (Object)GTS.getInstance()));
            Sponge.getEventManager().post((Event)tradeEvent);
            if (!tradeEvent.isCancelled()) {
                GTS.getInstance().getSql().addListing(this.player.getUniqueId(), pokeTrade, null, ConfigManager.listingsTime);
                PlayerPartyStorage storage = ListingUtils.getPlayerStorage((EntityPlayerMP)this.player);
                if (storage != null) {
                    storage.retrieveAll();
                    storage.set(this.playerPokemon.getPosition(), null);
                    if (MessageConfig.getServerAnnounce()) {
                        Chat.sendBroadcastMessage((CommandSource)this.player, (String)MessageConfig.getMessages("Messages.Trade-Item.Announce-Added").replace("{player}", this.player.getName()).replace("{ability}", this.playerPokemon.getAbility().getLocalizedName()).replace("{IV%}", this.IVPercent(this.playerPokemon)).replace("{shiny}", this.playerPokemon.isShiny() ? "Shiny " : "").replace("{pokemon}", this.playerPokemon.getSpecies().name()));
                    }
                    Chat.sendMessage((CommandSource)this.player, (String)MessageConfig.getMessages("Messages.Trade-Item.Success-Add").replace("{pokemon}", this.playerPokemon.getSpecies().name()));
                    this.player.closeInventory();
                }
            }
        }).delayTicks(1L).submit((Object)GTS.getInstance()));
        this.addIcon(confirm);
        this.addIcon(this.levelIcon(11));
        this.addIcon(this.shinyIcon(12));
        this.addIcon(this.natureIcon(13, this.natureIndex));
        this.addIcon(this.abilityIcon(14));
        this.addIcon(this.genderIcon(15));
    }

    private Icon pokemonIcon(int slot) {
        int dexNumber = Pixelmon.pokemonFactory.create((EnumSpecies)EnumSpecies.getFromNameAnyCase((String)this.requestedPokemon)).getBaseStats().nationalPokedexNumber;
        String spriteData = "pixelmon:" + GuiResources.getSpritePath((EnumSpecies)EnumSpecies.getFromNameAnyCase((String)this.requestedPokemon), (int)0, (Gender)(this.gender.equals("Male") ? Gender.Male : (this.gender.equals("Female") ? Gender.Female : Gender.None)), (boolean)false, (boolean)false);
        String pokeGender = this.gender.equals("Male") ? "&b" + this.gender + " \u2642" : (this.gender.equals("Female") ? "&d" + this.gender + " \u2640" : (this.gender.equals("Genderless") ? "&8Genderless \u26a5" : ""));
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&b" + ListingUtils.updatePokemonName(this.requestedPokemon) + " &7| &eLvl " + this.level + (this.level < 100 ? "+" : "") + (this.shiny ? " &7(&6Shiny&7)&r" : "")))}));
        if (!this.ability.equals("")) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Ability: &e" + this.ability + (this.abilityIndex == 2 ? " &7(&6HA&7)&r" : "")))}));
        }
        if (!this.nature.equals("")) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Nature: &e" + this.nature))}));
        }
        if (!this.gender.equals("")) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Gender: " + pokeGender))}));
        }
        Optional sprite = Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite");
        ItemStack Item = ItemStack.builder().itemType((ItemType)sprite.get()).build();
        Item.offer(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.DARK_AQUA, TextStyles.BOLD, "Requested Pok\u00e9mon"}));
        Item.offer(Keys.ITEM_LORE, itemLore);
        return new Icon(slot, ItemStack.builder().fromContainer((DataView)Item.toContainer().set(DataQuery.of((String[])new String[]{"UnsafeData", "SpriteName"}), (Object)spriteData)).build());
    }

    private Icon levelIcon(int slot) {
        Icon icon = new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:rare_candy").get()).add(Keys.DYE_COLOR, DyeColors.LIME).add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "Required Level")).add(Keys.ITEM_LORE, Lists.newArrayList(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7Click here to modify your requested Pok\u00e9mons &blevel.")}), Text.of((Object[])new Object[]{Chat.embedColours((String)"&cThis is the minimum level required.")}), Text.EMPTY, Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Current Selection: &e" + this.level))}), Text.EMPTY, Text.of((Object[])new Object[]{Chat.embedColours((String)"&bNote:")}), Text.of((Object[])new Object[]{TextColors.GRAY, this.level + 10 <= PixelmonConfig.maxLevel ? TextStyles.NONE : TextStyles.STRIKETHROUGH, " * Left Click: ", TextColors.GREEN, "+10 Levels"}), Text.of((Object[])new Object[]{TextColors.GRAY, this.level - 10 >= 1 ? TextStyles.NONE : TextStyles.STRIKETHROUGH, " * Right Click: ", TextColors.GREEN, "-10 Levels"}))).build());
        icon.addListener(clickable -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                if (this.level + 10 <= PixelmonConfig.maxLevel) {
                    this.level += 10;
                }
            } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary && this.level - 10 >= 1) {
                this.level -= 10;
            }
            Sponge.getScheduler().createTaskBuilder().execute(() -> this.updateIcons()).delayTicks(1L).submit((Object)GTS.getInstance());
        });
        return icon;
    }

    private Icon shinyIcon(int slot) {
        Icon icon = new Icon(slot, ItemStack.builder().itemType((ItemType)Sponge.getRegistry().getType(ItemType.class, "pixelmon:light_ball").get()).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Required Shininess"})).add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(Chat.embedColours((String)"&7Click here to modify your requested Pok\u00e9mons &bshininess.")), Text.EMPTY, Text.of(Chat.embedColours((String)("&7Current Selection: &e" + String.valueOf(this.shiny).replace('t', 'T').replace('f', 'F')))))).build());
        icon.addListener(clickable -> {
            this.shiny = !this.shiny;
            Sponge.getScheduler().createTaskBuilder().execute(() -> this.updateIcons()).delayTicks(1L).submit((Object)GTS.getInstance());
        });
        return icon;
    }

    private Icon natureIcon(int slot, int index) {
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7Click here to modify your requested Pok\u00e9mons &bnature.")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Current Selection: &e" + (this.nature.equals("") ? "Any" : this.nature)))}));
        if (index != 4) {
            EnumNature nature = EnumNature.natureFromString((String)this.nature);
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Increased: &a" + nature.increasedStat.name()))}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Lowered: &c" + nature.decreasedStat.name()))}));
        }
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bNote:")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aUse Left and Right click to cycle through options.")}));
        Icon icon = new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:ever_stone").orElse(ItemTypes.BARRIER)).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Required Nature"})).add(Keys.ITEM_LORE, itemLore).build());
        icon.addListener(clickable -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                this.natureIndex = this.natureIndex == 24 ? 4 : this.natureIndex + 1;
                this.nature = this.natureIndex == 4 ? "" : EnumNature.getNatureFromIndex((int)this.natureIndex).toString();
            } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary) {
                this.natureIndex = this.natureIndex == 4 ? 24 : this.natureIndex - 1;
                this.nature = this.natureIndex == 4 ? "" : EnumNature.getNatureFromIndex((int)this.natureIndex).toString();
            }
            Sponge.getScheduler().createTaskBuilder().execute(() -> this.updateIcons()).delayTicks(1L).submit((Object)GTS.getInstance());
        });
        return icon;
    }

    private Icon abilityIcon(int slot) {
        String[] abilities = Pixelmon.pokemonFactory.create((EnumSpecies)EnumSpecies.getFromNameAnyCase((String)this.requestedPokemon)).getBaseStats().abilities;
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7Click here to modify your requested Pok\u00e9mons &bability.")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Current Selection: &e" + (this.abilityIndex != -1 ? Abilities.valueOf(this.ability).getName() : "Any") + (this.abilityIndex == 2 ? " &7(&6HA&7)&r" : "")))}));
        if (this.abilityIndex != -1) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&b" + Abilities.valueOf(this.ability).getDescription()))}));
        }
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bNote:")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aLeft click to cycle through options.")}));
        Icon icon = new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:ability_capsule").orElse(ItemTypes.BARRIER)).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Required Ability"})).add(Keys.ITEM_LORE, itemLore).build());
        icon.addListener(clickable -> {
            if (clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
                if (this.abilityIndex == -1) {
                    this.abilityIndex = 0;
                    this.ability = abilities[this.abilityIndex];
                } else if (this.abilityIndex == 0) {
                    if (abilities[1] != null) {
                        this.abilityIndex = 1;
                        this.ability = abilities[1];
                    } else if (abilities[2] != null) {
                        this.abilityIndex = 2;
                        this.ability = abilities[2];
                    } else {
                        this.abilityIndex = -1;
                        this.ability = "";
                    }
                } else if (this.abilityIndex == 1) {
                    if (abilities[2] != null) {
                        this.abilityIndex = 2;
                        this.ability = abilities[2];
                    } else {
                        this.abilityIndex = -1;
                        this.ability = "";
                    }
                } else if (this.abilityIndex == 2) {
                    this.abilityIndex = -1;
                    this.ability = "";
                }
            } else if (clickable.getEvent() instanceof ClickInventoryEvent.Secondary) {
                // empty if block
            }
            Sponge.getScheduler().createTaskBuilder().execute(() -> this.updateIcons()).delayTicks(1L).submit((Object)GTS.getInstance());
        });
        return icon;
    }

    private Icon genderIcon(int slot) {
        int malePerecent = (int) Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(this.requestedPokemon)).getBaseStats().getMalePercent();
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7Click here to modify your requested Pok\u00e9mons &bgender.")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        if (malePerecent == -1) {
            this.gender = "Genderless";
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cRequested Pok\u00e9mon is genderless.")}));
        } else if (malePerecent == 0) {
            this.gender = "Female";
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cRequested Pok\u00e9mon can only be female.")}));
        } else if (malePerecent == 100) {
            this.gender = "Male";
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&cRequested Pok\u00e9mon can only be male.")}));
        } else {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Current Selection: &e" + (this.gender.equals("") ? "Any" : this.gender)))}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&bNote:")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&aLeft click to cycle through options.")}));
        }
        Icon icon = new Icon(slot, ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:rose_incense").orElse(ItemTypes.BARRIER)).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GOLD, TextStyles.BOLD, "Required Gender"})).add(Keys.ITEM_LORE, itemLore).build());
        if (this.between(malePerecent, 1, 99)) {
            icon.addListener(clickable -> {
                this.gender = this.gender.equals("Female") ? "Male" : (this.gender.equals("Male") ? "" : "Female");
                Sponge.getScheduler().createTaskBuilder().execute(() -> this.updateIcons()).delayTicks(1L).submit((Object)GTS.getInstance());
            });
        }
        return icon;
    }

    private Icon confirmIcon(int slot) {
        return new Icon(slot, ItemStack.builder().itemType(ItemTypes.STAINED_HARDENED_CLAY).quantity(1).add(Keys.DISPLAY_NAME, Text.of((Object[])new Object[]{TextColors.GREEN, "Confirm Trade Request"})).add(Keys.DYE_COLOR, DyeColors.LIME).add(Keys.ITEM_LORE, Lists.newArrayList(Text.of((Object[])new Object[]{TextColors.GRAY, "Click here to confirm trade request."}))).build());
    }

    private void updateIcons() {
        this.addIcon(this.pokemonIcon(9));
        this.addIcon(this.levelIcon(11));
        this.addIcon(this.shinyIcon(12));
        this.addIcon(this.natureIcon(13, this.natureIndex));
        this.addIcon(this.abilityIcon(14));
        this.addIcon(this.genderIcon(15));
        this.updateContents();
    }

    private boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return i >= minValueInclusive && i <= maxValueInclusive;
    }

    private String IVPercent(com.pixelmonmod.pixelmon.api.pokemon.Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#0.##");
        int totalIVs = pokemon.getIVs().hp + pokemon.getIVs().attack + pokemon.getIVs().defence + pokemon.getIVs().specialAttack + pokemon.getIVs().specialDefence + pokemon.getIVs().speed;
        return df.format((double)totalIVs / 186.0 * 100.0) + "%";
    }
}


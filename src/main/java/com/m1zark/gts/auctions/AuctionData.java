/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.m1zark.m1utilities.api.Chat
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec
 *  com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower
 *  com.pixelmonmod.pixelmon.client.gui.GuiResources
 *  com.pixelmonmod.pixelmon.config.PixelmonConfig
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.LakeTrioStats
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  com.pixelmonmod.pixelmon.enums.forms.EnumSpecial
 *  de.waterdu.aquaauras.file.FileManager
 *  de.waterdu.aquaauras.file.structures.AuraDefinition
 *  de.waterdu.aquaauras.file.structures.EffectDefinition
 *  org.apache.commons.lang3.StringUtils
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.data.DataQuery
 *  org.spongepowered.api.data.DataView
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.auctions;

import com.google.common.base.Strings;
import com.m1zark.gts.auctions.Auctions;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.m1utilities.api.Chat;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumSpecial;
import de.waterdu.aquaauras.helper.FileHelper;
import de.waterdu.aquaauras.structures.AuraDefinition;
import de.waterdu.aquaauras.structures.EffectDefinition;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class AuctionData {
    private Auctions auc;
    private int auctionID;
    private UUID owner;
    private Pokemon pokemon;
    private int id;
    private int form;
    private String formName = "";
    private String name;
    private String ability;
    private String m1;
    private String m2;
    private String m3;
    private String m4;
    private int lvl;
    private String growth;
    private String gender;
    private boolean shiny;
    private String nature;
    private String mintNature;
    private int happiness;
    private String caughtBall;
    private boolean isEgg;
    private boolean isHiddenAbility;
    private String heldItem = "Nothing";
    private String hiddenPower;
    private String Aura = "";
    private String specialTexture;
    private String pokerus;
    private boolean isTrio = false;
    private int numEnchanted;
    private String customTexture;
    private int totalIVs;
    private String IVPercent;
    private int[] ivs = new int[]{0, 0, 0, 0, 0, 0};
    private int totalEVs;
    private String EVPercent;
    private int[] evs = new int[]{0, 0, 0, 0, 0, 0};
    private boolean wasHyperTrained = false;
    private String[] ht = new String[]{"", "", "", "", "", ""};

    public AuctionData(Auctions auction) {
        this.auc = auction;
        this.owner = auction.getSeller();
        this.pokemon = auction.getPokemon();
        DecimalFormat df = new DecimalFormat("#0.##");
        if (this.pokemon != null) {
            this.id = this.pokemon.getBaseStats().nationalPokedexNumber;
            this.form = this.pokemon.getForm();
            this.customTexture = this.pokemon.getCustomTexture();
            String string = this.pokemon.getPokerus() != null ? (this.pokemon.getPokerus().canInfect() ? "&d[PKRS] " : "&7&m[PKRS] ") : (this.pokerus = "");
            if (!(this.pokemon.getFormEnum().equals(EnumSpecial.Base) || this.pokemon.getFormEnum().getLocalizedName().equals("None") || this.pokemon.getFormEnum().getLocalizedName().equals("Standard") || this.pokemon.getFormEnum().getLocalizedName().equals("Normal"))) {
                this.formName = this.pokemon.getFormEnum().getLocalizedName();
            }
            switch (this.pokemon.getSpecies()) {
                case Mesprit: 
                case Azelf: 
                case Uxie: {
                    this.isTrio = true;
                    try {
                        if (this.pokemon.getExtraStats() == null || !(this.pokemon.getExtraStats() instanceof LakeTrioStats)) break;
                        LakeTrioStats extra = (LakeTrioStats)this.pokemon.getExtraStats();
                        this.numEnchanted = PixelmonConfig.lakeTrioMaxEnchants - extra.numEnchanted;
                        break;
                    }
                    catch (Exception extra) {
                        // empty catch block
                    }
                }
            }
            this.name = this.pokemon.getSpecies().getPokemonName();
            this.ability = this.pokemon.getAbility().getName();
            this.isHiddenAbility = ListingUtils.isHiddenAbility(this.pokemon, this.ability);
            this.heldItem = !this.pokemon.getHeldItem().getDisplayName().equalsIgnoreCase("Air") ? this.pokemon.getHeldItem().getDisplayName() : "Nothing";
            this.m1 = this.pokemon.getMoveset().get(0) != null ? this.pokemon.getMoveset().attacks[0].getActualMove().getLocalizedName() : "Empty";
            this.m2 = this.pokemon.getMoveset().get(1) != null ? this.pokemon.getMoveset().attacks[1].getActualMove().getLocalizedName() : "Empty";
            this.m3 = this.pokemon.getMoveset().get(2) != null ? this.pokemon.getMoveset().attacks[2].getActualMove().getLocalizedName() : "Empty";
            this.m4 = this.pokemon.getMoveset().get(3) != null ? this.pokemon.getMoveset().attacks[3].getActualMove().getLocalizedName() : "Empty";
            this.isEgg = this.pokemon.isEgg();
            this.lvl = this.pokemon.getLevel();
            this.nature = this.pokemon.getNature().name();
            this.mintNature = this.pokemon.getMintNature() != null ? "&7|&b" + this.pokemon.getMintNature().getName() : "";
            this.gender = this.pokemon.getGender().name();
            this.shiny = this.pokemon.isShiny();
            this.growth = this.pokemon.getGrowth().name();
            this.happiness = this.pokemon.getFriendship();
            this.caughtBall = this.pokemon.getCaughtBall().name();
            this.ivs[0] = this.pokemon.getIVs().hp;
            this.ivs[1] = this.pokemon.getIVs().attack;
            this.ivs[2] = this.pokemon.getIVs().defence;
            this.ivs[3] = this.pokemon.getIVs().specialAttack;
            this.ivs[4] = this.pokemon.getIVs().specialDefence;
            this.ivs[5] = this.pokemon.getIVs().speed;
            this.totalIVs = this.ivs[0] + this.ivs[1] + this.ivs[2] + this.ivs[3] + this.ivs[4] + this.ivs[5];
            this.IVPercent = df.format((double)this.totalIVs / 186.0 * 100.0) + "%";
            StatsType[] stat = new StatsType[]{StatsType.HP, StatsType.Attack, StatsType.Defence, StatsType.SpecialAttack, StatsType.SpecialDefence, StatsType.Speed};
            for (int i = 0; i < stat.length; ++i) {
                if (!this.pokemon.getIVs().isHyperTrained(stat[i])) continue;
                this.ht[i] = "&3";
                this.wasHyperTrained = true;
            }
            this.evs[0] = this.pokemon.getEVs().hp;
            this.evs[1] = this.pokemon.getEVs().attack;
            this.evs[2] = this.pokemon.getEVs().defence;
            this.evs[3] = this.pokemon.getEVs().specialAttack;
            this.evs[4] = this.pokemon.getEVs().specialDefence;
            this.evs[5] = this.pokemon.getEVs().speed;
            this.totalEVs = this.evs[0] + this.evs[1] + this.evs[2] + this.evs[3] + this.evs[4] + this.evs[5];
            this.EVPercent = df.format((double)this.totalEVs / 510.0 * 100.0) + "%";
            this.hiddenPower = HiddenPower.getHiddenPowerType((IVStore)this.pokemon.getIVs()).getLocalizedName();
            if (this.pokemon.getPersistentData().hasKey("HasAura")) {
                AuraDefinition ad = FileHelper.getAuraDefinitionForID(this.pokemon.getPersistentData().getInteger("AuraType"));
                EffectDefinition ed = FileHelper.getEffectDefinitionForID(this.pokemon.getPersistentData().getInteger("AuraEffect"));
                this.Aura = ad.getDisplayName() + " " + ed.getDisplayName();
            }
        }
    }

    private static String updatePokemonName(String name) {
        if (name.equalsIgnoreCase("MrMime")) {
            return "Mr. Mime";
        }
        if (name.equalsIgnoreCase("MimeJr")) {
            return "Mime Jr.";
        }
        if (name.equalsIgnoreCase("Nidoranfemale")) {
            return "Nidoran&d\u2640&r";
        }
        if (name.equalsIgnoreCase("Nidoranmale")) {
            return "Nidoran&b\u2642&r";
        }
        if (name.equalsIgnoreCase("Farfetchd")) {
            return "Farfetch'd";
        }
        if (name.contains("Alolan")) {
            return StringUtils.join((Object[])StringUtils.splitByCharacterTypeCamelCase((String)name.replaceAll("\\d+", "")), (String)" ");
        }
        return name;
    }

    public ItemStack getSprite(Player player) {
        Optional sprite = Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite");
        ItemStack Item = ItemStack.builder().itemType((ItemType)sprite.get()).build();
        ItemStack item = this.setPicture(Item);
        this.setItemData(item, player);
        return ItemStack.builder().fromContainer((DataView)item.toContainer().set(DataQuery.of((String[])new String[]{"UnsafeData", "auctionID"}), (Object)this.owner.toString())).build();
    }

    private ItemStack setPicture(ItemStack item) {
        String spriteData = this.isEgg ? "pixelmon:" + GuiResources.getEggSprite((EnumSpecies)this.pokemon.getSpecies(), (int)0) : "pixelmon:" + GuiResources.getSpritePath((EnumSpecies)this.pokemon.getSpecies(), (int)this.pokemon.getForm(), (Gender)this.pokemon.getGender(), (String)this.pokemon.getCustomTexture(), (boolean)this.pokemon.isShiny());
        return ItemStack.builder().fromContainer((DataView)item.toContainer().set(DataQuery.of((String[])new String[]{"UnsafeData", "SpriteName"}), (Object)spriteData)).build();
    }

    private void setItemData(ItemStack item, Player player) {
        String displayName = this.pokerus + "&b" + AuctionData.updatePokemonName(this.name) + " &7| &eLvl " + this.lvl + " " + (this.shiny ? "&7(&6Shiny&7)&r" : "");
        String pokeGender = this.gender.equals("Male") ? "&b" + this.gender + " \u2642" : (this.gender.equals("Female") ? "&d" + this.gender + " \u2640" : "&8Genderless \u26a5");
        item.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours((String)displayName)));
        ArrayList<Text> itemLore = new ArrayList<Text>();
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Seller: &b" + ListingUtils.getNameFromUUID(this.owner).get()))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Current Price: &b" + this.auc.getPrice()))}));
        if (this.auc.getExpires() != null) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Ends in: &b" + this.auc.getAuctionTime()))}));
            if (this.auc.getHighestBidder() != null) {
                itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Highest Bidder: &b" + ListingUtils.getNameFromUUID(this.auc.getHighestBidder()).get()))}));
            }
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&c* Left click to bid on this auction!")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Increment: &b" + this.auc.getBidIncrement()))}));
        } else if (this.auc.getSeller().equals(player.getUniqueId())) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&c* Right click to remove this auction!")}));
        }
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7--------------------")}));
        int numEnchants = PixelmonConfig.lakeTrioMaxEnchants - this.numEnchanted;
        if (new PokemonSpec("unbreedable").matches(this.pokemon)) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&4Unbreedable")}));
        }
        if (!this.Aura.isEmpty()) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Aura: &e" + ListingUtils.capitalize(this.Aura)))}));
        }
        if (!this.formName.isEmpty()) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Form: &e" + ListingUtils.capitalize(this.formName)))}));
        }
        if (this.isTrio) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Ruby Enchant: &e" + (numEnchants != 0 ? numEnchants + " Available" : "None Available")))}));
        }
        if (!Strings.isNullOrEmpty((String)this.customTexture)) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Custom Texture: &e" + ListingUtils.capitalize(this.customTexture)))}));
        }
        if (!this.pokemon.getHeldItem().getDisplayName().equalsIgnoreCase("Air")) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Held Item: &e" + this.heldItem))}));
        }
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Ability: &e" + this.ability + (this.isHiddenAbility ? " &7(&6HA&7)&r" : "")))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Nature: &e" + this.nature + this.mintNature))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Gender: " + pokeGender))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Size: &e" + this.growth))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Happiness: &e" + this.happiness))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7Hidden Power: &e" + this.hiddenPower))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7CaughtBall: &e" + this.caughtBall))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        if (this.wasHyperTrained) {
            itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7IVs in &3aqua &7have been hyper-trained!")}));
        }
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7IVs: &e" + this.totalIVs + "&7/&e186 &7(&a" + this.IVPercent + "&7)"))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&cHP: " + this.ht[0] + this.ivs[0] + " &7/ &6Atk: " + this.ht[1] + this.ivs[1] + " &7/ &eDef: " + this.ht[2] + this.ivs[2]))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&9SpA: " + this.ht[3] + this.ivs[3] + " &7/ &aSpD: " + this.ht[4] + this.ivs[4] + " &7/ &dSpe: " + this.ht[5] + this.ivs[5]))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&7EVs: &e" + this.totalEVs + "&7/&e510 &7(&a" + this.EVPercent + "&7)"))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&cHP: " + this.evs[0] + " &7/ &6Atk: " + this.evs[1] + " &7/ &eDef: " + this.evs[2]))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&9SpA: " + this.evs[3] + " &7/ &aSpD: " + this.evs[4] + " &7/ &dSpe: " + this.evs[5]))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7Moves:")}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&b" + this.m1 + " &7- &b" + this.m2))}));
        itemLore.add(Text.of((Object[])new Object[]{Chat.embedColours((String)("&b" + this.m3 + " &7- &b" + this.m4))}));
        item.offer(Keys.ITEM_LORE, itemLore);
    }
}


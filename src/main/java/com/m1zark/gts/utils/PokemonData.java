/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.m1zark.m1utilities.api.Chat
 *  com.m1zark.m1utilities.api.Time
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
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.data.DataQuery
 *  org.spongepowered.api.data.DataView
 *  org.spongepowered.api.data.key.Keys
 *  org.spongepowered.api.item.ItemType
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.text.Text
 */
package com.m1zark.gts.utils;

import com.google.common.base.Strings;
import com.m1zark.gts.utils.Listing;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.m1utilities.api.Chat;
import com.m1zark.m1utilities.api.Time;
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
import de.waterdu.aquaauras.auras.AuraStorage;
import de.waterdu.aquaauras.helper.FileHelper;
import de.waterdu.aquaauras.structures.AuraDefinition;
import de.waterdu.aquaauras.structures.EffectDefinition;
import java.text.DecimalFormat;
import java.util.*;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class PokemonData {
    private int listingID;
    private String price;
    private UUID owner;
    private Date expires;
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
    private List<String> PokemonAuras = new ArrayList<>();
    private int AurasEnabled = 0;
    private String pokerus;
    private boolean isTrio = false;
    private int numEnchanted = 0;
    private String customTexture;
    private int totalIVs;
    private String IVPercent;
    private int[] ivs = new int[]{0, 0, 0, 0, 0, 0};
    private int totalEVs;
    private String EVPercent;
    private int[] evs = new int[]{0, 0, 0, 0, 0, 0};
    private boolean wasHyperTrained = false;
    private String[] ht = new String[]{"", "", "", "", "", ""};

    public PokemonData(int listingID, Pokemon pokemon, String price, UUID owner, Date expires, boolean trade) {
        this.listingID = listingID;
        this.price = price;
        this.owner = owner;
        this.expires = expires;
        DecimalFormat df = new DecimalFormat("#0.##");
        if (pokemon != null) {
            this.pokemon = pokemon;
            this.id = pokemon.getSpecies().getNationalPokedexInteger();
            this.form = pokemon.getForm();
            this.customTexture = pokemon.getCustomTexture();
            pokerus = pokemon.getPokerus() != null ? (pokemon.getPokerus().canInfect() ? "&d[PKRS] " : "&7&m[PKRS] ") : "";
            if (!(pokemon.getFormEnum().equals(EnumSpecial.Base) || pokemon.getFormEnum().getLocalizedName().equals("None") || pokemon.getFormEnum().getLocalizedName().equals("Standard") || pokemon.getFormEnum().getLocalizedName().equals("Normal"))) {
                this.formName = pokemon.getFormEnum().getLocalizedName();
            }
            switch (pokemon.getSpecies()) {
                case Mesprit: 
                case Azelf: 
                case Uxie: {
                    this.isTrio = true;
                }
            }
            try {
                if (pokemon.getExtraStats() != null && pokemon.getExtraStats() instanceof LakeTrioStats) {
                    LakeTrioStats extra = (LakeTrioStats)pokemon.getExtraStats();
                    this.numEnchanted = PixelmonConfig.lakeTrioMaxEnchants - extra.numEnchanted;
                }
            }
            catch (Exception extra) {
                // empty catch block
            }
            this.name = pokemon.getSpecies().getPokemonName();
            this.ability = pokemon.getAbility().getName();
            this.isHiddenAbility = ListingUtils.isHiddenAbility(pokemon, this.ability);
            this.heldItem = !pokemon.getHeldItem().getDisplayName().equalsIgnoreCase("Air") ? pokemon.getHeldItem().getDisplayName() : "Nothing";
            this.m1 = pokemon.getMoveset().get(0) != null ? pokemon.getMoveset().attacks[0].getActualMove().getAttackName() : "Empty";
            this.m2 = pokemon.getMoveset().get(1) != null ? pokemon.getMoveset().attacks[1].getActualMove().getAttackName() : "Empty";
            this.m3 = pokemon.getMoveset().get(2) != null ? pokemon.getMoveset().attacks[2].getActualMove().getAttackName() : "Empty";
            this.m4 = pokemon.getMoveset().get(3) != null ? pokemon.getMoveset().attacks[3].getActualMove().getAttackName() : "Empty";
            this.isEgg = pokemon.isEgg();
            this.lvl = pokemon.getLevel();
            this.nature = pokemon.getBaseNature().name();
            this.mintNature = pokemon.getMintNature() != null ? "&7|&b" + pokemon.getMintNature().getName() : "";
            this.gender = pokemon.getGender().name();
            this.shiny = pokemon.isShiny();
            this.growth = pokemon.getGrowth().name();
            this.happiness = pokemon.getFriendship();
            this.caughtBall = pokemon.getCaughtBall().name();
            this.ivs[0] = pokemon.getIVs().getStat(StatsType.HP);
            this.ivs[1] = pokemon.getIVs().getStat(StatsType.Attack);
            this.ivs[2] = pokemon.getIVs().getStat(StatsType.Defence);
            this.ivs[3] = pokemon.getIVs().getStat(StatsType.SpecialAttack);
            this.ivs[4] = pokemon.getIVs().getStat(StatsType.SpecialDefence);
            this.ivs[5] = pokemon.getIVs().getStat(StatsType.Speed);
            this.totalIVs = this.ivs[0] + this.ivs[1] + this.ivs[2] + this.ivs[3] + this.ivs[4] + this.ivs[5];
            this.IVPercent = df.format((double)this.totalIVs / 186.0 * 100.0) + "%";
            StatsType[] stat = new StatsType[]{StatsType.HP, StatsType.Attack, StatsType.Defence, StatsType.SpecialAttack, StatsType.SpecialDefence, StatsType.Speed};
            for (int i = 0; i < stat.length; ++i) {
                if (!pokemon.getIVs().isHyperTrained(stat[i])) continue;
                this.ht[i] = "&3";
                this.wasHyperTrained = true;
            }
            this.evs[0] = pokemon.getEVs().getStat(StatsType.HP);
            this.evs[1] = pokemon.getEVs().getStat(StatsType.Attack);
            this.evs[2] = pokemon.getEVs().getStat(StatsType.Defence);
            this.evs[3] = pokemon.getEVs().getStat(StatsType.SpecialAttack);
            this.evs[4] = pokemon.getEVs().getStat(StatsType.SpecialDefence);
            this.evs[5] = pokemon.getEVs().getStat(StatsType.Speed);
            this.totalEVs = this.evs[0] + this.evs[1] + this.evs[2] + this.evs[3] + this.evs[4] + this.evs[5];
            this.EVPercent = df.format((double)this.totalEVs / 510.0 * 100.0) + "%";
            this.hiddenPower = HiddenPower.getHiddenPowerType(pokemon.getIVs()).getLocalizedName();

            AuraStorage auras = new AuraStorage(pokemon.getPersistentData());
            if(auras.hasAuras()) {
                auras.getAuras().forEach(aura -> {
                    if(aura.isEnabled()) this.PokemonAuras.add(aura.getAuraDefinition().getDisplayName() + " " + aura.getEffectDefinition().getDisplayName());
                });
            }
            this.AurasEnabled = auras.aurasEnabled();
        }
    }

    public ItemStack getSprite(int display, Listing listing) {
        Optional<ItemType> sprite = Sponge.getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite");
        ItemStack Item = ItemStack.builder().itemType(sprite.get()).build();
        ItemStack item = this.setPicture(Item);
        this.setItemData(item, display, listing);
        return ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "ListingID"), this.listingID)).build();
    }

    private ItemStack setPicture(ItemStack item) {
        String spriteData = this.isEgg ? "pixelmon:" + GuiResources.getEggSprite(this.pokemon.getSpecies(), 0) : "pixelmon:" + GuiResources.getSpritePath(this.pokemon.getSpecies(), this.pokemon.getForm(), this.pokemon.getGender(), this.pokemon.getCustomTexture(), this.pokemon.isShiny());
        return ItemStack.builder().fromContainer(item.toContainer().set(DataQuery.of("UnsafeData", "SpriteName"), spriteData)).build();
    }

    private void setItemData(ItemStack item, int display, Listing listing) {
        String displayName = this.pokerus + "&b" + ListingUtils.updatePokemonName(this.name) + " &7| &eLvl " + this.lvl + " " + (this.shiny ? "&7(&6Shiny&7)&r" : "");
        String pokeGender = this.gender.equals("Male") ? "&b" + this.gender + " \u2642" : (this.gender.equals("Female") ? "&d" + this.gender + " \u2640" : "&8Genderless \u26a5");
        if (this.isEgg) {
            item.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours("&bPok\u00e9mon Egg")));
        } else {
            item.offer(Keys.DISPLAY_NAME, Text.of(Chat.embedColours(displayName)));
        }
        Time time = new Time(this.expires.toInstant().toEpochMilli());
        ArrayList<Text> itemLore = new ArrayList<Text>();
        if (display == 0) {
            itemLore.add(Text.of(Chat.embedColours(((listing.getisTrade() ? "&7Trader: " : "&7Seller: ") + "&b" + ListingUtils.getNameFromUUID(this.owner).get()))));
            itemLore.add(Text.of(Chat.embedColours((listing.getisTrade() ? "&7Looking for: &b" + listing.getRequest().getName() + " &7(&eClick for more info&7)" : "&7Price: &bP" + this.price))));
            itemLore.add(Text.of(Chat.embedColours(("&7Expires: &b" + time.toString("%1$dd %2$dh %3$dm")))));
            itemLore.add(Text.of(Chat.embedColours("&7--------------------")));
        } else if (display == 1) {
            itemLore.add(Text.of(Chat.embedColours((listing.getisTrade() ? "&7Trade Offer" : "&7Price: &bP" + this.price))));
            itemLore.add(Text.of(Chat.embedColours(("&7Expires: &b" + time.toString("%1$dd %2$dh %3$dm")))));
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&c* Left click to remove this listing from the gts.")));
            itemLore.add(Text.of(Chat.embedColours("&c* Right click to re-list an expired item.")));
            itemLore.add(Text.of(Chat.embedColours("&7--------------------")));
        }
        if (!this.isEgg) {
            if (new PokemonSpec("unbreedable").matches(this.pokemon)) {
                itemLore.add(Text.of(Chat.embedColours("&4Unbreedable")));
            }
            if (!PokemonAuras.isEmpty()) {
                itemLore.add(Text.of(Chat.embedColours("&7Aura(s): " + this.PokemonAuras.get(0) + (this.AurasEnabled > 1 ? " + " + this.PokemonAuras.get(1) : ""))));
            }
            if (!this.formName.isEmpty()) {
                itemLore.add(Text.of(Chat.embedColours("&7Form: &e" + ListingUtils.capitalize(this.formName))));
            }
            if (this.isTrio) {
                itemLore.add(Text.of(Chat.embedColours("&7Ruby Enchant: &e" + (this.numEnchanted != 0 ? this.numEnchanted + " Available" : "None Available"))));
            }
            if (!Strings.isNullOrEmpty(this.customTexture)) {
                itemLore.add(Text.of(Chat.embedColours("&7Custom Texture: &e" + ListingUtils.capitalize(this.customTexture))));
            }
            if (!this.pokemon.getHeldItem().getDisplayName().equalsIgnoreCase("Air")) {
                itemLore.add(Text.of(Chat.embedColours("&7Held Item: &e" + this.heldItem)));
            }
            itemLore.add(Text.of(Chat.embedColours("&7Ability: &e" + this.ability + (this.isHiddenAbility ? " &7(&6HA&7)&r" : ""))));
            itemLore.add(Text.of(Chat.embedColours("&7Nature: &e" + this.nature + this.mintNature)));
            itemLore.add(Text.of(Chat.embedColours("&7Gender: " + pokeGender)));
            itemLore.add(Text.of(Chat.embedColours("&7Size: &e" + this.growth)));
            itemLore.add(Text.of(Chat.embedColours("&7Happiness: &e" + this.happiness)));
            itemLore.add(Text.of(Chat.embedColours("&7Hidden Power: &e" + this.hiddenPower)));
            itemLore.add(Text.of(Chat.embedColours("&7CaughtBall: &e" + this.caughtBall)));
            itemLore.add(Text.of(Chat.embedColours("")));
            if (this.wasHyperTrained) {
                itemLore.add(Text.of(Chat.embedColours("&7IVs in &3aqua &7have been hyper-trained!")));
            }
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&7IVs: &e" + this.totalIVs + "&7/&e186 &7(&a" + this.IVPercent + "&7)")));
            itemLore.add(Text.of(Chat.embedColours("&cHP: " + this.ht[0] + this.ivs[0] + " &7/ &6Atk: " + this.ht[1] + this.ivs[1] + " &7/ &eDef: " + this.ht[2] + this.ivs[2])));
            itemLore.add(Text.of(Chat.embedColours("&9SpA: " + this.ht[3] + this.ivs[3] + " &7/ &aSpD: " + this.ht[4] + this.ivs[4] + " &7/ &dSpe: " + this.ht[5] + this.ivs[5])));
            itemLore.add(Text.of(Chat.embedColours("&7EVs: &e" + this.totalEVs + "&7/&e510 &7(&a" + this.EVPercent + "&7)")));
            itemLore.add(Text.of(Chat.embedColours("&cHP: " + this.evs[0] + " &7/ &6Atk: " + this.evs[1] + " &7/ &eDef: " + this.evs[2])));
            itemLore.add(Text.of(Chat.embedColours("&9SpA: " + this.evs[3] + " &7/ &aSpD: " + this.evs[4] + " &7/ &dSpe: " + this.evs[5])));
            itemLore.add(Text.of(Chat.embedColours("")));
            itemLore.add(Text.of(Chat.embedColours("&7Moves:")));
            itemLore.add(Text.of(Chat.embedColours("&b" + this.m1 + " &7- &b" + this.m2)));
            itemLore.add(Text.of(Chat.embedColours("&b" + this.m3 + " &7- &b" + this.m4)));
        } else {
            itemLore.add(Text.of(Chat.embedColours("&7Wait til it hatches first...")));
        }
        item.offer(Keys.ITEM_LORE, itemLore);
    }
}


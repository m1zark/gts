/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.m1zark.m1utilities.api.Chat
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 *  com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec
 *  com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower
 *  com.pixelmonmod.pixelmon.config.PixelmonConfig
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType
 *  com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.LakeTrioStats
 *  com.pixelmonmod.pixelmon.enums.EnumNature
 *  com.pixelmonmod.pixelmon.enums.EnumSpecies
 *  de.waterdu.aquaauras.file.FileManager
 *  de.waterdu.aquaauras.file.structures.AuraDefinition
 *  de.waterdu.aquaauras.file.structures.EffectDefinition
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.command.args.CommandContext
 *  org.spongepowered.api.command.spec.CommandExecutor
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.service.pagination.PaginationList
 *  org.spongepowered.api.text.LiteralText
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.action.ClickAction
 *  org.spongepowered.api.text.action.HoverAction
 *  org.spongepowered.api.text.action.TextActions
 *  org.spongepowered.api.text.channel.MessageReceiver
 *  org.spongepowered.api.text.format.TextColor
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.m1zark.gts.GTS;
import com.m1zark.gts.Logs.Log;
import com.m1zark.gts.Logs.LogAction;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.m1utilities.api.Chat;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.HiddenPower;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import de.waterdu.aquaauras.auras.AuraStorage;
import de.waterdu.aquaauras.helper.FileHelper;
import de.waterdu.aquaauras.structures.AuraDefinition;
import de.waterdu.aquaauras.structures.EffectDefinition;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class Logs
implements CommandExecutor {
    private static List<Log> list = Lists.newArrayList();

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "You must be logged onto the server to run this command."}));
        }
        Logs.removeExpired();
        list = GTS.getInstance().getSql().getAllLogs();
        Optional p = args.getOne((Text)Text.of((String)"player"));
        p.ifPresent(pl -> {
            list = list.stream().filter(log -> log.getBuyer().equals(pl) || log.getSeller().equals(pl)).collect(Collectors.toList());
        });
        if (list.isEmpty()) {
            throw new CommandException(Text.of((Object[])new Object[]{TextColors.RED, "There are no current logs to display."}));
        }
        list.sort(Comparator.comparing(Log::getDate).reversed());
        PaginationList.builder().contents(Logs.displayLogs(list)).title(Text.of((Object[])new Object[]{Chat.embedColours((String)"&7GTS Logs")})).build().sendTo((MessageReceiver)src);
        return CommandResult.success();
    }

    private static List<Text> displayLogs(List<Log> list) {
        ArrayList<Text> texts = new ArrayList<Text>();
        for (Log log : list) {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy @ h:nn a");
            df.setTimeZone(TimeZone.getTimeZone("EST"));
            Text time = Text.of((Object[])new Object[]{Chat.embedColours((String)("&7" + df.format(log.getDate())))});
            LiteralText delete = Text.builder((String)"[x]").color(TextColors.RED).onHover((HoverAction)TextActions.showText((Text)Text.of((Object[])new Object[]{TextColors.RED, "Click here to delete this log."}))).onClick((ClickAction)TextActions.executeCallback(s -> Logs.deleteLog(log, s))).build();
            texts.add(Text.of((Object[])new Object[]{time, " ", Logs.buildLogInfo(log), " ", delete}));
        }
        return texts;
    }

    private static Text buildLogInfo(Log log) {
        Pokemon pokemon = log.getAction().equals((Object)LogAction.Auction) ? log.getAuction().getPokemon() : log.getListing().getPokemon();
        String displayName = ListingUtils.updatePokemonName(pokemon.getSpecies().name());
        String displayNickname = pokemon.getNickname();
        String pokerus = pokemon.getPokerus() != null ? (pokemon.getPokerus().canInfect() ? "&d[PKRS] " : "&7&m[PKRS] ") : "";
        boolean isTrio = false;
        Gender gender = pokemon.getGender();
        EVStore eVsStore = null;
        IVStore ivStore = null;
        EnumNature nature = pokemon.getNature();
        Boolean isShiny = pokemon.isShiny();
        Moveset moveset = pokemon.getMoveset();
        String heldItem = pokemon.getHeldItem() != null ? pokemon.getHeldItem().getDisplayName() : "Nothing";
        boolean inRanch = pokemon.isInRanch();
        boolean isEgg = pokemon.isEgg();
        String formName = "";
        if (pokemon.getForm() != 0) {
            String form = pokemon.getFormEnum().getFormSuffix();
            formName = form.startsWith("-") ? form.substring(1) : form;
        }
        switch (pokemon.getSpecies()) {
            case Mesprit: 
            case Azelf: 
            case Uxie: {
                isTrio = true;
            }
        }
        int ivSum = 0;
        int evSum = 0;
        if (pokemon.getStats() != null) {
            eVsStore = pokemon.getEVs();
            ivStore = pokemon.getIVs();
            ivSum = ivStore.hp + ivStore.attack + ivStore.defence + ivStore.specialAttack + ivStore.specialDefence + ivStore.speed;
            evSum = eVsStore.hp + eVsStore.attack + eVsStore.defence + eVsStore.specialAttack + eVsStore.specialDefence + eVsStore.speed;
        }
        TextColor nameColor = TextColors.DARK_AQUA;
        String pokeName = "&3" + displayName;
        if (isShiny.booleanValue() && !isEgg) {
            nameColor = TextColors.GOLD;
            pokeName = "&e" + displayName;
        }
        if (EnumSpecies.legendaries.contains(displayName)) {
            nameColor = TextColors.LIGHT_PURPLE;
            pokeName = "&d" + displayName;
        }
        String pokeGender = gender.toString().equals("Female") ? "&d" + gender.toString() + " \u2640" : (gender.toString().equals("Male") ? "&b" + gender.toString() + " \u2642" : "&8Genderless \u26a5");
        ArrayList<String> moves = new ArrayList<String>();
        moves.add(moveset.get(0) == null ? "&bNone" : "&b" + moveset.get(0).getActualMove().getLocalizedName());
        moves.add(moveset.get(1) == null ? "&bNone" : "&b" + moveset.get(1).getActualMove().getLocalizedName());
        moves.add(moveset.get(2) == null ? "&bNone" : "&b" + moveset.get(2).getActualMove().getLocalizedName());
        moves.add(moveset.get(3) == null ? "&bNone" : "&b" + moveset.get(3).getActualMove().getLocalizedName());

        List<String> PokemonAuras = new ArrayList<>();
        AuraStorage auras = new AuraStorage(pokemon.getPersistentData());
        if(auras.hasAuras()) {
            auras.getAuras().forEach(aura -> {
                if(aura.isEnabled()) PokemonAuras.add(aura.getAuraDefinition().getDisplayName() + " " + aura.getEffectDefinition().getDisplayName());
            });
        }

        String customTexture = pokemon.getCustomTexture();
        DecimalFormat df = new DecimalFormat("#0.##");
        int numEnchants = 0;
        try {
            if (pokemon.getExtraStats() != null && pokemon.getExtraStats() instanceof LakeTrioStats) {
                LakeTrioStats extra = (LakeTrioStats)pokemon.getExtraStats();
                numEnchants = PixelmonConfig.lakeTrioMaxEnchants - extra.numEnchanted;
            }
        }
        catch (Exception extra) {
            // empty catch block
        }
        String mint = "";
        StatsType increasedStat = nature.increasedStat;
        StatsType decreasedStat = nature.decreasedStat;
        if (pokemon.getMintNature() != null) {
            mint = "&7|&b" + pokemon.getMintNature().getName();
            increasedStat = pokemon.getMintNature().increasedStat;
            decreasedStat = pokemon.getMintNature().decreasedStat;
        }
        String pokeStats = pokerus + pokeName + " &7| &eLvl " + pokemon.getLevel() + " " + (isShiny != false ? "&7(&6Shiny&7)&r " : "") + "\n&r" + (new PokemonSpec("untradeable").matches(pokemon) ? "&4Untradeable\n&r" : "") + (new PokemonSpec("unbreedable").matches(pokemon) ? "&4Unbreedable\n&r" : "") + (!PokemonAuras.isEmpty() ? "&7Aura(s): " + PokemonAuras.get(0) + (auras.aurasEnabled() > 1 ? " + " + PokemonAuras.get(1) : "") + "\n&r" : "") + (!formName.isEmpty() ? "&7Form: &e" + ListingUtils.capitalize(formName) + "\n&r" : "") + (isTrio ? "&7Ruby Enchant: &e" + (numEnchants != 0 ? numEnchants + " Available" : "None Available") + "\n&r" : "") + (!Strings.isNullOrEmpty((String)customTexture) ? "&7Custom Texture: &e" + ListingUtils.capitalize(customTexture) + "\n&r" : "") + (pokemon.getHeldItem() != null ? "&7Held Item: &e" + heldItem + "\n&r" : "") + "&7Ability: &e" + pokemon.getAbility().getName() + (ListingUtils.isHiddenAbility(pokemon, pokemon.getAbility().getName()) ? " &7(&6HA&7)&r" : "") + "\n&r&7Nature: &e" + nature.toString() + mint + " &7(&a+" + ListingUtils.getNatureShorthand(increasedStat) + " &7| &c-" + ListingUtils.getNatureShorthand(decreasedStat) + "&7)\n&r&7Gender: " + pokeGender + "\n&r&7Size: &e" + pokemon.getGrowth().toString() + "\n&r&7Happiness: &e" + pokemon.getFriendship() + "\n&r&7Hidden Power: &e" + HiddenPower.getHiddenPowerType((IVStore)pokemon.getIVs()).getLocalizedName() + "\n&r&7Caught Ball: &e" + pokemon.getCaughtBall().name() + "\n\n&r&7IVs: &e" + ivSum + "&7/&e186 &7(&a" + df.format((int)((double)ivSum / 186.0 * 100.0)) + "%&7) \n&cHP: " + ivStore.hp + " &7/ &6Atk: " + ivStore.attack + " &7/ &eDef: " + ivStore.defence + "\n&9SpA: " + ivStore.specialAttack + " &7/ &aSpD: " + ivStore.specialDefence + " &7/ &dSpe: " + ivStore.speed + "\n&7EVs: &e" + evSum + "&7/&e510 &7(&a" + df.format((int)((double)evSum / 510.0 * 100.0)) + "%&7) \n&cHP: " + eVsStore.hp + " &7/ &6Atk: " + eVsStore.attack + " &7/ &eDef: " + eVsStore.defence + "\n&9SpA: " + eVsStore.specialAttack + " &7/ &aSpD: " + eVsStore.specialDefence + " &7/ &dSpe: " + eVsStore.speed + "\n\n&7Moves:\n" + (String)moves.get(0) + " &7- " + (String)moves.get(1) + "\n" + (String)moves.get(2) + " &7- " + (String)moves.get(3);
        if (!isEgg) {
            return Text.builder().color(TextColors.GREEN).append(Text.of(Chat.embedColours("[Log Details]"))).onHover(TextActions.showText(Text.of(Chat.embedColours(pokeStats)))).build();
        }
        return Text.builder().color(nameColor).append(Text.of(Chat.embedColours("[Pok\u00e9mon Egg]"))).onHover(TextActions.showText(Text.of(Chat.embedColours("&7Wait til it hatches first...")))).build();
    }

    private static void deleteLog(Log log, CommandSource src) {
        if (src.hasPermission("gts.admin.remove.logs")) {
            GTS.getInstance().getSql().removeLog(log.getId());
            Chat.sendMessage((CommandSource)src, (String)"&cLog deleted!");
        } else {
            Chat.sendMessage((CommandSource)src, (String)"&cYou don't have permissions to do that.");
        }
    }

    private static void removeExpired() {
        GTS.getInstance().getSql().getAllLogs().forEach(log -> {
            long daysElapsed = ChronoUnit.DAYS.between(log.getDate().toInstant(), Instant.now());
            if (daysElapsed >= (long)ConfigManager.removeExpired) {
                GTS.getInstance().getSql().removeLog(log.getId());
            }
        });
    }
}


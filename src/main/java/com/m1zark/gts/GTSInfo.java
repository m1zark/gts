/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.text.format.TextColors
 */
package com.m1zark.gts;

import com.m1zark.gts.GTS;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GTSInfo {
    public static final String ID = "gts";
    public static final String NAME = "GTS";
    public static final String VERSION = "1.4.0-S7.2";
    public static final String DESCRIPTION = "Buying and selling of Pokemon via in-game market UI.";
    public static final Text PREFIX = Text.of((Object[])new Object[]{TextColors.AQUA, "GTS ", TextColors.GRAY, "\u00bb ", TextColors.DARK_AQUA});
    public static final Text ERROR_PREFIX = Text.of((Object[])new Object[]{TextColors.RED, "GTS ", TextColors.GRAY, "(", TextColors.RED, "Error", TextColors.GRAY, ") ", TextColors.DARK_RED});
    public static final Text DEBUG_PREFIX = Text.of((Object[])new Object[]{TextColors.AQUA, "GTS ", TextColors.GRAY, "(", TextColors.RED, "Debug", TextColors.GRAY, ") ", TextColors.DARK_AQUA});

    static void startup() {
        GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{TextColors.AQUA, NAME, " v.", TextColors.GREEN, VERSION}), Text.of((Object[])new Object[]{TextColors.GREEN, "Author: ", TextColors.AQUA, "m1zark"}), Text.EMPTY}));
    }

    static boolean dependencyCheck() {
        boolean valid = true;
        for (Dependencies dependency : Dependencies.values()) {
            if (Sponge.getPluginManager().isLoaded(dependency.getDependency())) continue;
            GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{ERROR_PREFIX, Text.of((Object[])new Object[]{TextColors.DARK_RED, "==== Missing Dependency ===="})})}));
            GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{ERROR_PREFIX, Text.of((Object[])new Object[]{TextColors.DARK_RED, "  Dependency: ", TextColors.RED, dependency.name()})})}));
            GTS.getInstance().getConsole().ifPresent(console -> console.sendMessages(new Text[]{Text.of((Object[])new Object[]{ERROR_PREFIX, Text.of((Object[])new Object[]{TextColors.DARK_RED, "  Version: ", TextColors.RED, dependency.getVersion()})})}));
            valid = false;
        }
        return valid;
    }

    public static enum Dependencies {
        Pixelmon("pixelmon", "8.x.x+"),
        M1utilities("m1utilities", "1.x.x+");

        private String dependency;
        private String version;

        private Dependencies(String dependency, String version) {
            this.dependency = dependency;
            this.version = version;
        }

        public String getDependency() {
            return this.dependency;
        }

        public String getVersion() {
            return this.version;
        }
    }
}


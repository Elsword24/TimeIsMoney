package com.lightsoul.timeismoney;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

//The config class for the mod you can find here :
//Number of item to give, mimimum time, maximum time
//AFK detection is here too.

public final class Config {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue PAY_MIN_MINUTES;
    public static final ModConfigSpec.IntValue PAY_MAX_MINUTES;
    public static final ModConfigSpec.IntValue PAY_AMOUNT;
    public static final ModConfigSpec.IntValue AFK_MINUTES;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.push("payday");
        PAY_MIN_MINUTES = b.comment("Minimum minutes between payouts").defineInRange("pay_min_minutes", 15,1,1440);
        PAY_MAX_MINUTES = b.comment("Maximum minutes between payoutsd").defineInRange("pay_max_minutes", 30,1,1440);
        PAY_AMOUNT = b.comment("Ammout payued at each payday (emeralds for debug)").defineInRange("pay_amout", 1,0,64);
        b.pop();

        b.push("afk");
        AFK_MINUTES = b.comment("AFK treshold in minutes").defineInRange("afk_minutes", 3,1,5);
        b.pop();

        SPEC = b.build();
    }

    private Config() {}
}
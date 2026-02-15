package com.lightsoul.timeismoney;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.ThreadLocalRandom;

public final class PaydayService {

    private int ticksUntilPayday = -1;
    private final AFKTracker afkTracker;

    public PaydayService(AFKTracker afkTracker) {
        this.afkTracker = afkTracker;
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (ticksUntilPayday < 0) {
            ticksUntilPayday = rollNextDelayTick();
            return;
        }
        if (--ticksUntilPayday > 0) return;

        payActivePlayers(server);
        ticksUntilPayday = rollNextDelayTick();
    }

    private void payActivePlayers(MinecraftServer server) {
        int amount = Config.PAY_AMOUNT.get();
        if (amount <= 0) return;

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            if (afkTracker.isAfk(sp)) continue;

            sp.getInventory().add(new ItemStack(Items.EMERALD, amount));
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "[TimeIsMoney] Payday! You got " + amount + " emeralds"
            ));
        }
    }

    private static int rollNextDelayTick() {
        int min = Config.PAY_MIN_MINUTES.get();
        int max = Config.PAY_MAX_MINUTES.get();
        if (max < min) max = min;

        int minutes = ThreadLocalRandom.current().nextInt(min, max + 1);
        return minutes * 60 * 20;
    }

    private static void claimRewards(Player player) {
        //player can ask for his rewards
    }

    private static void queueRewards(MinecraftServer server)
    {
        //server can put rewards in a queue for the player.
    }
}

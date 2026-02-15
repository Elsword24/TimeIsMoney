package com.lightsoul.timeismoney;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class AFKTracker {

    private static final class Activity {
        long lastActivityTick;
        double x,y,z;
        float yaw,pitch;
        boolean afk;
        //UUID playerID; Keep track of player between relaunch
    }

    private final Map<UUID,Activity> data = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void OnPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;

        Activity a = data.computeIfAbsent(sp.getUUID(), id -> init(sp));
        long now = sp.serverLevel().getGameTime();

        boolean moved = move(sp,a);
        boolean rotated = rotated(sp,a);

        if (moved || rotated) {
            a.lastActivityTick = now;
            a.x = sp.getX(); a.y = sp.getY(); a.z = sp.getZ();
            a.yaw = sp.getYRot(); a.pitch = sp.getXRot();
        }

        long afkTicks = Config.AFK_MINUTES.get() *60L * 20L;
        a.afk = (now - a.lastActivityTick) >= afkTicks;
    }

    public boolean isAfk(ServerPlayer sp) {
        Activity a = data.get(sp.getUUID());
        return a != null && a.afk;
    }

    public static Activity init(ServerPlayer sp) {
        Activity a = new Activity();
        long now = sp.serverLevel().getGameTime();
        a.lastActivityTick = now;
        a.x = sp.getX(); a.y = sp.getY(); a.z = sp.getZ();
        a.yaw = sp.getYRot(); a.pitch = sp.getXRot();
        a.afk = false;
        return a;
    }

    private static boolean move(ServerPlayer sp, Activity a) {
        final double eps = 1.0e-4;
        return Math.abs(sp.getX() - a.x) > eps || Math.abs(sp.getY() - a.y) > eps || Math.abs(sp.getZ() - a.z) > eps;
    }

    private static boolean rotated(ServerPlayer sp, Activity a) {
        final float eps = 0.01f;
        return Math.abs(sp.getYRot() - a.yaw) > eps || Math.abs(sp.getXRot() - a.pitch) > eps;
    }

}

package com.flymod.sound;

import com.flymod.config.FlyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class FlySoundManager {
    public static final FlySoundManager INSTANCE = new FlySoundManager();

    private final Map<UUID, FlyBuzzSoundInstance> activeSounds = new HashMap<>();
    private final Map<UUID, Vec3> previousPositions = new HashMap<>();

    public void tick(Minecraft mc) {
        if (mc.level == null || !FlyConfig.INSTANCE.enabled) {
            stopAll();
            return;
        }

        // Clean up stopped or inactive sounds
        Iterator<Map.Entry<UUID, FlyBuzzSoundInstance>> iterator = activeSounds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FlyBuzzSoundInstance> entry = iterator.next();
            FlyBuzzSoundInstance sound = entry.getValue();
            if (sound.isStopped() || !mc.getSoundManager().isActive(sound)) {
                iterator.remove();
            }
        }

        Vec3 listenerPos = (mc.getCameraEntity() != null) ? mc.getCameraEntity().position() : (mc.player != null ? mc.player.position() : null);
        if (listenerPos == null) {
            return;
        }

        // Iterate through all players in the level
        for (AbstractClientPlayer player : mc.level.players()) {
            UUID uuid = player.getUUID();

            // Strictly check if player is self
            boolean isSelf = (mc.player != null && (player == mc.player || uuid.equals(mc.player.getUUID())));
            if (isSelf && !FlyConfig.INSTANCE.enableForSelf) {
                previousPositions.remove(uuid);
                continue;
            }

            // Calculate actual physical displacement between ticks (DO NOT use getDeltaMovement on remote players!)
            Vec3 currentPos = player.position();
            Vec3 prevPos = previousPositions.put(uuid, currentPos);
            if (prevPos == null) {
                prevPos = new Vec3(player.xo, player.yo, player.zo);
            }
            double speed = currentPos.distanceTo(prevPos);

            // If player is stationary or standing still (< 0.10 blocks/tick), do NOT start sound!
            if (speed < 0.10) {
                continue;
            }

            if (activeSounds.containsKey(uuid)) {
                continue;
            }

            // Must be flying (creative flight, elytra, trident riptide, etc.)
            if (!FlightDetector.isFlying(player, speed)) {
                continue;
            }

            // Must be within hearing range
            double distance = currentPos.distanceTo(listenerPos);
            if (distance <= FlyConfig.INSTANCE.maxDistance + 4.0) {
                FlyBuzzSoundInstance sound = new FlyBuzzSoundInstance(player);
                mc.getSoundManager().play(sound);
                activeSounds.put(uuid, sound);
            }
        }
    }

    public void stopAll() {
        for (FlyBuzzSoundInstance sound : activeSounds.values()) {
            if (!sound.isStopped()) {
                sound.stopSound();
            }
        }
        activeSounds.clear();
        previousPositions.clear();
    }
}

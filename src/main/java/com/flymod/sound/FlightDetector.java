package com.flymod.sound;

import com.flymod.config.FlyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;

public class FlightDetector {
    /**
     * Determines whether a player is actively flying (Creative, Elytra, Trident Riptide, or modded flight).
     *
     * @param player The player to check
     * @param speed The player's actual coordinate displacement per tick
     * @return true if the player is actively flying and moving
     */
    public static boolean isFlying(Player player, double speed) {
        if (player == null || player.isRemoved() || !player.isAlive()) {
            return false;
        }

        // If player is on ground, swimming, in water, in lava, climbing ladder or in vehicle: definitely NOT flying
        if (player.onGround() || player.isInWater() || player.isInLava() || player.onClimbable() || player.isPassenger()) {
            return false;
        }

        // If actual speed is below 0.10 blocks/tick, player is hovering still or stationary: NO buzz
        if (speed < 0.10) {
            return false;
        }

        // 1. Trident Riptide spin attack (Survival & Creative)
        if (FlyConfig.INSTANCE.enableTrident && player.isAutoSpinAttack()) {
            return true;
        }

        // 2. Elytra gliding / rocket-boosting (Survival & Creative)
        if (FlyConfig.INSTANCE.enableElytra && player.isFallFlying()) {
            return true;
        }

        // 3. Creative flight ability flag (always active on LocalPlayer and on servers syncing abilities)
        if (player.getAbilities().flying) {
            return true;
        }

        // Check if there is solid ground directly underneath (within 0.35 blocks)
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            AABB boxBelow = player.getBoundingBox().move(0, -0.35, 0);
            if (!mc.level.noCollision(player, boxBelow)) {
                return false;
            }
        }

        // 4. Remote players in Creative / Spectator mode:
        if (mc.getConnection() != null) {
            PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(player.getUUID());
            if (playerInfo != null) {
                GameType mode = playerInfo.getGameMode();
                if (mode == GameType.CREATIVE || mode == GameType.SPECTATOR) {
                    double deltaY = player.getY() - player.yo;
                    // When flying in creative/spectator, player is moving and not falling under normal terminal gravity
                    if (deltaY > -0.35) {
                        return true;
                    }
                }
            }
        }

        // 5. Physics-based airborne flight detection in Survival (momentum from Riptide, cannons, wind charges, modded flight):
        double deltaX = player.getX() - player.xo;
        double deltaY = player.getY() - player.yo;
        double deltaZ = player.getZ() - player.zo;
        double horizontalSpeed = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // A) High speed airborne flight (Trident launch, wind charge launch, Elytra, fast propulsion)
        if (speed > 0.35) {
            return true;
        }

        // B) Horizontal flight at constant altitude in air (dy near 0 while moving) - impossible in vanilla gravity without flight
        if (horizontalSpeed > 0.12 && Math.abs(deltaY) < 0.08) {
            return true;
        }

        // C) Ascending in air without ground (dy > 0.08)
        if (deltaY > 0.08 && speed > 0.12) {
            return true;
        }

        return false;
    }
}

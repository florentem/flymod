package com.flymod.sound;

import com.flymod.config.FlyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlyBuzzSoundInstance extends AbstractTickableSoundInstance {
    private final Player player;
    private int stoppedMovingTicks = 0;
    private double prevX;
    private double prevY;
    private double prevZ;

    public FlyBuzzSoundInstance(Player player) {
        super(FlySoundEvents.FLY_BUZZ, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.05f;
        this.pitch = 1.0f;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.prevX = player.getX();
        this.prevY = player.getY();
        this.prevZ = player.getZ();
        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void stopSound() {
        this.stop();
    }

    @Override
    public void tick() {
        if (this.isStopped()) {
            return;
        }

        if (this.player.isRemoved() || !this.player.isAlive()) {
            this.stop();
            return;
        }

        // Follow player coordinates in 3D space
        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();

        // Calculate actual displacement between ticks
        double deltaX = this.player.getX() - this.prevX;
        double deltaY = this.player.getY() - this.prevY;
        double deltaZ = this.player.getZ() - this.prevZ;
        this.prevX = this.player.getX();
        this.prevY = this.player.getY();
        this.prevZ = this.player.getZ();

        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        // Check flight condition
        boolean isFlying = FlightDetector.isFlying(this.player, speed);

        // If player is stationary in the air (speed < 0.10) or landed or mod disabled: immediately silence and stop
        if (!isFlying || speed < 0.10 || !FlyConfig.INSTANCE.enabled) {
            this.stoppedMovingTicks++;
            // Rapid fade-out: reduce volume by 50% each tick
            this.volume *= 0.5f;
            if (this.stoppedMovingTicks >= 4 || this.volume < 0.01f) {
                this.stop();
            }
            return;
        }

        this.stoppedMovingTicks = 0;

        // Distance to camera/listener
        Minecraft mc = Minecraft.getInstance();
        Vec3 listenerPos = (mc.getCameraEntity() != null) ? mc.getCameraEntity().position() : (mc.player != null ? mc.player.position() : Vec3.ZERO);
        double distance = this.player.position().distanceTo(listenerPos);
        double maxDist = FlyConfig.INSTANCE.maxDistance;

        if (distance > maxDist + 6.0) {
            this.stop();
            return;
        }

        // Distance factor: quadratic falloff (closer = significantly louder)
        float distNorm = (float) Math.max(0.0, 1.0 - (distance / maxDist));
        float distFactor = distNorm * distNorm;

        // Speed factor: strictly 0 at speed <= 0.10, ramp up with flight speed
        float speedNorm = (float) ((speed - 0.10) * FlyConfig.INSTANCE.speedSensitivity);
        float speedFactor = Mth.clamp(speedNorm * 2.2f, 0.0f, 2.5f);

        // Target volume calculation
        float targetVolume = distFactor * speedFactor * FlyConfig.INSTANCE.masterVolume;
        targetVolume = Mth.clamp(targetVolume, 0.0f, 2.0f);

        // Smooth volume transitions
        this.volume = Mth.lerp(0.4f, this.volume, targetVolume);

        // Doppler pitch modulation
        if (FlyConfig.INSTANCE.dopplerPitchShift) {
            float basePitch = 1.0f;
            float speedPitch = (float) (speed * 0.22f);

            Vec3 toListener = listenerPos.subtract(this.player.position());
            double distLen = toListener.length();
            float dopplerShift = 0.0f;
            if (distLen > 0.05) {
                Vec3 dirToListener = toListener.scale(1.0 / distLen);
                Vec3 velocity = new Vec3(deltaX, deltaY, deltaZ);
                double approachSpeed = velocity.dot(dirToListener);
                dopplerShift = (float) (approachSpeed * 0.35f);
            }

            float targetPitch = Mth.clamp(basePitch + speedPitch + dopplerShift, 0.7f, 1.6f);
            this.pitch = Mth.lerp(0.3f, this.pitch, targetPitch);
        } else {
            this.pitch = 1.0f;
        }
    }
}

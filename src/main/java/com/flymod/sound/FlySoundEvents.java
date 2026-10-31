package com.flymod.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class FlySoundEvents {
    public static final Identifier FLY_BUZZ_ID = Identifier.fromNamespaceAndPath("flymod", "fly_buzz");
    public static final SoundEvent FLY_BUZZ = SoundEvent.createVariableRangeEvent(FLY_BUZZ_ID);

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, FLY_BUZZ_ID, FLY_BUZZ);
    }
}

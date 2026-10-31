package com.flymod;

import com.flymod.config.FlyConfig;
import com.flymod.sound.FlySoundEvents;
import com.flymod.sound.FlySoundManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FlyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FlyConfig.load();
        FlySoundEvents.register();

        ClientTickEvents.END_CLIENT_TICK.register(FlySoundManager.INSTANCE::tick);
    }
}

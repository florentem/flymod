package com.flymod.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FlyConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.flymod.config.title"))
                .setSavingRunnable(FlyConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("text.flymod.config.category.general"));

        // Enable / Disable
        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.flymod.config.enabled"),
                        FlyConfig.INSTANCE.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.flymod.config.enabled.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.enabled = val)
                .build());

        // Master Volume
        general.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("text.flymod.config.volume"),
                        Math.round(FlyConfig.INSTANCE.masterVolume * 100),
                        0,
                        200)
                .setDefaultValue(100)
                .setTextGetter(val -> Component.literal(val + "%"))
                .setTooltip(Component.translatable("text.flymod.config.volume.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.masterVolume = val / 100.0f)
                .build());

        // Max Hearing Distance
        general.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("text.flymod.config.max_distance"),
                        FlyConfig.INSTANCE.maxDistance,
                        5,
                        64)
                .setDefaultValue(32)
                .setTextGetter(val -> Component.literal(val + " blocks"))
                .setTooltip(Component.translatable("text.flymod.config.max_distance.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.maxDistance = val)
                .build());

        // Speed Sensitivity
        general.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("text.flymod.config.speed_sensitivity"),
                        Math.round(FlyConfig.INSTANCE.speedSensitivity * 100),
                        20,
                        300)
                .setDefaultValue(100)
                .setTextGetter(val -> Component.literal(val + "%"))
                .setTooltip(Component.translatable("text.flymod.config.speed_sensitivity.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.speedSensitivity = val / 100.0f)
                .build());

        // Doppler pitch shift
        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.flymod.config.doppler"),
                        FlyConfig.INSTANCE.dopplerPitchShift)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.flymod.config.doppler.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.dopplerPitchShift = val)
                .build());

        // Enable for Self
        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.flymod.config.enable_for_self"),
                        FlyConfig.INSTANCE.enableForSelf)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("text.flymod.config.enable_for_self.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.enableForSelf = val)
                .build());

        // Enable Elytra
        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.flymod.config.enable_elytra"),
                        FlyConfig.INSTANCE.enableElytra)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.flymod.config.enable_elytra.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.enableElytra = val)
                .build());

        // Enable Trident Riptide
        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.flymod.config.enable_trident"),
                        FlyConfig.INSTANCE.enableTrident)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.flymod.config.enable_trident.tooltip"))
                .setSaveConsumer(val -> FlyConfig.INSTANCE.enableTrident = val)
                .build());

        return builder.build();
    }
}

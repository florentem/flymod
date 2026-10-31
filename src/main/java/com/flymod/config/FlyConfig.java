package com.flymod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FlyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("flymod.json");

    public static FlyConfig INSTANCE = new FlyConfig();

    public boolean enabled = true;
    public float masterVolume = 1.0f;
    public int maxDistance = 32;
    public float speedSensitivity = 1.0f;
    public boolean dopplerPitchShift = true;
    public boolean enableForSelf = false;
    public boolean enableElytra = true;
    public boolean enableTrident = true;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                FlyConfig loaded = GSON.fromJson(reader, FlyConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                }
            } catch (Exception e) {
                System.err.println("[FlyMod] Failed to load config, using defaults: " + e.getMessage());
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            System.err.println("[FlyMod] Failed to save config: " + e.getMessage());
        }
    }
}

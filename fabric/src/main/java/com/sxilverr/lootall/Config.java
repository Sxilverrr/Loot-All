package com.sxilverr.lootall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sxilverr.lootall.config.LootConfig;
import com.sxilverr.lootall.core.LootFilter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Mth;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Config {
    public static final String MOD_ID = "lootall";

    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("lootall.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Data data = new Data();

    private Config() {
    }

    public static final class Data {
        public int range = 20;
        public boolean includeMinecarts = true;
        public boolean feedbackMessage = true;
        public boolean playSound = true;
        public boolean excludeBlockedContainers = false;
        public boolean autoLooting = false;
        public int autoLootingTimer = 20;
        public boolean enableLootingTransfer = true;
        public int maxLootTransferDistance = 0;
        public boolean transferRequireSameDimension = false;
        public boolean transferRequireLoadedChunk = false;
        public List<String> skipList = new ArrayList<>();
        public LootConfig.ListMode skipListMode = LootConfig.ListMode.BLACKLIST;
        public boolean skipArmorAndTools = false;
        public boolean skipNonStackable = false;
        public boolean skipUnenchantedGear = false;
        public LootConfig.RarityMode rarityFilterMode = LootConfig.RarityMode.OFF;
        public List<String> rarityList = new ArrayList<>();
    }

    public static Data data() {
        return data;
    }

    public static boolean transferEnabledForRegistration() {
        return data.enableLootingTransfer;
    }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                try (Reader reader = Files.newBufferedReader(PATH)) {
                    Data loaded = GSON.fromJson(reader, Data.class);
                    if (loaded != null) {
                        data = loaded;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        save();
        bake();
    }

    public static void persist() {
        save();
        bake();
    }

    private static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception ignored) {
        }
    }

    private static void bake() {
        if (data.skipListMode == null) {
            data.skipListMode = LootConfig.ListMode.BLACKLIST;
        }
        if (data.rarityFilterMode == null) {
            data.rarityFilterMode = LootConfig.RarityMode.OFF;
        }
        LootConfig.range = Mth.clamp(data.range, 1, 5000);
        LootConfig.includeMinecarts = data.includeMinecarts;
        LootConfig.feedbackMessage = data.feedbackMessage;
        LootConfig.playSound = data.playSound;
        LootConfig.excludeBlockedContainers = data.excludeBlockedContainers;
        LootConfig.autoLooting = data.autoLooting;
        LootConfig.autoLootingTimer = Mth.clamp(data.autoLootingTimer, 1, 3600);
        LootConfig.enableLootingTransfer = data.enableLootingTransfer;
        LootConfig.maxLootTransferDistance = Mth.clamp(data.maxLootTransferDistance, 0, 100000);
        LootConfig.transferRequireSameDimension = data.transferRequireSameDimension;
        LootConfig.transferRequireLoadedChunk = data.transferRequireLoadedChunk;
        LootConfig.skipArmorAndTools = data.skipArmorAndTools;
        LootConfig.skipNonStackable = data.skipNonStackable;
        LootConfig.skipUnenchantedGear = data.skipUnenchantedGear;
        LootConfig.skipListMode = data.skipListMode;
        LootConfig.rarityMode = data.rarityFilterMode;
        LootFilter.rebuild(
                data.skipList != null ? data.skipList : List.of(),
                data.rarityList != null ? data.rarityList : List.of());
    }
}

package com.instellar.joinleave.utils;

import com.instellar.joinleave.JoinLeavePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DataManager {

    private static final String JOINED_KEY      = "joined-players";
    private static final String CUSTOM_JOIN_KEY  = "custom-join";
    private static final String CUSTOM_LEAVE_KEY = "custom-leave";

    private final JoinLeavePlugin plugin;
    private final File dataFile;

    private final Set<UUID>         joinedPlayers      = new HashSet<>();
    private final Map<UUID, String> customJoinMessages  = new HashMap<>();
    private final Map<UUID, String> customLeaveMessages = new HashMap<>();

    public DataManager(JoinLeavePlugin plugin) {
        this.plugin   = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
    }

    public void load() {
        joinedPlayers.clear();
        customJoinMessages.clear();
        customLeaveMessages.clear();

        if (!dataFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);

        for (String raw : cfg.getStringList(JOINED_KEY)) {
            try {
                joinedPlayers.add(UUID.fromString(raw));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping malformed UUID in data.yml: " + raw);
            }
        }

        loadCustomMessages(cfg, CUSTOM_JOIN_KEY,  customJoinMessages);
        loadCustomMessages(cfg, CUSTOM_LEAVE_KEY, customLeaveMessages);
    }

    private void loadCustomMessages(YamlConfiguration cfg, String section, Map<UUID, String> target) {
        ConfigurationSection sec = cfg.getConfigurationSection(section);
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                target.put(UUID.fromString(key), sec.getString(key));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping malformed UUID under " + section + ": " + key);
            }
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set(JOINED_KEY, joinedPlayers.stream().map(UUID::toString).toList());

        customJoinMessages.forEach((uuid, msg)  -> cfg.set(CUSTOM_JOIN_KEY  + "." + uuid, msg));
        customLeaveMessages.forEach((uuid, msg) -> cfg.set(CUSTOM_LEAVE_KEY + "." + uuid, msg));

        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public boolean hasJoined(UUID uuid) {
        return joinedPlayers.contains(uuid);
    }

    public void recordJoin(UUID uuid) {
        if (joinedPlayers.add(uuid)) {
            save();
        }
    }

    // ── Custom join message ───────────────────────────────────────────────────

    public @Nullable String getCustomJoinMessage(UUID uuid) {
        return customJoinMessages.get(uuid);
    }

    public void setCustomJoinMessage(UUID uuid, String message) {
        customJoinMessages.put(uuid, message);
        save();
    }

    public void clearCustomJoinMessage(UUID uuid) {
        customJoinMessages.remove(uuid);
        save();
    }

    // ── Custom leave message ──────────────────────────────────────────────────

    public @Nullable String getCustomLeaveMessage(UUID uuid) {
        return customLeaveMessages.get(uuid);
    }

    public void setCustomLeaveMessage(UUID uuid, String message) {
        customLeaveMessages.put(uuid, message);
        save();
    }

    public void clearCustomLeaveMessage(UUID uuid) {
        customLeaveMessages.remove(uuid);
        save();
    }
}

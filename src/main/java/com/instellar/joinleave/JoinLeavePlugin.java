package com.instellar.joinleave;

import com.instellar.joinleave.commands.JoinLeaveCommand;
import com.instellar.joinleave.listeners.PlayerJoinListener;
import com.instellar.joinleave.listeners.PlayerQuitListener;
import com.instellar.joinleave.utils.DataManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinLeavePlugin extends JavaPlugin {

    private DataManager dataManager;

    // Cached at enable-time so listeners don't repeat a map lookup every event.
    // Safe to cache because PlaceholderAPI is a soft-depend: if present it loads
    // before us, so its enabled state won't change after our onEnable.
    private boolean papiEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        papiEnabled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (papiEnabled) {
            getLogger().info("PlaceholderAPI found — placeholder support active.");
        }

        dataManager = new DataManager(this);
        dataManager.load();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        PluginCommand cmd = getCommand("joinleave");
        if (cmd != null) {
            JoinLeaveCommand executor = new JoinLeaveCommand(this);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getLogger().info("JoinLeave enabled.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
        getLogger().info("JoinLeave disabled.");
    }

    /** Whether PlaceholderAPI is installed and enabled on this server. */
    public boolean isPapiEnabled() {
        return papiEnabled;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}

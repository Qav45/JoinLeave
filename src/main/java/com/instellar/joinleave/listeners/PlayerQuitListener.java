package com.instellar.joinleave.listeners;

import com.instellar.joinleave.JoinLeavePlugin;
import com.instellar.joinleave.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitListener implements Listener {

    private final JoinLeavePlugin plugin;

    public PlayerQuitListener(JoinLeavePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(null);

        boolean disableAll   = plugin.getConfig().getBoolean("Disable-Join-LeaveMessage", false);
        boolean disableLeave = plugin.getConfig().getBoolean("Disable-Leave-Message",     false);
        boolean hidePerm     = plugin.getConfig().getBoolean("Hide-Players-With-Perms",   false);

        boolean suppressMessage = disableAll || disableLeave
                || (hidePerm && player.hasPermission("hide.players"));

        if (!suppressMessage) {
            String customMsg = plugin.getDataManager().getCustomLeaveMessage(player.getUniqueId());
            String rawMessage = customMsg != null
                    ? customMsg
                    : plugin.getConfig().getString("Leave", "&7[&a-&7] &7%player%");
            Component message = ColorUtils.parseColors(applyPlaceholders(player, rawMessage));
            Bukkit.broadcast(message);
        }
    }

    private String applyPlaceholders(Player player, String text) {
        if (plugin.isPapiEnabled()) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
        return text.replace("%player%", player.getName());
    }
}

package com.instellar.joinleave.listeners;

import com.instellar.joinleave.JoinLeavePlugin;
import com.instellar.joinleave.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

public final class PlayerJoinListener implements Listener {

    private final JoinLeavePlugin plugin;

    private static final Component JOIN_TITLE = MiniMessage.miniMessage().deserialize(
            "<gradient:#99CCFF:#0D22FF><bold>Welcome to Instellar!</bold></gradient>"
    );
    private static final Component JOIN_SUBTITLE = MiniMessage.miniMessage().deserialize(
            "<dark_gray>do /guide</dark_gray>"
    );
    private static final Title.Times TITLE_TIMES = Title.Times.times(
            Duration.ofMillis(500),
            Duration.ofMillis(5_000),
            Duration.ofMillis(1_000)
    );

    public PlayerJoinListener(JoinLeavePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(null);

        boolean disableAll   = plugin.getConfig().getBoolean("Disable-Join-LeaveMessage", false);
        boolean hidePerm     = plugin.getConfig().getBoolean("Hide-Players-With-Perms",   false);
        boolean firstJoinCfg = plugin.getConfig().getBoolean("First-Join-Message",        true);
        boolean titleEnabled = plugin.getConfig().getBoolean("Title-Onjoin",              true);

        boolean isFirstJoin = !plugin.getDataManager().hasJoined(player.getUniqueId());
        if (isFirstJoin) {
            plugin.getDataManager().recordJoin(player.getUniqueId());
        }

        boolean suppressMessage = disableAll || (hidePerm && player.hasPermission("hide.players"));

        if (!suppressMessage) {
            String customMsg = plugin.getDataManager().getCustomJoinMessage(player.getUniqueId());
            String rawMessage;
            if (customMsg != null) {
                rawMessage = customMsg;
            } else if (isFirstJoin && firstJoinCfg) {
                rawMessage = plugin.getConfig().getString(
                        "First-Join",
                        "&7[&a+&7] &a%player% &7welcome to the server!"
                );
            } else {
                rawMessage = plugin.getConfig().getString(
                        "Join",
                        "&7[&a+&7] &7%player%"
                );
            }

            Component message = ColorUtils.parseColors(applyPlaceholders(player, rawMessage));
            Bukkit.broadcast(message);
        }

        if (titleEnabled) {
            player.showTitle(Title.title(JOIN_TITLE, JOIN_SUBTITLE, TITLE_TIMES));
        }
    }

    private String applyPlaceholders(Player player, String text) {
        if (plugin.isPapiEnabled()) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
        return text.replace("%player%", player.getName());
    }
}

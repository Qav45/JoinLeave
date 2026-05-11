package com.instellar.joinleave.commands;

import com.instellar.joinleave.JoinLeavePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class JoinLeaveCommand implements CommandExecutor, TabCompleter {

    private static final int MAX_MESSAGE_LENGTH = 256;

    private final JoinLeavePlugin plugin;

    public JoinLeaveCommand(JoinLeavePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload"     -> handleReload(sender, label);
            case "setjoin"    -> handleSet(sender, label, args, true);
            case "setleave"   -> handleSet(sender, label, args, false);
            case "resetjoin"  -> handleReset(sender, true);
            case "resetleave" -> handleReset(sender, false);
            default -> sender.sendMessage(Component.text(
                    "Unknown sub-command. ", NamedTextColor.RED
            ).append(usage(label)));
        }
        return true;
    }

    private void handleReload(CommandSender sender, String label) {
        if (!sender.hasPermission("joinleave.reload")) {
            sender.sendMessage(Component.text(
                    "You don't have permission to reload JoinLeave.", NamedTextColor.RED
            ));
            return;
        }
        plugin.reloadConfig();
        plugin.getDataManager().load();
        sender.sendMessage(Component.text(
                "[JoinLeave] Configuration reloaded successfully.", NamedTextColor.GREEN
        ));
        plugin.getLogger().info(sender.getName() + " reloaded the JoinLeave configuration.");
    }

    private void handleSet(CommandSender sender, String label, String[] args, boolean isJoin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }
        if (!player.hasPermission("joinleave.custom")) {
            player.sendMessage(Component.text(
                    "You don't have permission to set a custom message.", NamedTextColor.RED
            ));
            return;
        }
        if (args.length < 2) {
            String sub = isJoin ? "setjoin" : "setleave";
            player.sendMessage(Component.text(
                    "Usage: /" + label + " " + sub + " <message>  (must include %player%, max "
                            + MAX_MESSAGE_LENGTH + " chars, supports & color codes)",
                    NamedTextColor.YELLOW
            ));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (!message.contains("%player%")) {
            player.sendMessage(Component.text(
                    "Your message must include %player% so the player's name appears.",
                    NamedTextColor.RED
            ));
            return;
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            player.sendMessage(Component.text(
                    "Your message is too long (" + message.length() + "/" + MAX_MESSAGE_LENGTH + " chars).",
                    NamedTextColor.RED
            ));
            return;
        }

        if (isJoin) {
            plugin.getDataManager().setCustomJoinMessage(player.getUniqueId(), message);
            player.sendMessage(Component.text(
                    "[JoinLeave] Custom join message set: " + message, NamedTextColor.GREEN
            ));
        } else {
            plugin.getDataManager().setCustomLeaveMessage(player.getUniqueId(), message);
            player.sendMessage(Component.text(
                    "[JoinLeave] Custom leave message set: " + message, NamedTextColor.GREEN
            ));
        }
    }

    private void handleReset(CommandSender sender, boolean isJoin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return;
        }
        if (!player.hasPermission("joinleave.custom")) {
            player.sendMessage(Component.text(
                    "You don't have permission to reset your custom message.", NamedTextColor.RED
            ));
            return;
        }
        if (isJoin) {
            plugin.getDataManager().clearCustomJoinMessage(player.getUniqueId());
            player.sendMessage(Component.text(
                    "[JoinLeave] Custom join message cleared.", NamedTextColor.GREEN
            ));
        } else {
            plugin.getDataManager().clearCustomLeaveMessage(player.getUniqueId());
            player.sendMessage(Component.text(
                    "[JoinLeave] Custom leave message cleared.", NamedTextColor.GREEN
            ));
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(usage(label));
    }

    private Component usage(String label) {
        return Component.text(
                "Usage: /" + label + " <reload|setjoin|setleave|resetjoin|resetleave>",
                NamedTextColor.YELLOW
        );
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            return List.of("reload", "setjoin", "setleave", "resetjoin", "resetleave");
        }
        return List.of();
    }
}

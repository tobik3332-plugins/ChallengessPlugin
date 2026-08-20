package cz.craft.challengess;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChallengessCommand implements CommandExecutor {

    private final ChallengessPlugin plugin;

    public ChallengessCommand(ChallengessPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("challengess.admin")) {
                String noPerm = plugin.getConfig().getString("messages.no-permission");
                sender.sendMessage(ColorUtils.parse(plugin.getConfig().getString("messages.prefix") + noPerm));
                return true;
            }

            plugin.reloadConfig();
            String reloaded = plugin.getConfig().getString("messages.reload-success");
            sender.sendMessage(ColorUtils.parse(plugin.getConfig().getString("messages.prefix") + reloaded));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento příkaz mohou používat pouze hráči ve hře!");
            return true;
        }

        if (!player.hasPermission("challengess.use")) {
            String noPerm = plugin.getConfig().getString("messages.no-permission");
            player.sendMessage(ColorUtils.parse(plugin.getConfig().getString("messages.prefix") + noPerm));
            return true;
        }

        plugin.getGuiManager().openGUI1(player);
        return true;
    }
}

package cz.craft.challengess;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final ChallengessPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public GUIListener(ChallengessPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack current = event.getCurrentItem();
        if (current == null || !current.hasItemMeta()) return;

        ItemMeta meta = current.getItemMeta();
        if (!meta.getPersistentDataContainer().has(plugin.getActionKey(), PersistentDataType.STRING)) return;

        // Zabránění vytažení itemu z GUI
        event.setCancelled(true);

        String action = meta.getPersistentDataContainer().get(plugin.getActionKey(), PersistentDataType.STRING);
        if (action == null || action.equals("filler")) return;

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        switch (action) {
            case "request_approval" -> handleRequestApproval(player);
            case "open_gui_1" -> plugin.getGuiManager().openGUI1(player);
            case "open_gui_2" -> plugin.getGuiManager().openGUI2(player);
            case "open_gui_3" -> plugin.getGuiManager().openGUI3(player);
            case "execute_command" -> {
                String extraData = meta.getPersistentDataContainer().get(plugin.getExtraDataKey(), PersistentDataType.STRING);
                if (extraData != null) {
                    executeCustomCommands(player, extraData);
                }
            }
        }
    }

    private void handleRequestApproval(Player player) {
        int cooldownSec = plugin.getConfig().getInt("discord.cooldown-seconds", 60);
        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(player.getUniqueId())) {
            long lastUse = cooldowns.get(player.getUniqueId());
            long timeLeft = (lastUse + (cooldownSec * 1000L) - now) / 1000;

            if (timeLeft > 0) {
                String msg = plugin.getConfig().getString("messages.request-cooldown", "<red>Musíš počkat {time}s!")
                        .replace("{time}", String.valueOf(timeLeft));
                sendMessage(player, msg);
                return;
            }
        }

        String webhookUrl = plugin.getConfig().getString("discord.webhook-url", "");
        if (webhookUrl.isEmpty() || webhookUrl.contains("YOUR_WEBHOOK_URL_HERE")) {
            sendMessage(player, plugin.getConfig().getString("messages.webhook-error"));
            return;
        }

        String discordMsg = plugin.getConfig().getString("discord.message", "@everyone Hráč **{player}** zažádal o schválení!")
                .replace("{player}", player.getName());

        DiscordWebhook.sendWebhook(webhookUrl, discordMsg);
        cooldowns.put(player.getUniqueId(), now);

        sendMessage(player, plugin.getConfig().getString("messages.request-sent"));
    }

    private void executeCustomCommands(Player player, String configPath) {
        List<String> commands = plugin.getConfig().getStringList(configPath + ".commands");
        for (String cmd : commands) {
            String formatted = cmd.replace("{player}", player.getName());
            if (formatted.startsWith("[CONSOLE] ")) {
                String c = formatted.replace("[CONSOLE] ", "");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
            } else if (formatted.startsWith("[PLAYER] ")) {
                String c = formatted.replace("[PLAYER] ", "");
                player.performCommand(c);
            } else {
                player.performCommand(formatted);
            }
        }
    }

    private void sendMessage(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage(ColorUtils.parse(prefix + message));
    }
}

package cz.craft.challengess;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {

    private final ChallengessPlugin plugin;

    public GUIManager(ChallengessPlugin plugin) {
        this.plugin = plugin;
    }

    public void openGUI1(Player player) {
        FileConfiguration config = plugin.getConfig();
        String title = config.getString("gui1.title", "Main Menu");
        int rows = config.getInt("gui1.rows", 3);
        Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtils.parse(title));

        fillEmptySlots(inv, "gui1");

        // Request Approval Item
        createItem(inv, config.getConfigurationSection("gui1.items.request-approval"), "request_approval", null);

        // Challenges Item
        createItem(inv, config.getConfigurationSection("gui1.items.challenges"), "open_gui_2", null);

        player.openInventory(inv);
    }

    public void openGUI2(Player player) {
        FileConfiguration config = plugin.getConfig();
        String title = config.getString("gui2.title", "Challenges Menu");
        int rows = config.getInt("gui2.rows", 4);
        Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtils.parse(title));

        fillEmptySlots(inv, "gui2");

        // Back Button
        createItem(inv, config.getConfigurationSection("gui2.back-button"), "open_gui_1", null);

        // Advancements Button
        createItem(inv, config.getConfigurationSection("gui2.advancements"), "open_gui_3", null);

        // Custom Items
        ConfigurationSection customSection = config.getConfigurationSection("gui2.custom-items");
        if (customSection != null) {
            for (String key : customSection.getKeys(false)) {
                createItem(inv, customSection.getConfigurationSection(key), "execute_command", "gui2.custom-items." + key);
            }
        }

        player.openInventory(inv);
    }

    public void openGUI3(Player player) {
        FileConfiguration config = plugin.getConfig();
        String title = config.getString("gui3.title", "Advancements Menu");
        int rows = config.getInt("gui3.rows", 4);
        Inventory inv = Bukkit.createInventory(player, rows * 9, ColorUtils.parse(title));

        fillEmptySlots(inv, "gui3");

        // Back Button
        createItem(inv, config.getConfigurationSection("gui3.back-button"), "open_gui_2", null);

        // Custom Advancements Items (povoleno spouštění příkazů stejně jako v GUI 2)
        ConfigurationSection customSection = config.getConfigurationSection("gui3.custom-items");
        if (customSection != null) {
            for (String key : customSection.getKeys(false)) {
                createItem(inv, customSection.getConfigurationSection(key), "execute_command", "gui3.custom-items." + key);
            }
        }

        player.openInventory(inv);
    }

    private void fillEmptySlots(Inventory inv, String guiKey) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean(guiKey + ".fill-empty", false)) return;

        String matName = config.getString(guiKey + ".filler-item", "GRAY_STAINED_GLASS_PANE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.GRAY_STAINED_GLASS_PANE;

        ItemStack filler = new ItemStack(mat);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            meta.getPersistentDataContainer().set(plugin.getActionKey(), PersistentDataType.STRING, "filler");
            filler.setItemMeta(meta);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }
    }

    private void createItem(Inventory inv, ConfigurationSection section, String action, String extraData) {
        if (section == null) return;

        int slot = section.getInt("slot", 0);
        String matName = section.getString("material", "STONE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.STONE;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = section.getString("name", "");
            meta.displayName(ColorUtils.parse(name));

            List<String> lore = section.getStringList("lore");
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(ColorUtils.parse(line));
            }
            meta.lore(loreComponents);

            // Uložení akce do PersistentDataContainer
            meta.getPersistentDataContainer().set(plugin.getActionKey(), PersistentDataType.STRING, action);
            if (extraData != null) {
                meta.getPersistentDataContainer().set(plugin.getExtraDataKey(), PersistentDataType.STRING, extraData);
            }

            item.setItemMeta(meta);
        }

        if (slot >= 0 && slot < inv.getSize()) {
            inv.setItem(slot, item);
        }
    }
}

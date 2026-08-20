package cz.craft.challengess;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class ChallengessPlugin extends JavaPlugin {

    private GUIManager guiManager;
    private NamespacedKey actionKey;
    private NamespacedKey extraDataKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.actionKey = new NamespacedKey(this, "action");
        this.extraDataKey = new NamespacedKey(this, "extra_data");
        this.guiManager = new GUIManager(this);

        // Přímá registrace příkazu do Paper CommandMap
        Bukkit.getCommandMap().register("challengess", new ChallengessCommand(this));

        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getLogger().info("Challengess Plugin byl uspesne nacten pro Paper!");
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public NamespacedKey getActionKey() {
        return actionKey;
    }

    public NamespacedKey getExtraDataKey() {
        return extraDataKey;
    }
}

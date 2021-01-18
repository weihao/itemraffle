package org.akadia.itemraffle;

import net.milkbowl.vault.economy.Economy;
import org.akadia.itemraffle.configs.BoxConfiguration;
import org.akadia.itemraffle.configs.DepositoryConfiguration;
import org.akadia.itemraffle.configs.LanguageConfiguration;
import org.akadia.itemraffle.configs.PluginConfiguration;
import org.akadia.itemraffle.managers.CommandManager;
import org.akadia.itemraffle.managers.ConfigFileManager;
import org.akadia.itemraffle.managers.GUIManager;
import org.akadia.itemraffle.managers.ItemRaffleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ItemRaffleMain extends JavaPlugin {

    public static ItemRaffleMain itemRaffleMain;
    private ItemRaffleManager itemRaffleManager;
    private ConfigFileManager configFileManager;

    private PluginConfiguration pluginConfiguration;


    private LanguageConfiguration languageConfiguration;
    private DepositoryConfiguration depositoryConfiguration;
    private BoxConfiguration boxConfiguration;

    private CommandManager commandManager;
    private GUIManager guiManager;


    private Economy economy;

    public static ItemRaffleMain getItemRaffleMain() {
        return itemRaffleMain;
    }

    public Economy getEconomy() {
        return economy;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public ItemRaffleManager getItemRaffleManager() {
        return itemRaffleManager;
    }

    public ConfigFileManager getConfigFileManager() {
        return configFileManager;
    }

    public DepositoryConfiguration getDepositoryConfiguration() {
        return depositoryConfiguration;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public BoxConfiguration getBoxManager() {
        return boxConfiguration;
    }

    public PluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

    public BoxConfiguration getBoxConfiguration() {
        return boxConfiguration;
    }

    public LanguageConfiguration getLanguageConfiguration() {
        return languageConfiguration;
    }

    @Override
    public void onEnable() {
        itemRaffleMain = this;
        getLogger().info("Template plugin is loaded.");

        // Config must be initialized first.
        this.configFileManager = new ConfigFileManager(this);
        this.pluginConfiguration = new PluginConfiguration(this);
        this.depositoryConfiguration = new DepositoryConfiguration(this);
        this.boxConfiguration = new BoxConfiguration(this);

        this.itemRaffleManager = new ItemRaffleManager(this);

        this.commandManager = new CommandManager(this);
        this.guiManager = new GUIManager(this);


        PluginManager pm = this.getServer().getPluginManager();

        if (!setupEconomy()) {
            this.getLogger().log(Level.SEVERE, " 前置 Vault 经济插件加载失败.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }


    private boolean setupEconomy() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (plugin != null && plugin.isEnabled()) {
            RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
            return service != null && (this.economy = service.getProvider()) != null;
        }
        return false;
    }

    @Override
    public void onDisable() {
        depositoryConfiguration.onDisable();
        boxConfiguration.onDisable();
    }

    public String getLocale(String... nodes){
        return this.getLanguageConfiguration().getLocales().get(String.join(".", nodes));
    }

}
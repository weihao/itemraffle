package org.akadia.itemraffle.configs;

import org.akadia.itemraffle.ItemRaffleMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public abstract class Configuration {
    private final ItemRaffleMain main;
    private final FileConfiguration config;

    public Configuration(ItemRaffleMain main) {
        this.main = main;
        this.config = main.getConfigFileManager().create(getConfigName());
    }

    public ItemRaffleMain getMain() {
        return main;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public abstract String getConfigName();

    public String getString(String... nodes) {
        return this.getConfig().getString(String.join(".", nodes));
    }

    public int getInt(String... nodes) {
        return Integer.parseInt(this.getConfig().getString(String.join(".", nodes)));
    }

    public long getLong(String... nodes) {
        return Long.parseLong(this.getConfig().getString(String.join(".", nodes)));
    }

    public List<String> getStringList(String... nodes) {
        return this.getConfig().getStringList(String.join(".", nodes));
    }

    public Set<String> getConfigurationSection(String... nodes) {
        return this.getConfig().getConfigurationSection(String.join(".", nodes)).getKeys(false);
    }

    public void setValue(@Nullable Object value, String... nodes) {
        this.getConfig().set(String.join(".", nodes), value);
    }

    public void writeConfigFile() {
        this.getMain().getConfigFileManager().save(this.getConfigName());
    }
}

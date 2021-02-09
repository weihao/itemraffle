package org.akadia.itemraffle.managers;

import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigFileManager {
    Map<String, FileConfiguration> configs;
    Map<String, File> files;
    Logger logger;
    private final Plugin plugin;

    public ConfigFileManager(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.configs = new HashMap<>();
        this.files = new HashMap<>();

        if (!plugin.getDataFolder().exists()) {
            boolean mkdir = plugin.getDataFolder().mkdir();
            if (!mkdir) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED
                        + "Failed to create data folder!");
            }
        }
    }

    public FileConfiguration create(String configName) {
        File configFile = new File(plugin.getDataFolder(), configName);
        this.files.put(configName, configFile);
        FileConfiguration config = null;

        if (!configFile.exists()) {
            this.plugin.saveResource(configName, true);
            try {
                config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(configFile), Charsets.UTF_8));
                this.configs.put(configName, config);
            } catch (FileNotFoundException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED
                        + "Could not locate file " + configFile.getName() + ".yml!");
            }
        } else {
            // Generating missing configuration.
            config = YamlConfiguration.loadConfiguration(configFile);
            this.configs.put(configName, config);
            try {
                generate(configName);
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED
                        + "Could not add more configurations to " + config + ".yml!");
            }
        }
        return config;
    }


    private void backup(String configName) {
        File configFile = files.get(configName);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String timestamp = formatter.format(date);
        File dataDirectory = plugin.getDataFolder();
        File playerDataDirectory = new File(dataDirectory, "config_backup");

        if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
            return;
        }

        FileUtil.copy(configFile, new File(playerDataDirectory, timestamp + "_" + configName));
        this.logger.info("Backed up " + configName + ".");
    }

    public void generate(String configName) throws IOException {
        FileConfiguration config = configs.get(configName);
        int settings = 0;
        int addedSettings = 0;

        InputStream defConfigStream = plugin.getResource(configName);

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            for (String string : defConfig.getKeys(true)) {
                if (!config.contains(string)) {
                    config.set(string, defConfig.get(string));
                    addedSettings++;
                }
                if (!config.isConfigurationSection(string))
                    settings++;
            }
        }
        if (addedSettings > 0) {
            backup(configName);
        }


        this.logger.info("Found " + settings + " settings in " + configName);
        this.logger.info("Added " + addedSettings + " new settings for " + configName);
        if (addedSettings > 0) {
            this.save(configName);
        }
    }

    public void save(String configName) {
        File configFile = files.get(configName);
        FileConfiguration config = configs.get(configName);
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED
                    + "Could not save " + configName + "!");
        }
    }
}

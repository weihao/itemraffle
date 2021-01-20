package org.akadia.itemraffle.configs;

import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.enums.DepositoryMode;
import org.akadia.itemraffle.enums.DepositorySelection;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LanguageConfiguration extends Configuration {

    private Map<String, String> locales;

    public LanguageConfiguration(ItemRaffleMain main) {
        super(main);

        locales = new HashMap<>();

        Set<String> msg = this.getConfig().getConfigurationSection("").getKeys(true);
        for (String key : msg) {
            String value = this.getConfig().getString(key);
            if (value == null) {
                continue;
            }
            locales.put(key, toColor(value));
        }

        for (DepositoryMode enumName : DepositoryMode.values()) {
            String key = "enums." + enumName.toString().toLowerCase();
            String value = this.getConfig().getString(key);
            if (value == null) {
                continue;
            }
            locales.put(key, toColor(value));
        }

        for (DepositorySelection enumName : DepositorySelection.values()) {
            String key = "enums." + enumName.toString().toLowerCase();
            String value = this.getConfig().getString(key);
            if (value == null) {
                continue;
            }
            locales.put(key, toColor(value));
        }

    }

    public Map<String, String> getLocales() {
        return locales;
    }

    @Override
    public String getConfigName() {
        return "lang_" + this.getMain().getPluginConfiguration().getLang() + ".yml";
    }

    @Override
    public void onDisable() {

    }

    public String toColor(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }
}

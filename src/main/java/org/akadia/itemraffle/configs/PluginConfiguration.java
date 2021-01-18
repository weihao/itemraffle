package org.akadia.itemraffle.configs;

import org.akadia.itemraffle.ItemRaffleMain;

public class PluginConfiguration extends Configuration {
    private String lang;

    public PluginConfiguration(ItemRaffleMain main) {
        super(main);
        lang = this.getString("lang");

    }

    public String getLang() {
        return lang;
    }

    @Override
    public String getConfigName() {
        return "config.yml";
    }

    @Override
    public void onDisable() {
    }

}

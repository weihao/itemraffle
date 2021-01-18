package org.akadia.itemraffle.configs;

import org.akadia.itemraffle.ItemRaffleMain;

import java.util.HashMap;
import java.util.Map;

public class LanguageConfiguration extends Configuration {

    private Map<String, String> locales;

    public Map<String, String> getLocales() {
        return locales;
    }

    public LanguageConfiguration(ItemRaffleMain main) {
        super(main);

        locales = new HashMap<>();

    }

    @Override
    public String getConfigName() {
        return this.getMain().getPluginConfiguration().getLang() + ".yml";
    }

    @Override
    public void onDisable() {

    }

}

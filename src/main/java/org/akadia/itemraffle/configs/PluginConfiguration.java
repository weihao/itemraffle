package org.akadia.itemraffle.configs;

import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.utils.ItemStackUtil;
import org.akadia.itemraffle.utils.SerializeUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginConfiguration extends Configuration {
    public String getLang() {
        return lang;
    }

    private String lang;

    public PluginConfiguration(ItemRaffleMain main) {
        super(main);
        lang = this.getString("lang");

    }

    @Override
    public String getConfigName() {
        return "config.yml";
    }

    @Override
    public void onDisable() {
    }

}

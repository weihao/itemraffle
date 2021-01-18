package org.akadia.itemraffle.utils;

import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.bukkit.entity.HumanEntity;

import java.text.MessageFormat;

public class LangUtil {
    public static String getBoxMenuName(ItemRaffleMain main, HumanEntity player) {
        String title = main.getLocale("gui", "boxMenuTitle");
        return MessageFormat.format(title, player.getName());
    }

    public static String getHistoryMenuName(ItemRaffleMain main, ItemRafflePool pool) {
        String title = main.getLocale("gui", "historyMenuTitle");
        return MessageFormat.format(title, pool.getItemRaffleDepository().getName());
    }

    public static String getDepositoryMenuName(ItemRaffleMain main, ItemRafflePool pool) {
        String title = main.getLocale("gui", "depositoryMenuTitle");
        return MessageFormat.format(title, pool.getItemRaffleDepository().getName(), pool.getRaffleId());
    }

    public static String getPoolMenuName(ItemRaffleMain main, ItemRafflePool pool) {
        String title = main.getLocale("gui", "poolMenuTitle");
        return MessageFormat.format(title, pool.getItemRaffleDepository().getName(), pool.getRaffleId());
    }
}

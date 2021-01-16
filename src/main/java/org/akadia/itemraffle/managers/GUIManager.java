package org.akadia.itemraffle.managers;

import de.themoep.inventorygui.InventoryGui;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.gui.DepositoryListCommonMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class GUIManager extends BukkitRunnable {
    private final ItemRaffleMain main;
    private final DepositoryListCommonMenu itemDepositoryCommonMenu;


    public GUIManager(ItemRaffleMain main) {
        this.main = main;
        this.itemDepositoryCommonMenu = new DepositoryListCommonMenu(main);

        this.runTaskTimerAsynchronously(main, 0L, 20L); // 开始任务
    }

    public DepositoryListCommonMenu getDepositoryListCommonMenu() {
        return itemDepositoryCommonMenu;
    }

    @Override
    public void run() {
        for (ItemRafflePool pool : main.getItemRaffleManager().getPools()) {
            pool.run();
        }

        itemDepositoryCommonMenu.getGui().draw();

        HashSet<InventoryGui> set = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            InventoryGui open = InventoryGui.getOpen(onlinePlayer);
            if (open != null) {
                if (set.contains(open)) {
                    continue;
                }
                set.add(open);
                open.draw();
                System.out.println(onlinePlayer.getName() + " drawed");
            }
        }
    }
}

package org.akadia.itemraffle.managers;

import co.aikar.commands.BukkitCommandManager;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.commands.ItemRaffleCommand;

public class CommandManager {
    public CommandManager(ItemRaffleMain main) {
        BukkitCommandManager manager = new BukkitCommandManager(main);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new ItemRaffleCommand(main));
    }
}

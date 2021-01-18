package org.akadia.itemraffle.guis;

import de.themoep.inventorygui.InventoryGui;
import org.akadia.itemraffle.ItemRaffleMain;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCommonMenu implements InventoryHolder {
    private final ItemRaffleMain main;
    private final InventoryGui gui;

    BaseCommonMenu(ItemRaffleMain main, String name) {
        this.main = main;
        this.gui = new InventoryGui(main, this, name, getSetup());
    }

    public ItemRaffleMain getMain() {
        return main;
    }

    public InventoryGui getGui() {
        return gui;
    }

    public void open(HumanEntity player) {
        gui.show(player);
    }

    abstract String[] getSetup();

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}

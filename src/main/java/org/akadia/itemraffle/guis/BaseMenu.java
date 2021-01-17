package org.akadia.itemraffle.guis;

import de.themoep.inventorygui.InventoryGui;
import org.akadia.itemraffle.ItemRaffleMain;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public abstract class BaseMenu {
    private final ItemRaffleMain main;
    private final InventoryGui gui;
    private final InventoryHolder holder;

    BaseMenu(ItemRaffleMain main, InventoryHolder holder, String name) {
        this.main = main;

        this.gui = new InventoryGui(main, holder, name, getSetup());
        this.holder = holder;
    }

    BaseMenu(ItemRaffleMain main, InventoryHolder holder) {
        this.main = main;

        this.gui = new InventoryGui(main, holder, getName(), getSetup());
        this.holder = holder;
    }

    public ItemRaffleMain getMain() {
        return main;
    }

    public InventoryGui getGui() {
        return gui;
    }

    public InventoryHolder getHolder() {
        return holder;
    }

    public void open(HumanEntity player) {
        this.gui.show(player);
    }

    abstract String[] getSetup();

    abstract String getName();
}

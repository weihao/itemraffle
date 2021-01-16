package org.akadia.itemraffle.gui;

import de.themoep.inventorygui.GuiStorageElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.akadia.itemraffle.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DepositoryViewerCommonMenu extends BaseCommonMenu {

    private final ItemRaffleDepository itemRaffleDepository;

    public DepositoryViewerCommonMenu(ItemRaffleMain main, ItemRaffleDepository depository) {
        super(main, depository.getName());
        this.itemRaffleDepository = depository;
// With a virtual inventory to access items later on
        Inventory inv = Bukkit.createInventory(null, 54, depository.getName());
        for (ItemStack prize : depository.getPrizes()) {
            if (prize != null) {
                inv.addItem(prize);
            }
        }
        this.getGui().addElement(new GuiStorageElement('i', inv));
        this.getGui().setCloseAction(close -> {
            depository.setPrizes(InventoryUtil.sort(inv));
            this.getMain().getDepositoryConfiguration().saveDepository(depository);
            return true; // Don't go back to the previous GUI (true would automatically go back to the previously opened one)
        });
    }

    @Override
    String[] getSetup() {
        return new String[]{
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii"
        };
    }

    @Override
    String getName() {
        return this.itemRaffleDepository.getName() + ": " +
                "item raffle is using item from slot " + this.itemRaffleDepository.getItemSelectIndex();
    }


}

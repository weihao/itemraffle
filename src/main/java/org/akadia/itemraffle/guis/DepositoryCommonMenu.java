package org.akadia.itemraffle.guis;

import de.themoep.inventorygui.GuiStorageElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.akadia.itemraffle.enums.PoolState;
import org.akadia.itemraffle.utils.InventoryUtil;
import org.akadia.itemraffle.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DepositoryCommonMenu extends BaseCommonMenu {


    public DepositoryCommonMenu(ItemRaffleMain main, ItemRafflePool itemRafflePool) {
        super(main, main.getLocale("gui.depositoryMenuTitle", itemRafflePool.getItemRaffleDepository().getName(), itemRafflePool.getRaffleId()));

        ItemRaffleDepository depository = itemRafflePool.getItemRaffleDepository();

        Inventory inv = Bukkit.createInventory(null, 54, depository.getName());
        for (ItemStack prize : depository.getPrizes()) {
            if (prize != null) {
                inv.addItem(prize);
            }
        }
        // show depository prizes
        this.getGui().addElement(new GuiStorageElement('i', inv));

        this.getGui().setCloseAction(close -> {
            // save prizes on close
            ItemStack[] sort = InventoryUtil.sort(inv);
            depository.setPrizes(ItemStackUtil.arrayToList(sort.clone()));
            this.getMain().getDepositoryConfiguration().saveDepository(depository);
            itemRafflePool.setState(PoolState.RUNNING);
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
}

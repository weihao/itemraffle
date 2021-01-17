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

public class DepositoryViewerCommonMenu extends BaseCommonMenu {

    private final ItemRaffleDepository itemRaffleDepository;
    private final ItemRafflePool itemRafflePool;
    public DepositoryViewerCommonMenu(ItemRaffleMain main, ItemRaffleDepository depository, ItemRafflePool itemRafflePool) {
        super(main, depository.getName());
        this.itemRaffleDepository = depository;
        this.itemRafflePool = itemRafflePool;

        Inventory inv = Bukkit.createInventory(null, 54, depository.getName());
        for (ItemStack prize : depository.getPrizes()) {
            if (prize != null) {
                inv.addItem(prize);
            }
        }
        this.getGui().addElement(new GuiStorageElement('i', inv));

        this.getGui().setCloseAction(close -> {
            ItemStack[] sort = InventoryUtil.sort(inv);
            depository.setPrizes(ItemStackUtil.arrayToList(sort.clone()));
            this.getMain().getDepositoryConfiguration().saveDepository(depository);
            this.itemRafflePool.setState(PoolState.RUNNING);
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

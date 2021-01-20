package org.akadia.itemraffle.guis;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.StaticGuiElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DepositoryViewerCommonMenu extends BaseCommonMenu {

    public DepositoryViewerCommonMenu(ItemRaffleMain main, ItemRafflePool itemRafflePool) {
        super(main, main.getLocale("gui.depositoryViewerMenuTitle", itemRafflePool.getItemRaffleDepository().getName(), itemRafflePool.getRaffleId()));
        ItemRaffleDepository depository = itemRafflePool.getItemRaffleDepository();

        // show depository prizes
        this.getGui().addElement(new DynamicGuiElement('i', viewer -> {
            GuiElementGroup group = new GuiElementGroup('i');
            List<ItemStack> prizes = depository.getPrizes();
            for (ItemStack prize : prizes) {
                group.addElement(new StaticGuiElement('i', prize));
            }
            return group;
        }));

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
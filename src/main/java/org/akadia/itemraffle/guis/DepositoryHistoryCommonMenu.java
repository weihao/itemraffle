package org.akadia.itemraffle.guis;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleWinnerInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DepositoryHistoryCommonMenu extends BaseCommonMenu {

    public DepositoryHistoryCommonMenu(ItemRaffleMain main, ItemRafflePool pool) {
        super(main, main.getLocale("gui.historyMenuTitle", pool.getItemRaffleDepository().getName()));

        // show past winners
        this.getGui().addElement(new DynamicGuiElement('i', viewer -> {
            GuiElementGroup pastWinnersGroup = new GuiElementGroup('i');
            List<ItemRaffleWinnerInfo> history = pool.getItemRaffleDepository().getHistory();
            for (ItemRaffleWinnerInfo winnerInfo : history) {
                pastWinnersGroup.addElement(
                        new StaticGuiElement('i',
                                new ItemStack(XMaterial.matchXMaterial("PAPER").get().parseItem()),
                                winnerInfo.getUsername(),
                                winnerInfo.getChance(),
                                winnerInfo.getPlayerDepositValue()));
            }
            return pastWinnersGroup;
        }));

        this.getGui().addElement(new GuiPageElement('p', new ItemStack(XMaterial.matchXMaterial("OAK_SIGN").get().parseItem()), GuiPageElement.PageAction.PREVIOUS, this.getMain().getLocale("gui.prevPage")));
        this.getGui().addElement(new GuiPageElement('n', new ItemStack(XMaterial.matchXMaterial("OAK_SIGN").get().parseItem()), GuiPageElement.PageAction.NEXT, this.getMain().getLocale("gui.nextPage")));

    }


    @Override
    String[] getSetup() {
        return new String[]{
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "p       n",
        };
    }
}

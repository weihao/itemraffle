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
        super(main);

        GuiElementGroup group = new GuiElementGroup('i');

        List<ItemRaffleWinnerInfo> history = pool.getItemRaffleDepository().getHistory();
        for (ItemRaffleWinnerInfo winnerInfo : history) {
            DynamicGuiElement dynamicGuiElement = new DynamicGuiElement('i', (viewer)
                    -> new StaticGuiElement('i', new ItemStack(Material.PAPER), winnerInfo.getUsername(), winnerInfo.getChance(), winnerInfo.getPlayerDepositValue()));
            group.addElement(dynamicGuiElement);
        }

        this.getGui().addElement(group);

        this.getGui().addElement(new GuiPageElement('p', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
        this.getGui().addElement(new GuiPageElement('n', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

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

    @Override
    String getName() {
        return "History";
    }


}

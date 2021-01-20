package org.akadia.itemraffle.guis;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.configs.BoxConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoxViewerMenu extends BaseMenu {


    public BoxViewerMenu(ItemRaffleMain main, HumanEntity player) {
        super(main, player, main.getLocale("gui.boxMenuTitle", player.getName()));

        BoxConfiguration boxManager = this.getMain().getBoxManager();
        List<ItemStack> itemStacks = this.getMain().getBoxManager().getBoxes().getOrDefault(player.getName(), new ArrayList<>());


        GuiElementGroup itemGroup = new GuiElementGroup('i');

        for (int i = 0; i < itemStacks.size(); i++) {
            StaticGuiElement s = new StaticGuiElement('i', itemStacks.get(i), click -> {
                itemGroup.clearElements();
                boxManager.claimAll((Player) player);
                return true;
            });
            itemGroup.addElement(s);
        }
        // display item stacks in the inventory
        this.getGui().addElement(itemGroup);

        this.getGui().addElement(new GuiPageElement('p', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.PREVIOUS, this.getMain().getLocale("gui.prevPage")));
        this.getGui().addElement(new GuiPageElement('n', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.NEXT, this.getMain().getLocale("gui.nextPage")));

    }

    @Override
    String[] getSetup() {
        return new String[]{
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "p       n"
        };
    }
}

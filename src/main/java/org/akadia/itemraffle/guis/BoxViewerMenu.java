package org.akadia.itemraffle.guis;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.configs.BoxConfiguration;
import org.akadia.itemraffle.utils.LangUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoxViewerMenu extends BaseMenu {


    public BoxViewerMenu(ItemRaffleMain main, HumanEntity player) {
        super(main, player, LangUtil.getBoxMenuName(main, player));

        BoxConfiguration boxManager = this.getMain().getBoxManager();
        List<ItemStack> itemStacks = this.getMain().getBoxManager().getBoxes().getOrDefault(player.getName(), new ArrayList<>());


        GuiElementGroup group = new GuiElementGroup('i');

        for (int i = 0; i < itemStacks.size(); i++) {
            StaticGuiElement s = new StaticGuiElement('i', itemStacks.get(i), click -> {
                group.clearElements();
                boxManager.claimAll((Player) player);
                return true;
            });
            group.addElement(s);
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
                "p       n"
        };
    }
}

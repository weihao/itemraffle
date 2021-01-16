package org.akadia.itemraffle.gui;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DepositoryListCommonMenu extends BaseCommonMenu {

    public DepositoryListCommonMenu(ItemRaffleMain main) {
        super(main);

        GuiElementGroup group = new GuiElementGroup('i');

        ArrayList<ItemRafflePool> pools = this.getMain().getItemRaffleManager().getPools();

        for (ItemRafflePool pool : pools) {
            ItemRaffleDepository depository = pool.getItemRaffleDepository();
            DynamicGuiElement dynamicGuiElement = new DynamicGuiElement('i',
                    (viewer) -> {
                        if (pool.validateDepository()) {
                            return new StaticGuiElement('i', depository.getIcon(), 1, click -> {
                                if (click.getType().isRightClick() && click.getEvent().getWhoClicked().hasPermission("itemraffle.admin")) {
                                    pool.getDepositoryViewerCommonMenu().open(viewer);
                                    return true;
                                }

                                PoolViewerMenu poolViewerMenu = new PoolViewerMenu(main, viewer, pool);
                                pool.getPoolViewerMenus().put(viewer.getName(), poolViewerMenu);
                                poolViewerMenu.open(viewer);
                                return true;
                            }, depository.getName(),
                                    depository.getDepositoryMode() + "",
                                    depository.getDepositorySelection() + "",
                                    depository.getDrawingInterval() + "",
                                    depository.getNextDrawingTime() + "",
                                    pool.getRemainingNextDrawTime() + "");
                        } else {
                            return new StaticGuiElement('i', depository.getIcon(), 1, click -> {
                                if (click.getEvent().getWhoClicked().hasPermission("itemraffle.admin")) {
                                    pool.getDepositoryViewerCommonMenu().open(viewer);
                                }
                                return true;
                            }, "DISABLED", "DISABLED");
                        }
                    });

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
        return "Example";
    }


}

package org.akadia.itemraffle.guis;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.akadia.itemraffle.enums.PoolState;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DepositoryListCommonMenu extends BaseCommonMenu {

    public DepositoryListCommonMenu(ItemRaffleMain main) {
        super(main, main.getLocale("gui.listMenuTitle"));

        GuiElementGroup group = new GuiElementGroup('i');

        List<ItemRafflePool> pools = this.getMain().getItemRaffleManager().getPools();

        for (ItemRafflePool pool : pools) {
            ItemRaffleDepository depository = pool.getItemRaffleDepository();
            DynamicGuiElement dynamicGuiElement = new DynamicGuiElement('i',
                    (viewer) -> {
                        return new StaticGuiElement('i', depository.getIcon(), 1, click -> {
                            if (click.getType().isRightClick()) {
                                if (click.getEvent().getWhoClicked().hasPermission("itemraffle.admin")) {
                                    pool.setState(PoolState.BLOCKED);
                                    pool.getDepositoryCommonMenu().open(viewer);
                                } else {
                                    pool.getDepositoryViewerCommonMenu().open(viewer);
                                }
                                return true;
                            }
                            if (pool.validateDepository()) {
                                PoolViewerMenu poolViewerMenu = new PoolViewerMenu(main, viewer, pool);
                                pool.getPoolViewerMenus().put(viewer.getName(), poolViewerMenu);
                                poolViewerMenu.open(viewer);
                            }

                            return true;
                        },
                                this.getMain().getLocale("gui.depositoryName", depository.getName()),
                                this.getMain().getLocale("gui.depositoryMode", depository.getDepositoryMode()),
                                this.getMain().getLocale("gui.depositorySelection", depository.getDepositorySelection()),
                                this.getMain().getLocale("gui.drawingInterval", depository.getDrawingInterval()),
                                this.getMain().getLocale("gui.nextDrawingTime", depository.getNextDrawingTime()),
                                this.getMain().getLocale("gui.remainingNextDrawTime", pool.getRemainingNextDrawTime())
                        );
                    });

            group.addElement(dynamicGuiElement);
        }
        this.getGui().addElement(group);

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
                "p       n",
        };
    }


}

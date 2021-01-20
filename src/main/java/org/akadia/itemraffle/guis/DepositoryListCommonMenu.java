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
import org.akadia.itemraffle.utils.CalendarUitl;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class DepositoryListCommonMenu extends BaseCommonMenu {

    public DepositoryListCommonMenu(ItemRaffleMain main) {
        super(main, main.getLocale("gui.listMenuTitle"));


        // show depository
        this.getGui().addElement(new DynamicGuiElement('i', viewer -> {
            GuiElementGroup depositoryGroup = new GuiElementGroup('i');
            List<ItemRafflePool> pools = this.getMain().getItemRaffleManager().getPools();
            pools.sort(Comparator.comparingInt(a -> a.getItemRaffleDepository().getDrawingInterval()));
            for (ItemRafflePool pool : pools) {
                ItemRaffleDepository depository = pool.getItemRaffleDepository();
                depositoryGroup.addElement(
                        new StaticGuiElement('i', depository.getIcon(), 1, click -> {
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
                                this.getMain().getLocale("gui.depositoryMode", main.getEnumString(depository.getDepositoryMode().toString())),
                                this.getMain().getLocale("gui.depositorySelection", main.getEnumString(depository.getDepositorySelection().toString())),
                                this.getMain().getLocale("gui.itemSelectIndex", depository.getItemSelectIndex()),
                                this.getMain().getLocale("gui.drawingInterval", CalendarUitl.formatSeconds(depository.getDrawingInterval())),
                                this.getMain().getLocale("gui.nextDrawingTime", CalendarUitl.formatMillis(depository.getNextDrawingTime())),
                                this.getMain().getLocale("gui.remainingNextDrawTime", CalendarUitl.formatSeconds(pool.getRemainingNextDrawTime())),
                                this.getMain().getLocale("gui.totalEntry", pool.getPlayerCount()),
                                this.getMain().getLocale("gui.totalValue", pool.getTotalPoolDeposit().toPlainString())
                        ));
            }
            return depositoryGroup;
        }));

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

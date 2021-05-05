package org.akadia.itemraffle.guis;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.StaticGuiElement;
import net.wesjd.anvilgui.AnvilGUI;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PoolViewerMenu extends BaseMenu {

    public PoolViewerMenu(ItemRaffleMain main, HumanEntity player, ItemRafflePool pool) {
        super(main, player,
                main.getLocale("gui.poolMenuTitle",
                        pool.getItemRaffleDepository().getName(),
                        pool.getRaffleId()));


        ItemRaffleDepository depository = pool.getItemRaffleDepository();
        if (!pool.validateDepository()) {
            this.getGui().close();
            return;
        }

        this.getGui().addElement(new DynamicGuiElement('i', (viewer) -> {
            if (!pool.validateDepository()) {
                return new StaticGuiElement('i', new ItemStack(Material.AIR));
            }
            return new StaticGuiElement('i', pool.getSelectedItemStack());

        }));

        this.getGui().addElement(new StaticGuiElement('h', new ItemStack(XMaterial.matchXMaterial("BOOK").get().parseItem()), 1, click -> {
            pool.getDepositoryHistoryCommonMenu().open(player);
            return true;
        }, this.getMain().getLocale("gui.viewHistoryButton"),
                this.getMain().getLocale("gui.leftClick", this.getMain().getLocale("gui.viewHistoryButton"))));

        this.getGui().addElement(new StaticGuiElement('d', new ItemStack(XMaterial.matchXMaterial("OAK_SIGN").get().parseItem()), 1, click -> {
            click.getGui().close();
            new AnvilGUI
                    .Builder()
                    .onComplete((pl, text) -> {
                        try {
                            double v = Double.parseDouble(text);
                            if (v <= 0) {
                                return AnvilGUI.Response.text(this.getMain().getLocale("gui.errorNegativeOrZero"));
                            }
                            if (!pool.playerDeposit(pl, text)) {
                                return AnvilGUI.Response.text(this.getMain().getLocale("gui.errorNotEnoughMoney"));
                            }

                            this.getMain().getDepositoryConfiguration().saveDepository(depository);

                            return AnvilGUI.Response.close();
                        } catch (NumberFormatException e) {
                            return AnvilGUI.Response.text(this.getMain().getLocale("gui.errorNotANumber"));
                        }
                    })
                    .onClose(pl -> {
                        click.getGui().show(pl);
                    })
                    .title(getMain().getLocale("gui.depositing", pool.getItemRaffleDepository().getName()))
                    .text("0")
                    .plugin(main)
                    .open((Player) player);

            return true;
        }, this.getMain().getLocale("gui.depositButton"),
                this.getMain().getLocale("gui.leftClick", this.getMain().getLocale("gui.depositButton"))));


        Map<String, String> playerDepositMap = pool.getItemRaffleDepository().getPlayerDepositMap();
        this.getGui().addElement(
                new DynamicGuiElement('g', viewer -> {
                    List<Map.Entry<String, String>> list = new LinkedList<>(playerDepositMap.entrySet());
                    list.sort((o1, o2) -> new BigDecimal(o2.getValue()).compareTo(new BigDecimal(o1.getValue())));
                    GuiElementGroup group = new GuiElementGroup('g');
                    boolean served = false;
                    for (int i = 0; i < list.size(); i++) {
                        Map.Entry<String, String> playerDeposit = list.get(i);
                        String skullName = playerDeposit.getKey();
                        ItemStack icon;
                        if (this.getMain().getSkullManager().isServed(player.getName(), skullName)) {
                            icon = this.getMain().getSkullManager().getSkull(skullName);
                        } else {
                            if (served) {
                                icon = this.getMain().getSkullManager().getDefault();
                            } else {
                                icon = this.getMain().getSkullManager().serve(player.getName(), skullName);
                                served = true;
                            }
                        }
                        group.addElement(new StaticGuiElement('g', icon,
                                this.getMain().getLocale("gui.playerName", playerDeposit.getKey()),
                                this.getMain().getLocale("gui.playerDeposit", playerDeposit.getValue()),
                                this.getMain().getLocale("gui.playerChance", pool.calculateChanceToString(pool.getTotalPoolDeposit(), playerDeposit.getValue()))))
                        ;
                    }
                    return group;
                }));


        this.getGui().addElement(
                new DynamicGuiElement('c', viewer -> {
                    ItemStack skull = this.getMain().getSkullManager().getSkull(player.getName());
                    if (playerDepositMap.containsKey(player.getName())) {
                        return new StaticGuiElement('c', skull,
                                this.getMain().getLocale("gui.playerName", player.getName()),
                                this.getMain().getLocale("gui.playerDeposit", playerDepositMap.get(player.getName())),
                                this.getMain().getLocale("gui.playerChance", pool.calculateChanceToString(pool.getTotalPoolDeposit(), playerDepositMap.get(player.getName()))));
                    } else {
                        return new StaticGuiElement('c', new ItemStack(Material.AIR));
                    }
                }));

        this.getGui().addElement(new GuiPageElement('p', new ItemStack(XMaterial.matchXMaterial("OAK_SIGN").get().parseItem()), GuiPageElement.PageAction.PREVIOUS, this.getMain().getLocale("gui.prevPage")));
        this.getGui().addElement(new GuiPageElement('n', new ItemStack(XMaterial.matchXMaterial("OAK_SIGN").get().parseItem()), GuiPageElement.PageAction.NEXT, this.getMain().getLocale("gui.nextPage")));

    }

    @Override
    String[] getSetup() {
        return new String[]{
                "dhc     i",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "p       n"
        };
    }
}

package org.akadia.itemraffle.gui;

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

    private final ItemRafflePool pool;

    public PoolViewerMenu(ItemRaffleMain main, HumanEntity player, ItemRafflePool pool) {
        super(main, player, pool.getItemRaffleDepository().getName());
        this.pool = pool;

        ItemRaffleDepository depository = pool.getItemRaffleDepository();
        if (pool.isEmpty()) {

            return;
        }

        this.getGui().addElement(new DynamicGuiElement('i', (viewer) -> new StaticGuiElement('i', pool.getSelectedItemStack()))); // icon
        this.getGui().addElement(new StaticGuiElement('h', new ItemStack(Material.BOOK), 1, click -> {
            this.pool.getDepositoryHistoryCommonMenu().open(player);
            return true;
        }));

        this.getGui().addElement(new StaticGuiElement('d', new ItemStack(Material.OAK_SIGN), 1, click -> {
            click.getGui().close();
            new AnvilGUI
                    .Builder()
                    .onComplete((pl, text) -> {
                        try {
                            double v = Double.parseDouble(text);// test if double
                            if (v <= 0) {
                                return AnvilGUI.Response.text("Cannot be negative!");
                            }
                            if (!pool.playerDeposit(pl, text)) {
                                return AnvilGUI.Response.text("Not enough money!");
                            }
                            getMain().getDepositoryConfiguration().saveDepository(depository);

                            return AnvilGUI.Response.close();
                        } catch (NumberFormatException e) {
                            return AnvilGUI.Response.text("Not a number!");
                        }
                    })
                    .onClose(pl -> {
                        List<Map.Entry<String, String>> list = new LinkedList<>(pool.getItemRaffleDepository().getPlayerDepositMap().entrySet());
                        list.sort((o1, o2) -> new BigDecimal(o2.getValue()).compareTo(new BigDecimal(o1.getValue())));

                        GuiElementGroup group = new GuiElementGroup('g');

                        for (Map.Entry<String, String> playerDeposit : list) {
                            group.addElement(new StaticGuiElement('g', new ItemStack(Material.PAPER), playerDeposit.getKey(), playerDeposit.getValue()));
                        }

                        getGui().addElement(group);

                        click.getGui().show(pl);
                    })
                    .title("Depositing to " + pool.getItemRaffleDepository().getName())
                    .text("0")
                    .plugin(main)
                    .open((Player) player);

            return true;
        }, "Deposit"));

        List<Map.Entry<String, String>> list = new LinkedList<>(pool.getItemRaffleDepository().getPlayerDepositMap().entrySet());
        list.sort((o1, o2) -> new BigDecimal(o2.getValue()).compareTo(new BigDecimal(o1.getValue())));


        GuiElementGroup group = new GuiElementGroup('g');

        for (Map.Entry<String, String> playerDeposit : list) {
            group.addElement(new StaticGuiElement('g', new ItemStack(Material.PAPER), playerDeposit.getKey(), playerDeposit.getValue()));
        }
        this.getGui().addElement(group);

        this.getGui().addElement(new GuiPageElement('p', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
        this.getGui().addElement(new GuiPageElement('n', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

    }

    @Override
    String[] getSetup() {
        return new String[]{
                "dih       ",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "p       n"
        };
    }

    @Override
    String getName() {
        return this.pool.getItemRaffleDepository().getName();
    }


}
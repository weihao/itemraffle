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
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PoolViewerMenu extends BaseMenu {

    private final ItemRafflePool pool;

    public PoolViewerMenu(ItemRaffleMain main, HumanEntity player, ItemRafflePool pool) {
        super(main, player, MessageFormat.format(main.getLocale("gui", "poolMenuTitle"), pool.getItemRaffleDepository().getName(), pool.getRaffleId()));
        this.pool = pool;

        ItemRaffleDepository depository = pool.getItemRaffleDepository();
        if (!pool.validateDepository()) {
            this.getGui().close();
            return;
        }

        this.getGui().addElement(new DynamicGuiElement('i', (viewer) -> {
            if (!pool.validateDepository()) {
                return new StaticGuiElement('i', new ItemStack(Material.END_PORTAL_FRAME));
            }
            return new StaticGuiElement('i', pool.getSelectedItemStack());

        })); // icon
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

        this.getGui().addElement(new GuiPageElement('p', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.PREVIOUS, this.getMain().getLocale("gui.prevPage")));
        this.getGui().addElement(new GuiPageElement('n', new ItemStack(XMaterial.matchXMaterial("SIGN").get().parseItem()), GuiPageElement.PageAction.NEXT, this.getMain().getLocale("gui.nextPage")));

    }

    @Override
    String[] getSetup() {
        return new String[]{
                "dih      ",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "p       n"
        };
    }
}

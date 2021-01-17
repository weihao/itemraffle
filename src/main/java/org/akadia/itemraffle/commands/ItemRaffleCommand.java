package org.akadia.itemraffle.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.guis.BoxViewerMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ir|itemraffle")
public class ItemRaffleCommand extends BaseCommand {

    private final ItemRaffleMain main;

    public ItemRaffleCommand(ItemRaffleMain main) {
        this.main = main;
    }

    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Default
    @Subcommand("menu")
    public void onMenu(Player player) {
        this.main.getGuiManager().getDepositoryListCommonMenu().open(player);
    }

    @Subcommand("box")
    public void onBox(Player player) {
        new BoxViewerMenu(main, player).open(player);
    }

}

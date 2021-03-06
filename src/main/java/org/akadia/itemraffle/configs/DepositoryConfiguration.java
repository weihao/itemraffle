package org.akadia.itemraffle.configs;

import com.cryptomorin.xseries.XMaterial;
import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.akadia.itemraffle.data.ItemRaffleWinnerInfo;
import org.akadia.itemraffle.enums.DepositoryMode;
import org.akadia.itemraffle.enums.DepositorySelection;
import org.akadia.itemraffle.utils.ItemStackUtil;
import org.akadia.itemraffle.utils.SerializeUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DepositoryConfiguration extends Configuration {
    private final List<ItemRaffleDepository> depositories;

    public DepositoryConfiguration(ItemRaffleMain main) {
        super(main);
        this.depositories = new ArrayList<>();

        Set<String> depositoryKeys = this.getConfigurationSection("depository");
        for (String depositoryKey : depositoryKeys) {
            String selection = this.getString("depository", depositoryKey, "depositorySelection").toUpperCase();
            String mode = this.getString("depository", depositoryKey, "depositoryMode").toUpperCase();
            String iconItem = this.getString("depository", depositoryKey, "icon");
            String base64 = this.getString("depository", depositoryKey, "prizes");
            ItemStack icon = XMaterial.matchXMaterial(iconItem).get().parseItem();

            String name = this.getString("depository", depositoryKey, "name");
            DepositorySelection depositorySelection = DepositorySelection.valueOf(selection);
            DepositoryMode depositoryMode = DepositoryMode.valueOf(mode);
            int itemSelectIndex = this.getInt("depository", depositoryKey, "itemSelectIndex");
            int drawingInterval = this.getInt("depository", depositoryKey, "drawingInterval");
            long nextDrawingTime = this.getLong("depository", depositoryKey, "nextDrawingTime");
            ItemStack[] prizes = SerializeUtil.itemStackArrayFromBase64(base64);
            List<ItemStack> prizesList = ItemStackUtil.arrayToList(prizes);
            Set<String> players = this.getConfigurationSection("depository", depositoryKey, "deposits");
            Map<String, String> deposits = new HashMap<>();
            for (String player : players) {
                deposits.put(player, this.getString("depository", depositoryKey, "deposits", player));
            }

            List<ItemRaffleWinnerInfo> history = new ArrayList<>();

            Set<String> historyKeys = this.getConfigurationSection("depository", depositoryKey, "history");
            for (String historyKey : historyKeys) {
                String id = this.getString("depository", depositoryKey, "history", historyKey, "id");
                String username = this.getString("depository", depositoryKey, "history", historyKey, "username");
                long drawTimestamp = this.getLong("depository", depositoryKey, "history", historyKey, "drawTimestamp");
                String totalPoolValue = this.getString("depository", depositoryKey, "history", historyKey, "totalPoolValue");
                String totalEntry = this.getString("depository", depositoryKey, "history", historyKey, "totalEntry");
                String playerDepositValue = this.getString("depository", depositoryKey, "history", historyKey, "playerDepositValue");
                String chance = this.getString("depository", depositoryKey, "history", historyKey, "chance");
                String awardedPrize = this.getString("depository", depositoryKey, "history", historyKey, "awardedPrize");
                ItemStack awardedItem = SerializeUtil.itemStackArrayFromBase64(awardedPrize)[0];
                history.add(new ItemRaffleWinnerInfo(id, username, drawTimestamp, totalPoolValue, totalEntry, playerDepositValue, chance, awardedItem));
            }

//            final String AB = "abcdefghijklmnopqrstuvwxyz";
//            SecureRandom rnd = new SecureRandom();
//
//            for (int j = 0; j < 10; j++) {
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < 2; i++)
//                    sb.append(AB.charAt(rnd.nextInt(AB.length())));
//
//                deposits.put(sb.toString(), String.valueOf(new Random().nextInt(1000000)));
//            }


            depositories.add(new ItemRaffleDepository(
                    depositoryKey,
                    depositorySelection,
                    depositoryMode,
                    itemSelectIndex,
                    drawingInterval,
                    nextDrawingTime,
                    icon,
                    prizesList,
                    name,
                    deposits,
                    history));

        }

        Collections.sort(depositories);
    }

    @Override
    public void onDisable() {
        for (ItemRaffleDepository depository : this.getDepositories()) {
            this.saveDepository(depository);
        }
    }

    public void saveDepository(ItemRaffleDepository depository) {
//       this.config.set("depository",  depository.getKey() , "depositorySelection", depository.getDepositorySelection());
//       this.config.set("depository",  depository.getKey() , "depositoryMode", depository.getDepositoryMode());
        this.setValue(
                depository.getItemSelectIndex(),
                "depository", depository.getKey(), "itemSelectIndex"
        );
//       this.config.set("depository",  depository.getKey() , "drawingInterval", depository.getDrawingInterval());
        this.setValue(
                depository.getNextDrawingTime(),
                "depository", depository.getKey(), "nextDrawingTime"
        );

        this.setValue(
                SerializeUtil.itemStackArrayToBase64(ItemStackUtil.listToArray(depository.getPrizes())),
                "depository", depository.getKey(), "prizes"
        );

        for (Map.Entry<String, String> deposit : depository.getPlayerDepositMap().entrySet()) {
            this.setValue(
                    deposit.getValue(),
                    "depository", depository.getKey(), "deposits", deposit.getKey());
        }

        for (ItemRaffleWinnerInfo history : depository.getHistory()) {
            this.setValue(history.getUsername(), "depository", depository.getKey(), "history", history.getId(), "username");
            this.setValue(String.valueOf(history.getDrawTimestamp()), "depository", depository.getKey(), "history", history.getId(), "drawTimestamp");
            this.setValue(String.valueOf(history.getTotalPoolValue()), "depository", depository.getKey(), "history", history.getId(), "totalPoolValue");
            this.setValue(String.valueOf(history.getTotalEntry()), "depository", depository.getKey(), "history", history.getId(), "totalEntry");
            this.setValue(String.valueOf(history.getPlayerDepositValue()), "depository", depository.getKey(), "history", history.getId(), "playerDepositValue");
            this.setValue(String.valueOf(history.getChance()), "depository", depository.getKey(), "history", history.getId(), "chance");
            this.setValue(SerializeUtil.itemStackArrayToBase64(new ItemStack[]{history.getAwardedPrize()}), "depository", depository.getKey(), "history", history.getId(), "awardedPrize");
        }


        this.writeConfigFile();
    }

    public List<ItemRaffleDepository> getDepositories() {
        return depositories;
    }


    @Override
    public String getConfigName() {
        return "depository.yml";
    }
}

package org.akadia.itemraffle.data;

import org.bukkit.inventory.ItemStack;

public class ItemRaffleWinnerInfo {

    private final String id;
    private final String username;
    private final long drawTimestamp;
    private final String totalPoolValue;
    private final String totalEntry;
    private final String playerDepositValue;
    private final String chance;
    private final ItemStack awardedPrize;

    public ItemRaffleWinnerInfo(String id, String username, long drawTimestamp, String totalPoolValue, String totalEntry, String playerDepositValue, String chance, ItemStack awardedPrize) {
        this.id = id;
        this.username = username;
        this.drawTimestamp = drawTimestamp;
        this.totalPoolValue = totalPoolValue;
        this.totalEntry = totalEntry;
        this.playerDepositValue = playerDepositValue;
        this.chance = chance;
        this.awardedPrize = awardedPrize;
    }

    public String getId() {
        return id;
    }

    public String getTotalEntry() {
        return totalEntry;
    }

    public String getUsername() {
        return username;
    }

    public long getDrawTimestamp() {
        return drawTimestamp;
    }

    public String getTotalPoolValue() {
        return totalPoolValue;
    }

    public String getPlayerDepositValue() {
        return playerDepositValue;
    }

    public String getChance() {
        return chance;
    }

    public ItemStack getAwardedPrize() {
        return awardedPrize;
    }


}

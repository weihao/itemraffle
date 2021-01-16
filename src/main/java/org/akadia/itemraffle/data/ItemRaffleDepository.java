package org.akadia.itemraffle.data;


import org.akadia.itemraffle.enums.DepositoryMode;
import org.akadia.itemraffle.enums.DepositorySelection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ItemRaffleDepository implements Comparable<ItemRaffleDepository> {
    private String key;
    private DepositorySelection depositorySelection;
    private DepositoryMode depositoryMode;
    private int itemSelectIndex;
    private int drawingInterval;
    private long nextDrawingTime;
    private ItemStack icon;
    private List<ItemStack> prizes;
    private String name;
    private Map<String, String> playerDepositMap;
    private List<ItemRaffleWinnerInfo> history;

    public ItemRaffleDepository(String key, DepositorySelection depositorySelection, DepositoryMode depositoryMode, int itemSelectIndex, int drawingInterval, long nextDrawingTime, ItemStack icon, List<ItemStack> prizes, String name, Map<String, String> playerDepositMap, List<ItemRaffleWinnerInfo> history) {
        this.key = key;
        this.depositorySelection = depositorySelection;
        this.depositoryMode = depositoryMode;
        this.itemSelectIndex = itemSelectIndex;
        this.drawingInterval = drawingInterval;
        this.nextDrawingTime = nextDrawingTime;
        this.icon = icon;
        this.prizes = prizes;
        this.name = name;
        this.playerDepositMap = playerDepositMap;
        this.history = history;
    }

    public String getKey() {
        return key;
    }

    public List<ItemRaffleWinnerInfo> getHistory() {
        return history;
    }

    public Map<String, String> getPlayerDepositMap() {
        return playerDepositMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DepositorySelection getDepositorySelection() {
        return depositorySelection;
    }

    public void setDepositorySelection(DepositorySelection depositorySelection) {
        this.depositorySelection = depositorySelection;
    }

    public DepositoryMode getDepositoryMode() {
        return depositoryMode;
    }

    public void setDepositoryMode(DepositoryMode depositoryMode) {
        this.depositoryMode = depositoryMode;
    }

    public int getItemSelectIndex() {
        return itemSelectIndex;
    }

    public void setItemSelectIndex(int itemSelectIndex) {
        this.itemSelectIndex = itemSelectIndex;
    }

    public int getDrawingInterval() {
        return drawingInterval;
    }

    public void setDrawingInterval(int drawingInterval) {
        this.drawingInterval = drawingInterval;
    }

    public long getNextDrawingTime() {
        return nextDrawingTime;
    }

    public void setNextDrawingTime(long nextDrawingTime) {
        this.nextDrawingTime = nextDrawingTime;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public List<ItemStack> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<ItemStack> prizes) {
        this.prizes = prizes;
    }

    @Override
    public int compareTo(@NotNull ItemRaffleDepository o) {
        return Comparator
                .comparingLong((ItemRaffleDepository a) -> a.nextDrawingTime)
                .thenComparing((ItemRaffleDepository::getName)).compare(this, o);
    }
}

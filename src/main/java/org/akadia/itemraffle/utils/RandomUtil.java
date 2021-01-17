package org.akadia.itemraffle.utils;

import org.akadia.itemraffle.data.ItemRaffleEntryInfo;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class RandomUtil {
    private TreeMap<Double, ItemRaffleEntryInfo> weightMap;

    public RandomUtil(List<ItemRaffleEntryInfo> list) {
        weightMap = new TreeMap<>();
        for (ItemRaffleEntryInfo entryInfo : list) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();
            this.weightMap.put(Double.parseDouble(entryInfo.getDeposit()) + lastWeight, entryInfo);
        }
    }

    public ItemRaffleEntryInfo random() {
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, ItemRaffleEntryInfo> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }
}

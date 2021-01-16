package org.akadia.itemraffle.utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtil {
    public static ItemStack[] listToArray(List<ItemStack> list) {
        ItemStack[] items = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            items[i] = list.get(i);
        }
        return items;
    }

    public static List<ItemStack> arrayToList(ItemStack[] array) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack itemStack : array) {
            if (itemStack != null) {
                list.add(itemStack);
            }
        }
        return list;
    }
}

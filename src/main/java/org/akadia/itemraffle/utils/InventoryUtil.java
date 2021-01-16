package org.akadia.itemraffle.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static ItemStack[] sort(Inventory inv) {
        ItemStack[] contents = inv.getContents();
        inv.clear();
        int validIndex = 0;
        for (ItemStack itemStack : contents) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                inv.setItem(validIndex++, itemStack);
            }
        }
        return inv.getContents();
    }
}

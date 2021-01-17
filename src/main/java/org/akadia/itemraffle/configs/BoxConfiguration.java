package org.akadia.itemraffle.configs;

import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.utils.ItemStackUtil;
import org.akadia.itemraffle.utils.SerializeUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BoxConfiguration extends Configuration {
    public HashMap<String, List<ItemStack>> getBoxes() {
        return boxes;
    }

    private final HashMap<String, List<ItemStack>> boxes;

    public BoxConfiguration(ItemRaffleMain main) {
        super(main);
        this.boxes = new HashMap<>();
        List<String> players = this.getStringList("boxes");
        for (String player : players) {
            String base64 = this.getString("boxes", player);
            List<ItemStack> list = ItemStackUtil.arrayToList(SerializeUtil.itemStackArrayFromBase64(base64));
            this.boxes.put(player, list);
        }
    }

    @Override
    public String getConfigName() {
        return "box.yml";
    }

    @Override
    public void onDisable() {
        for (String player : this.getBoxes().keySet()) {
            this.saveBox(player);
        }
    }

    public void addItemToBox(String player, ItemStack... itemStacks) {
        List<ItemStack> list = this.boxes.getOrDefault(player, new ArrayList<>());
        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null) {
                continue;
            }
            list.add(itemStack);
        }
        this.boxes.put(player, list);
        this.saveBox(player);
    }

    public void claimItem(Player player, int index) {
        ItemStack item = this.boxes.get(player.getName()).get(index);
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItem(player.getLocation(), item);
        }
        this.boxes.get(player.getName()).remove(index);
        this.saveBox(player.getName());
    }

    public void claimAll(Player player) {
        List<ItemStack> itemStacks = this.boxes.get(player.getName());
        for (ItemStack item : itemStacks) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItem(player.getLocation(), item);
            }
        }
        this.boxes.clear();
        this.saveBox(player.getName());
    }

    public void saveBox(String player) {
        List<ItemStack> list = this.boxes.get(player);
        this.setValue(SerializeUtil.itemStackArrayToBase64(ItemStackUtil.listToArray(list)), "boxes", player);
        this.writeConfigFile();
    }

}

package org.akadia.itemraffle.managers;

import org.akadia.itemraffle.ItemRaffleMain;
import org.akadia.itemraffle.ItemRafflePool;
import org.akadia.itemraffle.data.ItemRaffleDepository;

import java.util.ArrayList;
import java.util.List;

public class ItemRaffleManager {
    private final ItemRaffleMain main;
    List<ItemRafflePool> pools;

    public ItemRaffleManager(ItemRaffleMain main) {
        this.main = main;
        this.pools = new ArrayList<>();

        for (ItemRaffleDepository depository : main.getDepositoryConfiguration().getDepositories()) {
            ItemRafflePool itemRafflePool = new ItemRafflePool(main, depository);
            this.pools.add(itemRafflePool);
        }

    }

    public List<ItemRafflePool> getPools() {
        return pools;
    }


}

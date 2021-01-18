package org.akadia.itemraffle;

import net.milkbowl.vault.economy.EconomyResponse;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.akadia.itemraffle.data.ItemRaffleEntryInfo;
import org.akadia.itemraffle.data.ItemRaffleWinnerInfo;
import org.akadia.itemraffle.enums.PoolState;
import org.akadia.itemraffle.guis.DepositoryHistoryCommonMenu;
import org.akadia.itemraffle.guis.DepositoryViewerCommonMenu;
import org.akadia.itemraffle.guis.PoolViewerMenu;
import org.akadia.itemraffle.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.management.openmbean.OpenDataException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

public class ItemRafflePool {

    public ItemRaffleMain getMain() {
        return main;
    }

    public void setDepositoryViewerCommonMenu(DepositoryViewerCommonMenu depositoryViewerCommonMenu) {
        this.depositoryViewerCommonMenu = depositoryViewerCommonMenu;
    }

    private final DepositoryHistoryCommonMenu depositoryHistoryCommonMenu;
    private final Map<String, PoolViewerMenu> poolViewerMenus;
    private final ItemRaffleDepository itemRaffleDepository;
    private final ItemRaffleMain main;
    private DepositoryViewerCommonMenu depositoryViewerCommonMenu;
    private PoolState state;


    public ItemRafflePool(ItemRaffleMain main, ItemRaffleDepository itemRaffleDepository) {
        this.main = main;
        this.itemRaffleDepository = itemRaffleDepository;
        this.depositoryViewerCommonMenu = new DepositoryViewerCommonMenu(main, this);
        this.depositoryHistoryCommonMenu = new DepositoryHistoryCommonMenu(main, this);
        this.poolViewerMenus = new HashMap<>();

        if (itemRaffleDepository.getNextDrawingTime() == -1L) {
            this.setNextDrawingTime();
        }
        setState(PoolState.RUNNING);
    }

    public DepositoryHistoryCommonMenu getDepositoryHistoryCommonMenu() {
        return depositoryHistoryCommonMenu;
    }

    public DepositoryViewerCommonMenu getDepositoryViewerCommonMenu() {
        return depositoryViewerCommonMenu;
    }

    public Map<String, PoolViewerMenu> getPoolViewerMenus() {
        return poolViewerMenus;
    }

    public ItemRaffleDepository getItemRaffleDepository() {
        return itemRaffleDepository;
    }

    public PoolState getState() {
        return state;
    }

    public void setState(PoolState state) {
        this.state = state;
    }

    public long getRemainingNextDrawTime() {
        return (itemRaffleDepository.getNextDrawingTime() - System.currentTimeMillis()) / 1000L;
    }

    public void refreshView() {
        this.depositoryViewerCommonMenu.getGui().destroy();
        this.depositoryViewerCommonMenu = new DepositoryViewerCommonMenu(main, this);
    }

    public void run() {
        // 暂停状态
        if (!this.validateDepository()) {
            this.setState(PoolState.ERROR);
//            this.getMain().getLogger().log(Level.WARNING, "物品抽奖仓库设置错误, 抽奖池处于无效状态...");
            return;
        }

        if (this.getState().equals(PoolState.BLOCKED)) {
//            this.getMain().getLogger().log(Level.INFO, "物品抽奖仓库暂停中...");

            return;
        }

//        this.getMain().getLogger().log(Level.INFO, "抽奖池开始正常工作, 距离此次开奖剩余 {0} 秒...", getRemainingNextDrawTime());
        this.setState(PoolState.RUNNING);

        if (this.isDrawingTimeNow()) {
            if (!validateDrawCondition()) {
                this.setNextDrawingTime();
                return;
            }
            ItemRaffleWinnerInfo winnerInfo = calculateFinalAwardWinner();
            this.refreshView();
            this.getItemRaffleDepository().getHistory().add(winnerInfo);
            this.getMain().getDepositoryConfiguration().saveDepository(this.getItemRaffleDepository());
        }
    }

    /**
     * @return validate if the depository is setup correctly
     */
    public boolean validateDepository() {
        return this.isSelectedIndexValid() && !this.isEmpty();
    }

    /**
     * @return validate if the pool is ready to draw a winner
     */
    public boolean validateDrawCondition() {
        return this.getPlayerCount() >= 1;
    }

    /**
     * @return validate if the timer ready to draw a winner
     */
    public boolean isDrawingTimeNow() {
        return this.getItemRaffleDepository().getNextDrawingTime() <= System.currentTimeMillis();
    }

    public void setNextDrawingTime() {
        this.itemRaffleDepository.setNextDrawingTime(System.currentTimeMillis() + this.itemRaffleDepository.getDrawingInterval() * 1000L);
    }

    private BigDecimal handleBigDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) bigDecimal = new BigDecimal(0d);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean playerDeposit(Player player, String deposit) {
        BigDecimal bigDecimal = deductPlayerEco(player, handleBigDecimal(new BigDecimal(deposit)));
        return bigDecimal != null && playerDeposit(player.getName(), bigDecimal);
    }

    private boolean playerDeposit(String username, BigDecimal deposit) {
        String cache = itemRaffleDepository.getPlayerDepositMap().get(username);
        if (cache == null) {
            itemRaffleDepository.getPlayerDepositMap().put(username, deposit.toPlainString());
            return true;
        }
        deposit = deposit.add(new BigDecimal(cache));
        itemRaffleDepository.getPlayerDepositMap().replace(username, deposit.toPlainString());
        return true;
    }


    private BigDecimal deductPlayerEco(Player player, BigDecimal bigDecimal) {
        try {
            double value = bigDecimal.doubleValue();
            if (!this.getMain().getEconomy().has(player, value)) {
                player.sendMessage(this.getMain().getLocale("msg.notEnoughMoney"));
                return null;
            }
            EconomyResponse response = this.getMain().getEconomy().withdrawPlayer(player, value);
            if (response == null || !response.transactionSuccess())
                throw new OpenDataException(response != null ? response.errorMessage : "null");

        } catch (Exception e) {
            this.getMain().getLogger().log(Level.SEVERE, MessageFormat.format(main.getLocale("log.failedDepositDeduction"), player.getName(), e));
            return null;
        }
        return bigDecimal;
    }

    public BigDecimal getTotalPoolDeposit() {
        // 获取当前抽奖池总共掷入的金钱数据
        BigDecimal base = new BigDecimal(0);
        for (String economy : getItemRaffleDepository().getPlayerDepositMap().values())
            base = base.add(new BigDecimal(economy));
        return handleBigDecimal(base);
    }

    /**
     * @return total number of players that have deposit in the pool
     */
    public int getPlayerCount() {
        return this.getItemRaffleDepository().getPlayerDepositMap().size();
    }

    /**
     * @return total number of prizes in the pool
     */
    public int getItemCount() {
        return this.getItemRaffleDepository().getPrizes().size();
    }

    private ItemRaffleWinnerInfo calculateFinalAwardWinner() {
        int totalEntry = getPlayerCount();

        if (totalEntry == 0) {
            // 并没有任何玩家进行掷入金钱
            this.getMain().getLogger().log(Level.INFO, "此次抽奖池没有任何玩家掷入金钱, 无法计算获奖者...");
            return null;
        }

        ItemRaffleEntryInfo winnerEntry;
        BigDecimal totalPoolDeposit = getTotalPoolDeposit();
        ItemStack prize = this.getSelectedItemStack();
        if (totalEntry > 1) {
            List<ItemRaffleEntryInfo> list = new ArrayList<>();
            for (Map.Entry<String, String> playerDeposit : itemRaffleDepository.getPlayerDepositMap().entrySet()) {
                list.add(new ItemRaffleEntryInfo(playerDeposit.getKey(), playerDeposit.getValue()));
            }

            winnerEntry = new RandomUtil(list).random();
        } else {
            Map.Entry<String, String> winEntry = new ArrayList<>(itemRaffleDepository.getPlayerDepositMap().entrySet()).get(0);
            winnerEntry = new ItemRaffleEntryInfo(winEntry.getKey(), winEntry.getValue());
        }

        if (winnerEntry == null) {
            this.getMain().getLogger().log(Level.SEVERE,
                    MessageFormat.format(this.getMain().getLocale("log.failedCalculateWinner"),
                            this.getItemRaffleDepository().getName()));
            return null;
        }
        ItemRaffleWinnerInfo winner = new ItemRaffleWinnerInfo(
                String.valueOf(this.getRaffleId()),
                winnerEntry.getUsername(),
                itemRaffleDepository.getNextDrawingTime(),
                this.getTotalPoolDeposit().toPlainString(),
                String.valueOf(totalEntry),
                winnerEntry.getDeposit(),
                this.calculateChanceToString(totalPoolDeposit, winnerEntry.getDeposit()),
                prize);
        // 发送全服消息通知本次获奖者
//        sendAwardWinnerMessage(total, winner, awardItem);

        this.getMain().getLogger().log(Level.INFO, "恭喜此次抽奖池获奖者玩家 {0}, 总掷入 {1} 金钱.", new Object[]{winner.getUsername(), winner.getPlayerDepositValue()});

        switch (itemRaffleDepository.getDepositoryMode()) {
            case DRAIN:
                this.shiftPrizes();
                break;
            case CYCLE:
                this.incrementSelectIndex();
                if (!isSelectedIndexValid()) {
                    this.getItemRaffleDepository().setItemSelectIndex(0);
                }
                break;
            default:
                this.incrementSelectIndex();
        }
        this.setNextDrawingTime();
        this.getItemRaffleDepository().getPlayerDepositMap().clear();
        this.getMain().getBoxManager().addItemToBox(winner.getUsername(), winner.getAwardedPrize());
        Player player = Bukkit.getServer().getPlayer(winner.getUsername());
        if (player != null)
            player.sendMessage(
                    MessageFormat.format(getMain().getLocale("msg.playerWinningMessage"),
                            winner.getTotalPoolValue(),
                            this.getItemRaffleDepository().getName(),
                            winner.getChance()));
        return winner;
    }

    public void shiftPrizes() {
        List<ItemStack> prizes = itemRaffleDepository.getPrizes();
        prizes.remove(0);
    }

    public void incrementSelectIndex() {
        int selectIndex;
        switch (itemRaffleDepository.getDepositorySelection()) {
            case RANDOM:
                selectIndex = new Random().nextInt(itemRaffleDepository.getPrizes().size());
                break;
            case SEQUENTIAL:
                selectIndex = itemRaffleDepository.getItemSelectIndex() + 1;
                break;
            default:
                selectIndex = itemRaffleDepository.getItemSelectIndex() + 1;
        }
        itemRaffleDepository.setItemSelectIndex(selectIndex);
    }

    public ItemStack getSelectedItemStack() {
        return itemRaffleDepository.getPrizes().get(itemRaffleDepository.getItemSelectIndex());
    }

    public boolean isSelectedIndexValid() {
        return this.getItemRaffleDepository().getItemSelectIndex() < this.getItemCount();
    }

    private BigDecimal calculateChance(BigDecimal total, String economy) {
        BigDecimal bigDecimal;
        BigDecimal bigDecimalEconomy = new BigDecimal(economy);

        if (total.compareTo(BigDecimal.ZERO) == 0)
            return new BigDecimal(0);
        if (total.compareTo(BigDecimal.ZERO) > 0 && bigDecimalEconomy.compareTo(total) == 0)
            return new BigDecimal(100);

        try {
            bigDecimal = bigDecimalEconomy.divide(total, 10, RoundingMode.HALF_UP).stripTrailingZeros();
        } catch (Exception e) {
            // 异常说明整除舍入报错则使用不舍入模式
            bigDecimal = bigDecimalEconomy.divide(total, 10, RoundingMode.UNNECESSARY).stripTrailingZeros();
        }
        return bigDecimal;
    }

    private double calculateChanceToDouble(BigDecimal total, String economy) {
        return calculateChance(total, economy).doubleValue();
    }

    private String calculateChanceToString(BigDecimal total, String economy) {
        BigDecimal chance = calculateChance(total, economy);
        if (chance.doubleValue() <= 1d)
            chance = chance.multiply(new BigDecimal(100));
        return chance.toPlainString();
    }

    public boolean isEmpty() {
        return this.getItemRaffleDepository().getPrizes().size() <= 0;
    }

    public int getRaffleId() {
        return itemRaffleDepository.getHistory().size() + 1;
    }

}

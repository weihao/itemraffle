package org.akadia.itemraffle;

import net.milkbowl.vault.economy.EconomyResponse;
import org.akadia.itemraffle.data.ItemRaffleDepository;
import org.akadia.itemraffle.data.ItemRaffleEntryInfo;
import org.akadia.itemraffle.data.ItemRaffleWinnerInfo;
import org.akadia.itemraffle.enums.PoolState;
import org.akadia.itemraffle.gui.DepositoryViewerCommonMenu;
import org.akadia.itemraffle.gui.PoolViewerMenu;
import org.akadia.itemraffle.utils.RandomUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.management.openmbean.OpenDataException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

public class ItemRafflePool {

    private final DepositoryViewerCommonMenu depositoryViewerCommonMenu;
    private final Map<String, PoolViewerMenu> poolViewerMenus;
    private final ItemRaffleDepository itemRaffleDepository;
    private final ItemRaffleMain main;
    private PoolState state;


    public ItemRafflePool(ItemRaffleMain main, ItemRaffleDepository itemRaffleDepository) {
        this.main = main;
        this.itemRaffleDepository = itemRaffleDepository;
        this.depositoryViewerCommonMenu = new DepositoryViewerCommonMenu(main, itemRaffleDepository);
        this.poolViewerMenus = new HashMap<>();

        if (!this.hasValidDepository()) {
            // 物品抽奖仓库没有有效的物品
            this.setState(PoolState.STOPPED);
            this.main.getLogger().log(Level.WARNING, "物品抽奖仓库未存在物品, 抽奖池处于无效状态...");
            return;
        }

        if (this.getItemRaffleDepository().getNextDrawingTime() < System.currentTimeMillis()) {
            this.setNextDrawingTime();
        }

        this.setState(PoolState.RUNNING);
        this.main.getLogger().log(Level.INFO, "抽奖池开始正常工作, 距离此次开奖剩余 {0} 秒...", getRemainingNextDrawTime());
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

    private boolean hasValidDepository() {
        return itemRaffleDepository.getPrizes().length > 0;
    }

    public long getRemainingNextDrawTime() {
        return (itemRaffleDepository.getNextDrawingTime() - System.currentTimeMillis()) / 1000L;
    }

    public void run() {
        this.main.getLogger().log(Level.INFO, "抽奖池开始正常工作, 距离此次开奖剩余 {0} 秒...", getRemainingNextDrawTime());

        if (this.getState().equals(PoolState.STOPPED)) {
            // 如果是暂停状态

            return;
        }
        if (this.getState().equals(PoolState.ERROR)) {
            // 处于错误状态

            return;
        }
//
        if (this.getItemRaffleDepository().getNextDrawingTime() <= System.currentTimeMillis()) {
            if (!validateOpenCondition()) {
                this.main.getLogger().log(Level.INFO, "此次抽奖池未达到开奖条件, 开始下一轮抽奖...");
                this.setNextDrawingTime();
                return;
            }
            ItemRaffleWinnerInfo winnerInfo = calculateFinalAwardWinner();
            // 可以开奖了
//            this.saveHistoryRecord(calculateFinalAwardWinner()); // 计算最终获奖者保存此次抽奖池到历史记录
//            this.next(); // 开始下一次抽奖池
            return;
        }
    }

    private boolean validateOpenCondition() {
        // 验证抽奖池开奖条件
        return getPlayerCount() >= 1;
    }

    public void setNextDrawingTime() {
        itemRaffleDepository.setNextDrawingTime(System.currentTimeMillis() + itemRaffleDepository.getDrawingInterval() * 1000L);
    }

    private BigDecimal handlerBigDecimal(BigDecimal bigDecimal) {
        // 处理 BigDecimal 的小数保留位数以及四舍五入
        if (bigDecimal == null) bigDecimal = new BigDecimal(0d);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean addCurrentPoolPlayerEconomy0(Player player, String economy) {
        // 将指定玩家的金钱数据掷入到当前抽奖池
        BigDecimal bigDecimal = takePlayerEconomy(player, handlerBigDecimal(new BigDecimal(economy)));
        return bigDecimal != null && addCurrentPoolPlayerEconomy0(player.getName(), bigDecimal);
    }

    private boolean addCurrentPoolPlayerEconomy0(String name, BigDecimal economy) {
        // 将指定玩家的金钱数据掷入到当前抽奖池
        String cache = itemRaffleDepository.getPlayerDepositMap().get(name);
        if (cache == null) {
            itemRaffleDepository.getPlayerDepositMap().put(name, economy.toPlainString());
            return true;
        }
        economy = economy.add(new BigDecimal(cache));
        itemRaffleDepository.getPlayerDepositMap().replace(name, economy.toPlainString());
        return true;
    }


    private BigDecimal takePlayerEconomy(Player player, BigDecimal bigDecimal) {
        // 从指定玩家经济账户减去指定经济余额
        try {
            double value = bigDecimal.doubleValue();
            if (!this.main.getEconomy().has(player, value)) {
                // 玩家没有足够的金钱则提示并返回 null
                player.sendMessage("玩家没有足够的金钱");
                return null;
            }
            EconomyResponse response = this.main.getEconomy().withdrawPlayer(player, value);
            if (response == null || !response.transactionSuccess()) // 处理玩家经济时结果为 null 或没有成功则抛出异常
                throw new OpenDataException(response != null ? response.errorMessage : "null");

        } catch (Exception e) {
            this.main.getLogger().log(Level.SEVERE, "错误: 从玩家 '" + player.getName() + "' 的经济账户减少金钱时未成功, 异常信息:", e);
            return null;
        }
        return bigDecimal;
    }

    public BigDecimal getTotalPoolDeposit() {
        // 获取当前抽奖池总共掷入的金钱数据
        BigDecimal base = new BigDecimal(0);
        for (String economy : getItemRaffleDepository().getPlayerDepositMap().values())
            base = base.add(new BigDecimal(economy));
        return handlerBigDecimal(base);
    }

    public int getPlayerCount() {
        // 获取当前抽奖池总共掷入的玩家数量
        return getItemRaffleDepository().getPlayerDepositMap().size();
    }

    private ItemRaffleWinnerInfo calculateFinalAwardWinner() {
        ItemRaffleEntryInfo winnerEntry;
//         计算抽奖池最终的获奖者
        BigDecimal totalPoolDeposit = getTotalPoolDeposit();
        int totalEntry = getPlayerCount();

        if (totalEntry == 0) {
            // 并没有任何玩家进行掷入金钱
            this.main.getLogger().log(Level.INFO, "此次抽奖池没有任何玩家掷入金钱, 无法计算获奖者...");
            return null;
        }

        if (totalEntry > 1) {
            List<ItemRaffleEntryInfo> list = new ArrayList<>();
            for (Map.Entry<String, String> playerDeposit : itemRaffleDepository.getPlayerDepositMap().entrySet()) {
                list.add(new ItemRaffleEntryInfo(playerDeposit.getKey(), playerDeposit.getValue()));
            }

            winnerEntry = new RandomUtil(list).random();
        } else {
            // 只有一个玩家那肯定就是获奖者
            Map.Entry<String, String> winEntry = new ArrayList<>(itemRaffleDepository.getPlayerDepositMap().entrySet()).get(0);
            winnerEntry = new ItemRaffleEntryInfo(winEntry.getKey(), winEntry.getValue());
        }

        if (winnerEntry == null) {
            // 如果最终获奖者依然为 null 则发送控制台警告
            this.main.getLogger().log(Level.SEVERE, "错误: 计算抽奖池最终获奖者时 null 获奖者.");
            return null;
        }
        ItemRaffleWinnerInfo winnerInfo = new ItemRaffleWinnerInfo(
                String.valueOf(itemRaffleDepository.getHistory().size() + 1),
                winnerEntry.getUsername(),
                itemRaffleDepository.getNextDrawingTime(),
                this.getTotalPoolDeposit().toPlainString(),
                String.valueOf(totalEntry),
                winnerEntry.getDeposit(),
                this.calculateChanceToString(totalPoolDeposit, winnerEntry.getDeposit()),
                this.getSelectedItemStack());
        // 发送全服消息通知本次获奖者
//        sendAwardWinnerMessage(total, winnerInfo, awardItem);
        // 给获奖者执行自定义命令
//        sendAwardWinnerCommand(winnerInfo);

        // 打印到控制台信息
        this.main.getLogger().log(Level.INFO, "恭喜此次抽奖池获奖者玩家 {0}, 总掷入 {1} 金钱.", new Object[]{winnerInfo.getUsername(), winnerInfo.getPlayerDepositValue()});

        switch (itemRaffleDepository.getDepositoryMode()) {
            case DRAIN:
                this.shiftPrizes();
                break;
            default:
                this.incrementSelectIndex();
        }

        // 将抽奖池的物品发送给玩家
        this.main.getBoxManager().addItemToBox(winnerInfo.getUsername(), winnerInfo.getAwardedPrize());
//        if (ItemRaffleManager.sendItemToItemBox(winnerInfo.getUsername(), awardItem)) {
//            // 如果奖励物品发送成功则获取玩家如果在线则提示
//            Player player = PlayerManager.fromName(winnerInfo.getUsername());
//            if (player != null)
//                player.sendMessage(getMain().getMessage("ItemRaffleAwardSendItemBox"));
//        }
        return winnerInfo;
    }

    public void shiftPrizes() {
        ItemStack[] prizes = itemRaffleDepository.getPrizes();
        ItemStack[] shift = new ItemStack[prizes.length - 1];
        for (int i = 1; i < prizes.length; i++) {
            shift[i - 1] = prizes[i];
        }
        itemRaffleDepository.setPrizes(shift);
    }

    public void incrementSelectIndex() {
        int selectIndex;
        switch (itemRaffleDepository.getDepositorySelection()) {
            case RANDOM:
                selectIndex = new Random().nextInt(itemRaffleDepository.getPrizes().length);
                break;
            default:
                selectIndex = itemRaffleDepository.getItemSelectIndex() + 1;
        }
        itemRaffleDepository.setItemSelectIndex(selectIndex);
    }

    public ItemStack getSelectedItemStack() {
        return itemRaffleDepository.getPrizes()[itemRaffleDepository.getItemSelectIndex()];
    }

    private BigDecimal calculateChance(BigDecimal total, String economy) {
        // 计算此抽奖池用户的中奖几率
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
        // 计算此抽奖池用户的中奖几率
        return calculateChance(total, economy).doubleValue();
    }

    private String calculateChanceToString(BigDecimal total, String economy) {
        // 计算此抽奖池用户的中奖几率到字符串
        BigDecimal chance = calculateChance(total, economy);
        if (chance.doubleValue() <= 1d)
            chance = chance.multiply(new BigDecimal(100));
        return chance.toPlainString();
    }

    public boolean isEmpty() {
        return this.getItemRaffleDepository().getPrizes().length <= 0;
    }

}

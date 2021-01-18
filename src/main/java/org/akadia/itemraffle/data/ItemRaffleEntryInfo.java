package org.akadia.itemraffle.data;

public class ItemRaffleEntryInfo {
    public void setUsername(String username) {
        this.username = username;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    private  String username;
    private  String deposit;

    public ItemRaffleEntryInfo(String username, String deposit) {
        this.username = username;
        this.deposit = deposit;
    }

    public String getDeposit() {
        return deposit;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemRaffleEntryInfo that = (ItemRaffleEntryInfo) o;

        if (!this.username.equals(that.getUsername())) return false;
        return this.deposit.equals(that.getDeposit());
    }
}

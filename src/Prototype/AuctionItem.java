package Prototype;

import java.io.Serializable;

public class AuctionItem implements IBuyerItem, Serializable {
    private static int itemIdCount = 0;

    private final int itemId;
    private final String itemTitle;
    private final String itemDesc;
    private int price;
    private final int reservePrice;
    private final String sellerUsername;
    private String bidHolderUsername;
    private String bidHolderEmail;
    private boolean off = false;

    public AuctionItem(String itemTitle, String itemDesc, int initPrice, int reservePrice, String sellerUsername) {
        this.itemId = itemIdCount++;
        this.itemTitle = itemTitle;
        this.itemDesc = itemDesc;
        this.price = initPrice;
        this.reservePrice = reservePrice;
        this.sellerUsername = sellerUsername;
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public String getItemTitle() {
        return itemTitle;
    }

    @Override
    public String getItemDesc() {
        return itemDesc;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public int getReservePrice() {
        return reservePrice;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public String[] getBidHolder() {
        return new String[]{bidHolderUsername, bidHolderEmail};
    }

    public boolean isOff() {
        return off;
    }

    public void setOff(boolean off) {
        this.off = off;
    }

    public synchronized boolean setPrice(String bidHolderUsername, String bidHolderEmail, int price) {
        if (price <= this.price) {
            return false;
        }
        this.bidHolderUsername = bidHolderUsername;
        this.bidHolderEmail = bidHolderEmail;
        this.price = price;
        return true;
    }

    @Override
    public String toString() {
        return "AuctionItem{" +
                "itemId=" + itemId +
                ", itemTitle='" + itemTitle + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", price=" + price +
                ", reservePrice=" + reservePrice +
                ", sellerUsername='" + sellerUsername + '\'' +
                ", bidHolderUsername='" + bidHolderUsername + '\'' +
                ", bidHolderEmail='" + bidHolderEmail + '\'' +
                ", off=" + off +
                '}';
    }
}

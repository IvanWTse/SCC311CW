package Prototype;

import java.io.Serializable;

public class Requests {
    public static class sellerCreateAuction implements Serializable {
        private final String sellerUsername;
        private final String itemTitle;
        private final String itemDesc;
        private final int price;
        private final int reservePrice;

        public sellerCreateAuction(String sellerUsername, String itemTitle, String itemDesc, int price, int reservePrice) {
            this.itemTitle = itemTitle;
            this.sellerUsername = sellerUsername;
            this.itemDesc = itemDesc;
            this.price = price;
            this.reservePrice = reservePrice;
        }

        @Override
        public String toString() {
            return "sellerCreateAuction{" +
                    "sellerUsername='" + sellerUsername + '\'' +
                    ", itemTitle='" + itemTitle + '\'' +
                    ", itemDesc='" + itemDesc + '\'' +
                    ", price=" + price +
                    ", reservePrice=" + reservePrice +
                    '}';
        }

        public String getSellerUsername() {
            return sellerUsername;
        }

        public String getItemTitle() {
            return itemTitle;
        }

        public String getItemDesc() {
            return itemDesc;
        }

        public int getPrice() {
            return price;
        }

        public int getReservePrice() {
            return reservePrice;
        }
    }

    public static class BuyerBid implements Serializable {
        private final String email;
        private final int price;
        private final int auctionId;

        public BuyerBid(String email, int price, int auctionId) {
            this.email = email;
            this.price = price;
            this.auctionId = auctionId;
        }

        public String getEmail() {
            return email;
        }

        public int getPrice() {
            return price;
        }

        public int getAuctionId() {
            return auctionId;
        }

        @Override
        public String toString() {
            return "BuyerBid{" +
                    "email='" + email + '\'' +
                    ", price=" + price +
                    ", auctionId=" + auctionId +
                    '}';
        }
    }
}

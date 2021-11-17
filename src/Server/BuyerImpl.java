package Server;

import Prototype.AuctionItem;
import Prototype.IBuyerItem;
import Prototype.Requests;


import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BuyerImpl implements Prototype.IBuyer, Serializable {
    transient Cipher cipher;

    public BuyerImpl(){
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
    }

    @Override
    public ArrayList<IBuyerItem> inspectItems() {
        ArrayList<IBuyerItem> ret = new ArrayList<>();
        for (AuctionItem item : MainServer.items.values()) {
            if (!item.isOff()) {
                ret.add(item);
            }
        }
        return ret;
    }

    public SealedObject inspectItems(String username) throws RemoteException, InvalidKeyException {
        Key key = Utils.checkUsers(username);
        ArrayList<IBuyerItem> ret = new ArrayList<>();
        for (AuctionItem item : MainServer.items.values()) {
            if (!item.isOff()) {
                ret.add(item);
            }
        }
        cipher.init(Cipher.ENCRYPT_MODE, key);
        try {
            return new SealedObject(ret, cipher);
        } catch (IOException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
    }

    @Override
    public boolean bid(String username, byte[] usernameKey, String email, int price, int auctionId)
            throws RemoteException, InvalidKeyException {

        if (!MainServer.items.containsKey(auctionId)) {
            throw new RemoteException("Auction item not exists");
        }
        AuctionItem item = MainServer.items.get(auctionId);
        if (item.isOff()) {
            throw new RemoteException("Auction is off");
        }
        Utils.checkUsers(username, usernameKey);

        return item.setPrice(username, email, price);
    }

    public SealedObject bid(String username, SealedObject request) throws RemoteException, InvalidKeyException {
        Key privateKey = Utils.checkUsers(username);
        try {
            Requests.BuyerBid requestBody = (Requests.BuyerBid) request.getObject(privateKey);
            AuctionItem item = MainServer.items.get(requestBody.getAuctionId());
            if (item.isOff()) {
                throw new RemoteException("Auction is off");
            }
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return new SealedObject(item.setPrice(username, requestBody.getEmail(), requestBody.getPrice()), cipher);
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
    }

    @Override
    public boolean bidBelongs2You(String username, byte[] usernameKey, int auctionId)
            throws RemoteException, InvalidKeyException {
        if (!MainServer.items.containsKey(auctionId)) {
            throw new RemoteException("Auction item not exists");
        }
        Utils.checkUsers(username, usernameKey);
        AuctionItem item = MainServer.items.get(auctionId);
        return item.getBidHolder()[0].equals(username);
    }

    public SealedObject bidBelongs2You(String username, SealedObject request) throws RemoteException, InvalidKeyException {
        Key privateKey = Utils.checkUsers(username);
        try {
            Integer auctionId = (Integer) request.getObject(privateKey);
            if (!MainServer.items.containsKey(auctionId)) {
                throw new RemoteException("Auction item not exists");
            }
            AuctionItem item = MainServer.items.get(auctionId);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return new SealedObject(item.getBidHolder()[0].equals(username), cipher);
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
    }
}

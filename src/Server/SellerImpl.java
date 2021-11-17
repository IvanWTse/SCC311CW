package Server;

import Prototype.AuctionItem;
import Prototype.Requests;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SellerImpl implements Prototype.ISeller, Serializable {
    private transient Cipher cipher;

    public SellerImpl(){
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
    }

    @Override
    public int createAuction(String sellerUsername, byte[] sellerKey,
                             String itemTitle, String itemDesc, int price, int reservePrice)
            throws RemoteException, InvalidKeyException {

//        Utils.checkUsers(sellerUsername, sellerKey);
        AuctionItem auctionItem = new AuctionItem(itemTitle, itemDesc, price, reservePrice, sellerUsername);
        MainServer.items.put(auctionItem.getItemId(), auctionItem);
        return auctionItem.getItemId();
    }

    public SealedObject createAuction(String sellerUsername, SealedObject request) throws RemoteException, InvalidKeyException {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("secretKey"));
            Key key = (Key) ois.readObject();
            System.out.println(Arrays.toString(key.getEncoded()));
            Requests.sellerCreateAuction requestBody = (Requests.sellerCreateAuction) request.getObject(key);
            AuctionItem auctionItem = new AuctionItem(
                    requestBody.getItemTitle(),
                    requestBody.getItemDesc(),
                    requestBody.getPrice(),
                    requestBody.getReservePrice(),
                    sellerUsername
            );
            MainServer.items.put(auctionItem.getItemId(), auctionItem);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(auctionItem.getItemId(), cipher);
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new InternalError(e);
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException("Failed at verification. ");
        }
    }

    @Override
    public String[] stopAuction(int auctionId, String sellerUsername, byte[] sellerKey)
            throws InvalidKeyException, RemoteException {

        Utils.checkAuctionAndSeller(auctionId, sellerUsername, sellerKey);
        AuctionItem item = MainServer.items.get(auctionId);
        if (item.getPrice() < item.getReservePrice() || item.getBidHolder()[0] == null) {
            return null;
        }
        return MainServer.items.get(auctionId).getBidHolder();
    }

    public SealedObject stopAuction(String sellerUsername, SealedObject request) throws RemoteException, InvalidKeyException {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("secretKey"));
            Key key = (Key) ois.readObject();
            Integer auctionId = (Integer) request.getObject(key);
            AuctionItem item = MainServer.items.get(auctionId);
            if (item.getPrice() < item.getReservePrice() || item.getBidHolder()[0] == null) {
                return new SealedObject(null, cipher);
            }
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(MainServer.items.get(auctionId).getBidHolder(), cipher);
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new InternalError(e);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new InvalidKeyException("Failed at verification");
        }
    }

    @Override
    public AuctionItem inspectAuction(int auctionId, String sellerUsername, byte[] sellerKey)
            throws InvalidKeyException, RemoteException{

        Utils.checkAuctionAndSeller(auctionId, sellerUsername, sellerKey);
        return MainServer.items.get(auctionId);
    }

    public SealedObject inspectAuction(String sellerUsername, SealedObject request) throws RemoteException, InvalidKeyException {
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("secretKey"));
            Key key = (Key) ois.readObject();
            Integer auctionId = (Integer) request.getObject(key);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(MainServer.items.get(auctionId), cipher);
        } catch (NoSuchAlgorithmException | ClassNotFoundException | IOException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new InternalError(e);
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException("Failed at verification");
        }
    }

}

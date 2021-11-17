package Prototype;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;

public interface ISeller extends Remote {
    int createAuction(String sellerUsername, byte[] sellerKey,
                      String itemTitle, String itemDesc, int price, int reservePrice)
            throws RemoteException, InvalidKeyException;

    SealedObject createAuction(String sellerUsername, SealedObject request) throws RemoteException, InvalidKeyException;

    String[] stopAuction(int auctionId, String sellerUsername, byte[] sellerKey)
            throws InvalidKeyException, RemoteException;

    SealedObject stopAuction(String sellerUsername, SealedObject request) throws RemoteException, InvalidKeyException;

    AuctionItem inspectAuction(int auctionId, String sellerUsername, byte[] sellerKey)
            throws InvalidKeyException, RemoteException;

    SealedObject inspectAuction(String sellerUsername, SealedObject request) throws RemoteException, InvalidKeyException;
}

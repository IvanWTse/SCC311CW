package Prototype;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

public interface IBuyer extends Remote {
    ArrayList<IBuyerItem> inspectItems();

    SealedObject inspectItems(String username) throws RemoteException, InvalidKeyException;

    boolean bid(String username, byte[] usernameKey, String email, int price, int auctionId)
            throws RemoteException, InvalidKeyException;

    SealedObject bid(String username, SealedObject request) throws RemoteException, InvalidKeyException;

    boolean bidBelongs2You(String username, byte[] usernameKey, int auctionId)
            throws RemoteException, InvalidKeyException;

    SealedObject bidBelongs2You(String username, SealedObject request) throws RemoteException, InvalidKeyException;
}

package Server;

import Prototype.AuctionItem;
import Prototype.IBuyer;
import Prototype.IRegister;
import Prototype.ISeller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.Key;
import java.util.HashMap;

public class MainServer {
    public final static HashMap<String, Key> users = new HashMap<>();
    public final static HashMap<Integer, AuctionItem> items = new HashMap<>();

    public static void main(String[] args) throws RemoteException {
        LocateRegistry.createRegistry(6666);
        System.out.println("Registry created successfully");

        IRegister register = new Register();
        IBuyer buyer = new BuyerImpl();
        ISeller seller = new SellerImpl();

        try {
            Naming.rebind("rmi://localhost:6666/register", register);
            System.out.println("Register service deployed successfully! ");
            Naming.rebind("rmi://localhost:6666/buyer", buyer);
            System.out.println("Buyer service deployed successfully! ");
            Naming.rebind("rmi://localhost:6666/seller", seller);
            System.out.println("Seller service deployed successfully! ");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}

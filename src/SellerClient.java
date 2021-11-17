import Prototype.AuctionItem;
import Prototype.IRegister;
import Prototype.ISeller;
import Prototype.Requests;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

public class SellerClient {
    public static void main(String[] args) throws IOException {
        Key clientKey;
        ISeller seller;
        IRegister register;
        String userName = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return;
        }
        int auctionId;
        Scanner s = new Scanner(System.in);
        try {
            System.out.println("Please enter a username for signing up");
            userName = s.nextLine();
            register = (IRegister) Naming.lookup("rmi://127.0.0.1:6666/register");
            seller = (ISeller) Naming.lookup("rmi://127.0.0.1:6666/seller");
            clientKey = register.register(userName);
            System.out.printf("\nThe username is %s. Your public key is %s. \n", userName, Arrays.toString(clientKey.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            System.out.println("What do you want to operate? \n" +
                    "    A. Create an auction\n" +
                    "    B. Stop your auction\n" +
                    "    C. inspect your designate auction\n" +
                    "Enter your operation key or any other keys to exit");
            switch (s.nextLine()){
                case "A":
                case "a":
                    System.out.println("Please enter the title of your item");
                    String itemTitle = s.nextLine();
                    System.out.println("Please enter the description of your item");
                    String itemDesc = s.nextLine();
                    System.out.println("Please enter the price of your item");
                    int price = s.nextInt();
                    System.out.println("Please enter the reserve price of your item");
                    int reservePrice = s.nextInt();
                    try {
                        cipher.init(Cipher.ENCRYPT_MODE, clientKey);
                        Requests.sellerCreateAuction requestBody = new Requests.sellerCreateAuction(userName, itemTitle, itemDesc, price, reservePrice);
                        SealedObject response = seller.createAuction(userName, new SealedObject(requestBody, cipher));
                        auctionId = (Integer) response.getObject(clientKey);
                        System.out.println("Your auction is on the shelf. The auction ID is " + auctionId + ". " +
                                "Please mark it down and remember it. You will need the ID to manage your auction. ");
                    } catch (RemoteException e) {
                        System.out.println("Failed! " + e.getMessage());
                    } catch (InvalidKeyException e) {
                        System.out.println("Failed to verification your identity. ");
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                        System.out.println("Failed! " + e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                    break;
                case "B":
                case "b":
                    System.out.println("Please enter the auction ID which was created by you and you want to stop it");
                    auctionId = s.nextInt();
                    try {
                        cipher.init(Cipher.ENCRYPT_MODE, clientKey);
                        SealedObject requestBody = new SealedObject(auctionId, cipher);
                        SealedObject responseBody = seller.stopAuction(userName, requestBody);
                        String[] bidInfo = (String[]) responseBody.getObject(clientKey);
                        System.out.println("Stop successfully! ");
                        if (bidInfo == null) {
                            System.out.println("Your auction will be bought-in. ");
                        } else {
                            System.out.printf("The winner of your auction is %s. Email address is %s", bidInfo[0], bidInfo[1]);
                        }
                    } catch (RemoteException e) {
                        System.out.println("Failed! " + e.getMessage());
                    } catch (IllegalBlockSizeException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                        System.out.println("Failed! " + e.getMessage());
                        return;
                    } catch (InvalidKeyException e) {
                        System.out.println("Failed to verification your identity. ");
                    }
                    break;
                case "C":
                case "c":
                    System.out.println("Please enter the auction ID which was created by you and you want to inspect it");
                    auctionId = s.nextInt();
                    try {
                        cipher.init(Cipher.ENCRYPT_MODE, clientKey);
                        SealedObject requestBody = new SealedObject(auctionId, cipher);
                        SealedObject responseBody = seller.inspectAuction(userName, requestBody);
                        AuctionItem auctionItem = (AuctionItem) responseBody.getObject(clientKey);
                        System.out.println("                   Item ID:  " + auctionItem.getItemId());
                        System.out.println("                Item Title:  " + auctionItem.getItemTitle());
                        System.out.println("          Item Description:  " + auctionItem.getItemDesc());
                        System.out.println("            Active Auction?  " + (auctionItem.isOff()?"No":"Yes"));
                        System.out.println("             Reserve Price:  " + auctionItem.getReservePrice());
                        System.out.println("             Current Price:  " + auctionItem.getPrice());
                        if (auctionItem.getBidHolder()[0] != null) {
                            System.out.println(" Highest bid holder's name:  " + auctionItem.getBidHolder()[0]);
                        }
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                        System.out.println("Failed to verification your identity. ");
                    } catch (RemoteException e) {
                        System.out.println("Failed! " + e.getMessage());
                    } catch (IllegalBlockSizeException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        return;
                    }
                    break;
                default:
                    return;
            }
            System.out.println("Press any key to go back to main menu");
            s.nextLine();
            Runtime.getRuntime().exec("clear");
        }
    }
}

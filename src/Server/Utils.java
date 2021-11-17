package Server;

import javax.crypto.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.*;
import java.util.Arrays;

public class Utils {
    public static byte[] encrypt(byte[] bytes, Key key) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public static byte[] encrypt(String str, Key key) throws Exception {
        return encrypt(str.getBytes(StandardCharsets.UTF_8), key);
    }

    public static byte[] decrypt(byte[] bytes, Key key) throws InvalidKeyException, Exception{
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(bytes);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public static byte[] decrypt(String str, Key key) throws InvalidKeyException, Exception {
        return decrypt(str.getBytes(StandardCharsets.UTF_8), key);
    }

    public static Key checkAuctionAndSeller(int auctionId, String sellerUsername, byte[] sellerKey)
            throws RemoteException, InvalidKeyException {

        if ((!MainServer.items.containsKey(auctionId))) {
            throw new RemoteException("Invalid auction");
        }
        Key privateKey = checkUsers(sellerUsername, sellerKey);
        if (!MainServer.items.get(auctionId).getSellerUsername().equals(sellerUsername)){
            throw new RemoteException("Auction not belongs to the seller");
        }
        return privateKey;
    }

    public static Key checkUsers(String username, byte[] userKey) throws RemoteException, InvalidKeyException {

        if (!MainServer.users.containsKey(username)) {
            throw new RemoteException("Invalid user");
        }
        Key privateKey = MainServer.users.get(username);
        try {
            if (!Arrays.equals(username.getBytes(StandardCharsets.UTF_8), Utils.decrypt(userKey, privateKey))) {
                throw new InvalidKeyException("Invalid Key");
            }
        } catch (Exception e) {
            throw new InternalError(e);
        }
        return privateKey;
    }

    public static Key checkUsers(String username) throws RemoteException {
        if (!MainServer.users.containsKey(username)) {
            throw new RemoteException("Not a valid user");
        }
        return MainServer.users.get(username);
    }

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
        SealedObject obj1 = new SealedObject("Hello", cipher);
//        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
        System.out.println(obj1.getObject(keyPair.getPublic()));
//
//        System.out.println((String[]) null);

    }
}

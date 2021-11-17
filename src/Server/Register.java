package Server;

import Prototype.IRegister;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

import javax.crypto.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;

public class Register extends UnicastRemoteObject implements IRegister {
    private final HashMap<String, byte[]> clientChallenge = new HashMap<>();

    protected Register() throws RemoteException {
    }

    public synchronized Key register(String userName) throws Exception {
        if (MainServer.users.containsKey(userName)) {
            throw new RemoteException("Existing user");
        }
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.genKeyPair();
            MainServer.users.put(userName, keyPair.getPrivate());
            return keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e);
        }
    }

    @Override
    public byte[] proveServer(byte[] challenge) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, MainServer.key);
        return cipher.doFinal(challenge);
    }

    @Override
    public byte[] proveClient(String username) throws RemoteException{
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        clientChallenge.put(username, bytes);
        return bytes;
    }

    @Override
    public boolean clientChallengeBack(String username, byte[] encryptedChallenge) throws RemoteException{
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, MainServer.key);
            if (!clientChallenge.containsKey(username)) {
                return false;
            }
            if (Arrays.equals(clientChallenge.get(username), cipher.doFinal(encryptedChallenge))) {
                MainServer.authorizedUsers.add(username);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public SealedObject getKey(String username) throws Exception {
        Key privateKey = Utils.checkUsers(username);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key sessionKey = keyGen.generateKey();
        MainServer.users.put(username, sessionKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return new SealedObject(sessionKey, cipher);
    }
}

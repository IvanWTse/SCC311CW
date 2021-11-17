package Server;

import Prototype.IRegister;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Register extends UnicastRemoteObject implements IRegister {
    protected Register() throws RemoteException {
    }

    public synchronized Key register(String userName) throws Exception {
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

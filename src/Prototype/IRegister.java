package Prototype;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Key;

public interface IRegister extends Remote {
    Key register(String username) throws Exception;

    byte[] proveServer(byte[] challenge) throws Exception;

    byte[] proveClient(String username) throws RemoteException;

    boolean clientChallengeBack(String user, byte[] encryptedChallenge) throws RemoteException;

    SealedObject getKey(String username) throws Exception;
}

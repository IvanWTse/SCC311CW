package Prototype;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.security.Key;

public interface IRegister extends Remote {
    Key register(String username) throws Exception;

    byte[] proveServer(byte[] challenge) throws Exception;

    byte[] proveClient(String username);

    boolean clientChallengeBack(String user, byte[] encryptedChallenge);

    SealedObject getKey(String username) throws Exception;
}

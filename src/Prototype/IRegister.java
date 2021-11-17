package Prototype;

import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.security.Key;

public interface IRegister extends Remote {
    Key register(String username) throws Exception;

    SealedObject getKey(String username) throws Exception;
}

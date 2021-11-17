import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PreSharedKey {
    public static Key getKey() {
        try (FileInputStream fis = new FileInputStream("secretKey")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Key) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new PreSharedKey().genKey();
    }

    private Key genKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        try (FileOutputStream fos = new FileOutputStream("secretKey")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(secretKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return secretKey;
    }
}

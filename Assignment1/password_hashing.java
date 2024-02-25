import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class password_hashing
{
    public static String password_hashing(String password) throws NoSuchAlgorithmException
    {
        /* Password hashing using Message Digest-5 Algorithm */
        MessageDigest md =  MessageDigest.getInstance("MD5");
        byte[] pass_byte = password.getBytes();
        /* Updating md object */
        md.update(pass_byte);
        byte[] hash_pass = md.digest();
        /* Processing password in 16 word block */
        BigInteger bigInt = new BigInteger(1,hash_pass);
        return bigInt.toString(16);
    }
}

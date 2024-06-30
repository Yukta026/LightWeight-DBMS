import java.util.UUID;
public class generateToken
{
    public static String generateToken(String inputId)
    {
        /* Random token generation using UUID */
        UUID token = UUID.randomUUID();
        return token.toString();
    }
}

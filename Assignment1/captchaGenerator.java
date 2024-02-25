import java.util.Random;
public class captchaGenerator
{
    public static String captchaGenerator(int size)
    {
        /* Random number generation for random captcha generation */
        Random number = new Random();
        StringBuilder captcha = new StringBuilder();
        String input = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for(int i =0;i<size;i++)
        {
            int index = number.nextInt(input.length());
            captcha.append(input.charAt(index));
        }
        /* Returns randomly generated captcha */
        return captcha.toString();
    }
}

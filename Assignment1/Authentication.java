import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import static java.lang.System.currentTimeMillis;

public class Authentication extends Query
{
    public void Authentication() throws NoSuchAlgorithmException, IOException
    {
        /* Object instantiation */
        Scanner sc = new Scanner(System.in);
        password_hashing password_hashing = new password_hashing();
        generateToken generateToken = new generateToken();
        captchaGenerator captchaGenerator = new captchaGenerator();

        Map<String, String> users = new HashMap<>();
        Map<String, List<String>> user_log;
        List<String> log = new ArrayList<>();

        /* Hardcoded user credentials in (user id, hashed password format) */
        users.put("1", password_hashing.password_hashing("1234"));
        users.put("2", password_hashing.password_hashing("abcd"));
        final String user_log_file = "user_log.txt";

        /* Setting length of captcha */
        int size = 5;

        /* Two - factor Authentication */
        System.out.println("Welcome to light-weight DBMS Prototype!!");
        System.out.println("Enter Credentials");
        user_log = new HashMap<>();
        System.out.print("Enter ID:");
        String input_id = sc.next();
        if (!users.containsKey(input_id))
        {
            System.out.println("Invalid Credentials");
            return;
        }
        System.out.print("Enter Password:");
        String input_pass = sc.next();

        /* Matches hashed user input password with stored password */
        String hash_pass = password_hashing.password_hashing(input_pass);
        if (!users.get(input_id).equals(hash_pass)) {
            System.out.println("Invalid Password!");
            return;
        }

        /* Verification of valid captcha input */
        boolean cap_match = false;
        while (!cap_match) {
            String captcha = captchaGenerator.captchaGenerator(size);
            System.out.print("Enter Captcha: " + captcha + " ");
            String input_captcha = sc.next();
            if (!captcha.equals(input_captcha)) {
                System.out.println("Re-enter Captcha!");
            } else {
                cap_match = true;
            }
        }
        System.out.println("Authentication Success!");

        /* Token generation on successful authentication */
        String token = generateToken.generateToken(input_id);

        /* Timestamp generation */
        long milliseconds = currentTimeMillis();
        Date date = new Date(milliseconds);
        String timestamp = date.toString();
        System.out.println(timestamp);
        log.add(token);
        log.add(timestamp);

        /* UserId,Session Token,Timestamp addition to user_log.txt file */
        user_log.put(input_id, log);
        System.out.println("Session token: " + token);
        System.out.println(user_log);

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(user_log_file, true)))
        {
            for (Map.Entry<String, List<String>> data : user_log.entrySet()) {
                String record = data.getKey() + "|" + data.getValue() + "\n";
                bf.write(record);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

       /* Query Execution */
        boolean execution = true;
        while (execution) {
            System.out.println("Enter SQL Query or type 'quit' to quit: ");
            String input_query = sc.nextLine();
            if (input_query.equalsIgnoreCase("quit")) {
                execution = false;
                return;
            }
            List user_data = executeQuery(input_query);

        }
        System.out.println(user_log);
    }
}

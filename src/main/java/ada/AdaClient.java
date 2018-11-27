package ada;

import java.sql.Time;
import java.util.Optional;
import java.util.Scanner;
import java.sql.Timestamp;
import org.json.*;


/**
 * {@code ada.AdaClient} reads messages from and sends Messages to {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient {

    private static final int PORT = 6259;

    public static void main(String[] args) {
        NetworkSocketClient client = new NetworkSocketClient("localhost", PORT);
        NetworkSender sender = new NetworkSender(client);
        NetworkReader reader = new NetworkReader(client);
        Scanner input = new Scanner(System.in);

        /* DB: log user if new */
        String username = null;
        String answer = null;
        String flag = null;

        /** DB: known bug - multiple logins same person allowed
         * this is not fully a bug because no two people can have
         * the same username, so someone would have to lie and say
         * that they do have an account when the really do not and
         * then use an existing username as their own
         * reach goal: implement authorization mechanism
         */
        while (true) {
            System.out.print("Do you already have an account (y/n):  ");
            answer = input.nextLine();
            if (answer.equals("n")) {
                flag = "n";
                System.out.print("Please enter a username: ");
                username = input.nextLine();
                new PostgreSQL_createUser();
                Boolean ret = PostgreSQL_createUser.main(username, flag);
                if (ret.equals(false)) {
                    System.out.println("please try again!");
                } else {
                    System.out.println("user created in database!");
                    break;
                }
            } else if (answer.equals("y")) {
                flag = "y";
                System.out.print("Please enter *your* username: ");
                /* check if in system */
                username = input.nextLine();
                new PostgreSQL_createUser();
                Boolean ret = PostgreSQL_createUser.checkUser(username, flag);
                if (ret.equals(true)) {
                    System.out.println("username validated");
                    break;
                } else {
                    System.out.println("username not in system, try again");
                }
            } else {
                System.out.println("incorrect selection, please try again!");
            }
        }

        Thread sendMessages =
                new Thread(
                        () -> {
                            while (!Thread.interrupted()) {
                                if (input.hasNext()) {
                                    sender.SendMessage(input.nextLine());
                                } else {
                                    input.nextLine();
                                }
                            }
                        });
        sendMessages.start();
        sender.SendMessage("\\username " + username);
        Optional<String> s;
        do {
            s = reader.ReadMessage();
            if (s.isPresent()) {
                JSONObject jobj = new JSONObject(s.get());
                String parsedSender = jobj.getString("sender");
                String parsedMsg = jobj.getString("msg");

                /* DB: SQL insertion */
                new PostgreSQL_insertChat();
                PostgreSQL_insertChat.main(jobj, username);

                System.out.println(parsedSender + ": " + parsedMsg);
                if (parsedMsg.equals("exit")) {
                    break;
                }
            }
        } while (true);

        System.out.println("closing out");

        /* clean exit */
        sender.Close();
        reader.Close();
        client.Close();

        try {
            sendMessages.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings("InfiniteLoopStatement")
public class Client {

    public void start() {
        try {
            Socket socket = new Socket("127.0.0.1", 12000);
            System.out.println("Connected to server.");
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            Scanner scan = new Scanner(System.in);
            while (true) {
                writer.println(scan.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

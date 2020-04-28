import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("InfiniteLoopStatement")
public class Server {
    private BufferedReader reader;
    private Socket socket;

    public void start() {
        try {
            ServerSocket server = new ServerSocket(12000);
            System.out.println("Server is activated");

            while (true) {
                socket = server.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (socket != null) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getMessage() {
        try {
            while (true) {
                System.out.println("Client: " + reader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

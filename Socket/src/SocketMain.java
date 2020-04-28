import java.net.InetAddress;

public class SocketMain {

    public static void main(String[] args) {
        InetAddress address = null;

        try {
            address = InetAddress.getByName("www.google.com");
            System.out.println("Host name: " + address.getHostName());
            System.out.println("Host address: " + address.getHostAddress());
            System.out.println("My address: " + InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

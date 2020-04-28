import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

public class PacketMain {

    public static void main(String[] args) {
        ArrayList<PcapIf> alldevs = new ArrayList<PcapIf>();
        StringBuilder errbuf = new StringBuilder();

        int stat = Pcap.findAllDevs(alldevs, errbuf);
        if (stat != Pcap.OK || alldevs.isEmpty()) {
            System.out.println("Cannot find network devices. " + errbuf.toString());
            return;
        }
        System.out.println("Succeeded in finding network devices.");

        try {
            for (final PcapIf i : alldevs) {
                final byte[] address = i.getHardwareAddress();
                if (address == null) continue;
                System.out.println(
                    "Device address: " + i.getName() + ", Mac address: " + macString(address)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = 0;
        for (PcapIf device : alldevs) {
            String desc = (device.getDescription() != null)
                ? device.getDescription()
                : "No explanation on device";
            System.out.printf("[%2d]: %s [%s]\n", i++, device.getName(), desc);
        }

        PcapIf device = alldevs.get(6);
        String desc = device.getDescription();
        String select = (desc != null) ? desc : device.getName();
        System.out.println("Selected device: " + select);

        int snapLen = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeout = 2 * 1000;

        Pcap pcap = Pcap.openLive(device.getName(), snapLen, flags, timeout, errbuf);
        if (pcap == null) {
            System.out.println("Failed to open the network device. " + errbuf.toString());
            return;
        }

        // sendPacket(pcap);
        // pcapHeaderPrint(pcap);
        // pcapPacketLoop(pcap, 10);

        pcap.close();
    }

    public static void sendPacket(Pcap pcap) {
        byte[] bytes = new byte[14];
        Arrays.fill(bytes, (byte) 0xff);

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        if (pcap.sendPacket(byteBuffer) != Pcap.OK) System.out.println(pcap.getErr());

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) stringBuilder.append(String.format("%02x ", b & 0xff));
        System.out.println("Transferred Packet: " + stringBuilder.toString());
    }

    public static void pcapHeaderPrint(Pcap pcap) {
        Ethernet ethernet = new Ethernet();
        Ip4 ip4 = new Ip4();
        Tcp tcp = new Tcp();
        Payload payload = new Payload();
        PcapHeader pcapHeader = new PcapHeader(JMemory.POINTER);
        JBuffer jBuffer = new JBuffer(JMemory.POINTER);
        int id = JRegistry.mapDLTToId(pcap.datalink());

        while (pcap.nextEx(pcapHeader, jBuffer) != Pcap.NEXT_EX_NOT_OK) {
            PcapPacket pcapPacket = new PcapPacket(pcapHeader, jBuffer);
            pcapPacket.scan(id);

            System.out.printf("[ #%2d ]\n", pcapPacket.getFrameNumber());
            if (pcapPacket.hasHeader(ethernet)) {
                System.out.println("Source MAC: " + FormatUtils.mac(ethernet.source()));
                System.out.println("Destination MAC: " + FormatUtils.mac(ethernet.destination()));
            }
            if (pcapPacket.hasHeader(ip4)) {
                System.out.println("Source IP: " + FormatUtils.ip(ip4.source()));
                System.out.println("Destination IP: " + FormatUtils.ip(ip4.destination()));
            }
            if (pcapPacket.hasHeader(tcp)) {
                System.out.println("Source TCP: " + tcp.source());
                System.out.println("Destination TCP: " + tcp.destination());
            }
            if (pcapPacket.hasHeader(payload)) {
                System.out.println("Payload length: " + payload.getLength());
                System.out.println(payload.toHexdump());
            }
        }
    }

    public static void pcapPacketLoop(Pcap pcap, int loop) {
        PcapPacketHandler<String> packetHandler = new PcapPacketHandler<String>() {

            @Override
            public void nextPacket(PcapPacket pcapPacket, String s) {
                Date date = new Date(pcapPacket.getCaptureHeader().timestampInMillis());
                System.out.println("Start capturing: " + date);
                System.out.println("Length of packet: " + pcapPacket.getCaptureHeader().caplen());
            }
        };

        pcap.loop(10, packetHandler, "jNetPcap");
    }

    public static String macString(final byte[] address) {
        final StringBuilder strbuf = new StringBuilder();
        for (byte b : address) {
            if (strbuf.length() != 0) strbuf.append(":");
            if (b >= 0 && b < 16) strbuf.append('0');
            strbuf.append((Integer.toHexString((b < 0) ? b + 256 : b).toUpperCase()));
        }
        return strbuf.toString();
    }
}

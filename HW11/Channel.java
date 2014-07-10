import java.util.*;
import java.net.*;
import java.io.*;
public class Channel {
	public static void main(String[] args) throws IOException, UnknownHostException {
		try {
			double v = Double.parseDouble(prompt("What is the end-to-end propagation delay (in seconds) over the channel?"));
			int numHosts = Integer.parseInt(prompt("How many hosts are there on this channel?"));
			HostObject[] hosts = new HostObject[numHosts];
			int count = 0;
			DatagramSocket socket = new DatagramSocket(5678);
			while (count < numHosts - 1) {
				DatagramPacket packet = receive(socket);
				String message = new String(packet.getData(), 0, packet.getLength());
				InetAddress ip = packet.getAddress();
				int port = packet.getPort();
				long time = System.currentTimeMillis();
				if (message.equals("SIMSTART")) {
					hosts[count++] = new HostObject(ip.toString(), port);
					System.out.println("SIMSTART");
					//message + " " + ip.toString() + )
				}
			}
			for (int i = 0; i < numHosts; i++) {
				send(socket, hosts[i].hostIP, hosts[i].port, "YESSS");
			}
			while (true) {
				DatagramPacket packet = receive(socket);
				String message = new String(packet.getData(), 0, packet.getLength());
				InetAddress ip = packet.getAddress();
				int port = packet.getPort();
				long t_now = System.currentTimeMillis();
				if (message.equals("IDLE")) {
					for (int i = 0; i < numHosts; i++) {
 						long t_start = hosts[i].t_start;
 						long t_stop = hosts[i].t_stop;
 						
 						send(socket, hosts[i].hostIP, port,(t_start < 0 || 
 						(((t_start + 1000*v) > t_now) && 
 						((t_stop < 0) || 
 						((t_stop + 1000*v) < t_now))) || 
 						((t_start <= t_stop) && 
 						((t_stop + 1000*v) <= t_now))
 						) ? "YES" : "NO");
					}
				} else if (message.equals("COLIDE")) {
					for (int i = 0; i < numHosts; i++) {
						
 						long t_start = hosts[i].t_start;
 						long t_stop = hosts[i].t_stop;

 						send(socket, hosts[i].hostIP, port,(t_start < 0 || 
 						(((t_start + 1000*v) > t_now) && 
 						((t_stop < 0) || 
 						((t_stop + 1000*v) < t_now))) || 
 						((t_start <= t_stop) && 
 						((t_stop + 1000*v) <= t_now))
 						) ? "NO" : "YES");
					}
				} else if (message.equals("START")) {
					boolean found = false;
					for (int i = 0; i < numHosts; i++) {
						if (hosts[i].hostIP == ip.toString()) {
							found = true;
							hosts[i].t_start = t_now;
						}
					}
					if (!found) {
						hosts[0] = new HostObject(ip.toString(),port);
						hosts[0].t_start = t_now;
					}
				} else if (message.equals("DONE") || message.equals("ABORT")) {
					boolean found = false;
					for (int i = 0; i < numHosts; i++) {
						if (hosts[i].hostIP == ip.toString()) {
							found = true;
							hosts[i].t_stop = t_now;
						}
					}
					if (!found) {
						hosts[0] = new HostObject(ip.toString(),port);
						hosts[0].t_stop = t_now;
					}
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid number for these questions");
			System.exit(1);
		} catch (SocketException e) {
			System.out.println("Socket error");
			System.exit(1);
		}
	}

	public static void send(DatagramSocket socket, String ip, int port, String message) throws UnknownHostException {
		DatagramPacket packet = new DatagramPacket(message.getBytes(),message.length(),InetAddress.getByName(ip),port);
	}

	public static DatagramPacket receive(DatagramSocket socket) throws IOException {
		boolean more = true;
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        return packet;
	}

	public static String prompt(String msg) {
		try {
            System.out.print(msg+" ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String result = in.readLine();
            System.out.println();
			return result;
        } catch (IOException e) {
            System.out.println("ERROR: IO Exception");
        }
        return null;
    }

    public static class HostObject {
    	public String hostIP;
    	public int port;
    	public long t_start;
    	public long t_stop;
    	public HostObject() {
    		hostIP = null;
    		port = 0;
    		t_start = -1;
    		t_stop = -1;
    	}
    	public HostObject(String ip, int p) {
    		hostIP = ip;
    		port = p;
    		t_start = -1;
    		t_stop = -1;

    	}

    	public boolean equals(Object o) {
    		return hostIP.equals(((HostObject)o).hostIP);
    	}
    }
}
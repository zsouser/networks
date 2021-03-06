/*
 * Server App upon UDP
 * Weiying Zhu
 */ 
 
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException {
	 
	DatagramSocket udpServerSocket = null;
        BufferedReader in = null;
	DatagramPacket udpPacket = null, udpPacket2 = null;
	String fromClient = null, toClient = null;
        boolean morePackets = true;

	byte[] buf = new byte[256];
	HashMap<String,String> choices = new HashMap<String,String>();
	choices.put("00001", "New Inspiron 15\t\t$379.99\t\t157");
        choices.put("00002", "New Inspiron 17\t\t$449.99\t\t128");
        choices.put("00003", "New Inspiron 15R\t\t$549.99\t\t202");
	choices.put("00004", "New Inspiron 15z Ultrabook\t\t$749.99\t\t315");
        choices.put("00005", "XPS 14 Ultrabook\t\t$999.99\t\t261");
        choices.put("00006", "New XPS 12 UltrabookXPS\t\t$1199.99\t\t178");
	
	udpServerSocket = new DatagramSocket(5678);
	
	while (morePackets) {
	    try {
		
		// receive UDP packet from client
		udpPacket = new DatagramPacket(buf, buf.length);
		udpServerSocket.receive(udpPacket);
		
		fromClient = new String(udpPacket.getData(), 0, udpPacket.getLength());
		
		if (choices.containsKey(fromClient)) {
		    toClient = choices.get(fromClient);
		    
		    // send the response to the client at "address" and "port"
		    InetAddress address = udpPacket.getAddress();
		    int port = udpPacket.getPort();
		    byte[] buf2 = toClient.getBytes();
		    udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
		    udpServerSocket.send(udpPacket2);
		    
		} 
	    } catch (IOException e) {
		e.printStackTrace();
		morePackets = false;
		
	    }
	    udpServerSocket.close();
	    
	}
    }
}
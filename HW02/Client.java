import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
	    System.out.println("Usage: java Client <hostname>");
	    return;
        }

	// creat a UDP socket
        DatagramSocket udpSocket = new DatagramSocket();

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;
	HashMap<String,String> choices = new HashMap<String,String>();
	choices.put("00001", "New Inspiron 15");
	choices.put("00002", "New Inspiron 17");
	choices.put("00003", "New Inspiron 15R");
	choices.put("00004", "New Inspiron 15z Ultrabook");
	choices.put("00005", "XPS 14 Ultrabook");
	choices.put("00006", "New XPS 12 UltrabookXPS");

	
	while(true) {
	    displayMenu(choices);
	    
	    fromUser = sysIn.readLine();
	    if (fromUser == null || fromUser.equals("exit"))
		break;
	    //display user input
	    if (!choices.containsKey(fromUser)) {
		System.out.println("Invalid input! Please enter one of the chioces, with leading zeroes");
		continue;
	    }
		
	    // send request
	    InetAddress address = InetAddress.getByName(args[0]);
	    byte[] buf = fromUser.getBytes();
	    DatagramPacket udpPacket = new DatagramPacket(buf, buf.length, address, 5678);
	    udpSocket.send(udpPacket);
	    
	    // get response
	    byte[] buf2 = new byte[256];
	    DatagramPacket udpPacket2 = new DatagramPacket(buf2, buf2.length);
	    udpSocket.receive(udpPacket2);
	    
	    // display response
	    fromServer = new String(udpPacket2.getData(), 0, udpPacket2.getLength());
	    System.out.println("Item ID\t\tItem Description\t\tUnit Price\t\tInventory");
	    System.out.println(fromServer);
	}
	  
        udpSocket.close();
    }

    public static void displayMenu(HashMap<String,String> choices) {
	System.out.println("Item ID\t\tItem Description");
	for (String key : choices.keySet()) {
	    System.out.println(key + "\t\t" + choices.get(key));
	}
	System.out.println("Please enter an ID (with leading zeroes) to view product info, or 'exit' to quit");
    }
}
import java.net.*;
import java.io.*;
import java.util.*;
public class ServerThread extends Thread {
    private Socket sock = null;

    public ServerThread(Socket socket) {
	super("Server Thread");
	this.sock = socket;
    }

    public void run() {
	try {
	    PrintWriter out = new PrintWriter(this.sock.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));

	      
	    String line = in.readLine();
	    String[] parts = line.split(" ");
	    BufferedReader file = null;

	    
	    if (parts[0].equals("GET")) {
		try {
		    file = new BufferedReader(new InputStreamReader(new FileInputStream(parts[1].substring(1))));
		    out.print("200 OK\r\n");
		} catch (IOException e) {
		    out.print("404 File Not Found\r\n");
		} 
	    } else {
		out.print("400 Bad Request\r\n");
	    }

	    out.print("Date: " + (new Date()).toString() + "\r\n");
	    out.print("Server: Zach Souser\r\n");
	    out.print("\r\n");
	    String fileLine = null;
	    while (file != null && (fileLine = file.readLine()) != null) out.println(fileLine);
	    out.print("\r\n\r\n\r\n\r\n");
	    out.close();
	    in.close();
	    sock.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
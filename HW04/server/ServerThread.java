import java.net.*;
import java.io.*;
import java.util.*;
public class ServerThread extends Thread {
    private Socket sock = null;
    private static String local = null, remote = null;
    public ServerThread(Socket socket) {
	super("Server Thread");
	this.sock = socket;
    }

    public void run() {
	try {
	    PrintWriter out = new PrintWriter(this.sock.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
	    local = InetAddress.getLocalHost().getHostAddress().toString();
	    remote = this.sock.getInetAddress().getHostAddress().toString();
	    out.println("220 " + local);	    
	    
	    while(helo(in,out) && mailFrom(in,out) && rcptTo(in,out) && data(in,out));

	    out.println("221 " + local + " closing connection");
	    out.close();
	    in.close();
	    this.sock.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean helo(BufferedReader in, PrintWriter out) throws IOException {
	String[] words = in.readLine().split(" ");
	if (words[0].toUpperCase().equals("QUIT")) return false;
	if (words[0].toUpperCase().equals("HELO")) {
	    out.println("250 " + local + " Hello " + remote);
	    return true;
	}
	out.println("503 5.5.2 Send hello first");
	return helo(in,out);
	
    }

    public boolean mailFrom(BufferedReader in, PrintWriter out) throws IOException {
	String[] words = in.readLine().split(" ");
	if (words[0].toUpperCase().equals("QUIT")) return false;
	if (words.length > 1 && words[0].toUpperCase().equals("MAIL") && words[1].toUpperCase().equals("FROM")) {
	    out.println("250 2.1.0 Sender OK");
	    return true;
	} 
	out.println("503 5.5.2 Need mail command");
	return mailFrom(in,out);
    }

    public boolean rcptTo(BufferedReader in, PrintWriter out) throws IOException {
	String[] words = in.readLine().split(" ");
	if (words[0].toUpperCase().equals("QUIT")) return false;
	if (words.length > 1 && words[0].toUpperCase().equals("RCPT") && words[1].toUpperCase().equals("TO")) {
	    out.println("250 2.1.5 Recipient OK");
	    return true;
	}
	out.println("503 5.5.2 Need rcpt command");
	return rcptTo(in,out);
    }

    public boolean data(BufferedReader in, PrintWriter out) throws IOException {
	String line = in.readLine();
	if (line.equals("QUIT")) return false;
	if (line.equals("DATA")) {
	    out.println("354 Start mail input; end with <CRLF>.<CRLF>");
	    
	    do {
		line = in.readLine();
	    } while (!line.equals("."));
	    out.println("250 Message received and to be delivered");
	    return true;
	} else {
	    out.println("503 5.5.2 Need data command");
	    return data(in,out);
	}
    }
}
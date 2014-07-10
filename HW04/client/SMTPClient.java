import java.io.*;
import java.net.*;

public class SMTPClient {
    public static String local;
    public static void main(String[] args) throws IOException, UnknownHostException {
	local = InetAddress.getLocalHost().getHostAddress().toString(); 
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	try {
	    String host = prompt("What is the host you would like to reach?");
	    socket = getSocket(host);
	    out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    System.out.println(in.readLine());
	    boolean quit = false;
	    do {
		long time = System.currentTimeMillis();
		
		request(in,out, 
			prompt("Please enter a sender email address"),
			prompt("Please enter a receiver email address"),
			prompt("Please enter a subject"),
			getBody("Please enter the message body, ending with . on a blank line")
			);
		System.out.println("\n\n\n");
		System.out.println("Total RTT: " + (System.currentTimeMillis() - time));
		String quitPrompt = prompt("Would you like to continue?");
		quit = quitPrompt == null || !quitPrompt.isEmpty() && quitPrompt.toLowerCase().charAt(0) != 'y';
	    } while (!quit);
	    
	    out.println("QUIT");
	    System.out.println(in.readLine());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        } finally {
	    in.close();
	    out.close();
	    socket.close();
	}
    }


    public static void request(BufferedReader in, PrintWriter out, String sender, String recipient, String subject, String body) throws IOException {
	out.println("HELO " + local);
	System.out.println(in.readLine());
	out.println("MAIL FROM <" + sender + ">");
	System.out.println(in.readLine());
	out.println("RCPT TO <" + recipient + ">");
	System.out.println(in.readLine());
	out.println("DATA");
	System.out.println(in.readLine());

	String[] lines = body.split("\n");
	out.println("To: " + recipient);
	System.out.println("To: " + recipient);
	out.println("From: " + sender);
	System.out.println("From: " + sender);
	out.println("Subject: " + subject);	
	System.out.println("Subject: " + subject);

	out.println();
	System.out.println();
	for (String s : lines) {
	    out.println(s);
	    System.out.println(s);
	}
	if (!lines[lines.length - 1].equals(".")) {
	    out.println(".");
	    System.out.println(".");
	}
	System.out.println(in.readLine());
	
    }

    public static String prompt(String question) throws IOException {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	System.out.print(question + ": ");
	return in.readLine();
    }

    public static String getBody(String question) throws IOException {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	System.out.println(question + ": ");
	String line = null;
	String msg = "";
	do {
	    line = in.readLine();
	    msg = msg + line;
	} while (!line.equals("."));
	return msg;
    }

    public static Socket getSocket(String host) throws UnknownHostException, IOException {
	long time = System.currentTimeMillis();
	Socket socket = new Socket(host, 4567);
	time = System.currentTimeMillis() - time;
	System.out.println("RTT (Establishing Connection) " + time + "ms");
	return socket;
    }
}
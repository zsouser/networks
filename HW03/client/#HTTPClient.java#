import java.io.*;
import java.net.*;

public class HTTPClient {
    public static String filename = "default";
    public static void main(String[] args) throws IOException {
        Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	try {
	    String host = prompt("What is the host you would like to reach?");
	    socket = getSocket(host);
	    out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    do {
		long time = System.currentTimeMillis();
		out.println(formRequest(host, 
					prompt("Please enter a request Method"),
					prompt("Please enter a file name"),
					prompt("Please enter a HTTP version"),
					prompt("Please enter a User Agent")
					));
		System.out.println("\n\n\n");
		handleResponse(in);
		System.out.println("\n\n\n");
		System.out.println("Total RTT: " + (System.currentTimeMillis() - time));
	    } while (prompt("Would you like to continue?").toLowerCase().charAt(0) == 'y');
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

    public static void handleResponse(BufferedReader in) throws FileNotFoundException, IOException {
	String line = in.readLine();
	System.out.println(line);
	int emptyCount = 0;
	boolean ok = line.equals("200 OK");
	if (!ok) System.out.println("ERROR: " + line);
	boolean isHeader = true, done = false;
	PrintWriter fileOut = new PrintWriter(new File(filename));
	while (!done && (line = in.readLine()) != null) {
	    if (line.isEmpty()) emptyCount++;
	    else emptyCount = 0;
	    if (isHeader && emptyCount == 1) isHeader = false;
	    if (!isHeader && emptyCount == 4) done = true;
	    if (isHeader) System.out.println(line);
	    if (!isHeader && ok) fileOut.println(line);
	}
	fileOut.close();
    }

    public static String formRequest(String host, String method, String file, String version, String agent) {
	String request = method + " /" + file + " HTTP/" + version + "\r\n";
	request += "Host: " + host + " \r\n";
	request += "User-Agent: " + agent + "\r\n";
	request += "\r\n";
	filename = file;
	return request;
    }

    public static String prompt(String question) throws IOException {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	System.out.print(question + ": ");
	return in.readLine();
    }

    public static Socket getSocket(String host) throws UnknownHostException, IOException {
	long time = System.currentTimeMillis();
	Socket socket = new Socket(host, 4567);
	time = System.currentTimeMillis() - time;
	System.out.println("RTT (Establishing Connection) " + time + "ms");
	return socket;
    }
}
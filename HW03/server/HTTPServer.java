import java.net.*;
import java.io.*;

public class HTTPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(4567);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4567.");
            System.exit(-1);
        }

        while (listening){
	    new ServerThread(serverSocket.accept()).start();
	}
	
        serverSocket.close();
    }
}
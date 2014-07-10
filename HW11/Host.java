import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
class Host {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Please enter the IP of the channel as an argument.");
			System.exit(1);
		}
		Host h = new Host(args[0]);

	}
	DatagramSocket socket;
	InetAddress address;
	int collisions;
	Timer timer;
	double leftover, x, y;
	public Host(String host) throws NumberFormatException {
		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName(host);
			this.x = Double.parseDouble(prompt("How many seconds from now until this frame is ready to be sent out?"));
			this.y = Double.parseDouble(prompt("How long does it take for the host ot transmit this frame entirely? (in seconds)"));
			this.simStart();
			this.collisions = 0;
			timer = new Timer();
			timer.schedule(new SensingTask(),1000L);
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid number for these questions");
			System.exit(1);
		} catch (SocketException e) {
			System.out.println("Socket error");
			System.exit(1);
		} catch (UnknownHostException e) {
			System.out.println("Host unkonwn");
			System.exit(1);
		}
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

	public void simStart() {
		if (sendPacket("IDLE").equals("YESSS")) {
			System.out.println("YESSS");
		} else {
			System.out.println("ERROR");
			System.exit(1);
		}
	}

	public String sendPacket(String data) {
		try {
			DatagramPacket packet = new DatagramPacket(data.getBytes(),data.length(),this.address,5678);
			socket.send(packet);
			byte[] buffer = new byte[256];
			DatagramPacket received = new DatagramPacket(buffer, buffer.length);
			socket.receive(received);
			return new String(received.getData());
		} catch (IOException e) {
			System.out.println("ERROR: IO Exception");
			System.exit(1);
		} 
		return null;
	}

	private class SensingTask extends TimerTask {
		public void run() {
			System.out.println("NIC senses channel to see whether the channel is idle. Current time is "+System.currentTimeMillis());
			String response = sendPacket("IDLE");
			if (response.equals("NO")) {
				timer.schedule(this, 1000L);
			} else if (response.equals("YES")) {
				leftover = y;
				sendPacket("START");
				System.out.println("NIC starts transmitting a frame. Current time is " + System.currentTimeMillis() + ". The leftover transmission duration is " + y);
				timer.schedule(new CollisionTask(),1000L);
			}
		}
	}

	private class CollisionTask extends TimerTask {
		public void run() {
			System.out.println("NIC detects collision on channel. Current time is "+System.currentTimeMillis());
			String response = sendPacket("COLIDE");
			if (response.equals("NO")) {
				if (leftover == 0) {
					sendPacket("DONE");
					System.out.println("Done with transmitting this frame! Current time is " + System.currentTimeMillis());
					System.exit(1);
				} else if (leftover > 0) {
					leftover -= Math.min(1,leftover);
					System.out.println("NIC is still transmitting. The leftover-transmission-duration is " + leftover + "sec. Current time is " + System.currentTimeMillis());
					timer.schedule(this,(long)(leftover*1000));
				}
			} else if (response.equals("YES")) {
				collisions++;
				Random random = new Random();
				int k = (int)Math.pow(2,random.nextInt(collisions - 1));
				System.out.println("NIC detects a collision and aborts transmitting the frame and will sense the channel for re-transmission " + (k * y) + " seconds later. Local time is " + System.currentTimeMillis());
				sendPacket("ABORT");
				timer.schedule(new SensingTask(), (long)(k * y * 1000));

			}
		}
	}
}
package gambyt.monitor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Heartbeat extends Thread {
	private String self, monip;

	public Heartbeat(String self, String monip) {
		this.self = self;
		this.monip = monip;
	}

	public void run() {
		try {
			DatagramSocket ds = new DatagramSocket();
			InetAddress server = InetAddress.getByName(monip);
			int port = 1100;
			while (true) {
				String payload = self;
				byte[] plb = payload.getBytes();
				DatagramPacket p = new DatagramPacket(plb, plb.length, server, port);
				ds.send(p);
				sleep(10000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

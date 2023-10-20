package gambyt.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Monitor {

	public static void main(String[] args) throws SocketException {
		DatagramSocket ds = new DatagramSocket(1100);
		byte[] buf = new byte[1000];
		while(true) {
			try {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				ds.receive(dp);
				System.out.println(new String(buf));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

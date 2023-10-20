package gambyt.proxy.controllers;

import java.util.concurrent.ArrayBlockingQueue;

public class QueueThread extends Thread {
	private ArrayBlockingQueue<Request> Q;

	public QueueThread(ArrayBlockingQueue<Request> Q) {
		this.Q = Q;
	}

	public void run() {
		while (true) {
			try {
				if (Q.isEmpty())
					System.out.println("[QueueThread] Waiting for new requests");
				Request r = Q.take();
				r.fulfil();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

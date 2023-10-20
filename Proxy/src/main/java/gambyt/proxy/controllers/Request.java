package gambyt.proxy.controllers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import gambyt.backend.RemoteFrontend;
import gambyt.proxy.ServerNotFoundException;

public class Request<R> implements RemoteFuture<R> {
	private static ArrayList<RemoteFrontend> INSTANCES;
	private static ArrayList<String> IPS;

	private final CountDownLatch latch = new CountDownLatch(1);
	private RemoteFunction<R> sf;
	private R value;
	private RemoteException re;

	private RemoteFrontend first;
	private boolean writeop;

	public static void InitBroadcast(ArrayList<RemoteFrontend> INSTANCES, ArrayList<String> IPS) {
		Request.INSTANCES = INSTANCES;
		Request.IPS = IPS;
	}

	private synchronized void broadcast() throws RemoteException, ServerNotFoundException {
		String[] succ = new String[INSTANCES.size()];
		for (int i = 0; i < succ.length; i++) {
			RemoteFrontend be = INSTANCES.get(i);
			if (be != first) {
				sf.apply(be);
				succ[i] = IPS.get(i);
			}
		}
		System.out.println("Broadcast successful [" + Arrays.toString(succ) + "]");
	}

	public Request(RemoteFunction<R> sf, RemoteFrontend first, boolean writeop) {
		this.sf = sf;
		this.first = first;
		this.writeop = writeop;
	}

	public synchronized void fulfil() {
		try {
			value = sf.apply(first);
			System.out.println(this + " fulfilled");
			if (writeop) this.broadcast();
		} catch (RemoteException e) {
			this.re = e;
			System.err.println("RemoteException caught on " + this);
		} catch (ServerNotFoundException e) {
			System.err.println("this shouldn't happen");
			e.printStackTrace();
		}
		latch.countDown();
	}

	@Override
	public boolean cancel(boolean arg0) {
		return false;
	}

	@Override
	public R get() throws InterruptedException, ExecutionException, RemoteException {
		System.out.println("Waiting for req to finish");
		latch.await();
		if (re != null) throw re;
		return value;
	}

	@Override
	public R get(long arg0, TimeUnit arg1)
			throws InterruptedException, ExecutionException, TimeoutException, RemoteException {
		if (latch.await(arg0, arg1)) {
			if (re != null) throw re;
			return value;
		} else {
			throw new TimeoutException();
		}
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return latch.getCount() == 0;
	}

	public String toString() {
		if (value != null) {
			Class<R> clazz = (Class<R>) value.getClass();
			return "Request<" + clazz.getSimpleName() + "> " + this.hashCode();
		}
		return "Request " + this.hashCode();
	}
}

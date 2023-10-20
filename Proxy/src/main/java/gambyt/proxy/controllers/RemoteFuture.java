package gambyt.proxy.controllers;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface RemoteFuture<T> {

	public boolean cancel(boolean arg0);

	public T get() throws InterruptedException, ExecutionException, RemoteException;

	public T get(long arg0, TimeUnit arg1)
			throws InterruptedException, ExecutionException, TimeoutException, RemoteException;

	public boolean isCancelled();

	public boolean isDone();

}

package gambyt.proxy.controllers;

import java.rmi.RemoteException;

import gambyt.backend.RemoteFrontend;
import gambyt.proxy.ServerNotFoundException;

public interface RemoteFunction<T> {

	public T apply(RemoteFrontend rf) throws RemoteException, ServerNotFoundException;

}

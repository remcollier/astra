package astra.messaging.rmi;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import astra.messaging.AstraMessage;
import astra.messaging.MessageService;

public class RMIService extends MessageService {
	public static interface RemoteAPI extends Remote {
		public boolean receiveMessage(AstraMessage message) throws RemoteException;
	}
	
	public static class ServiceAPI implements RemoteAPI {
		RMIService service;
		
		public ServiceAPI(RMIService service) {
			this.service = service;
		}
		@Override
		public boolean receiveMessage(AstraMessage message) {
			return service.receiveMessage(message);
		}
	}
	
	Registry registry;
	String id = Long.toString(System.currentTimeMillis());
	RemoteAPI api;
	
	@Override
	public boolean sendMessage(AstraMessage message) {
		// Try the local MessageService first...
		if (receiveMessage(message)) {
			return true;
		}
		
		// Now try the remote services...
		try {
			for(String id : registry.list()) {
				if (!id.equals(this.id)) {
					try {
						if (((RemoteAPI) registry.lookup(id)).receiveMessage(message)) {
							return true;
						}
					} catch (NotBoundException e) {
						// Platform is in the registry but is not active, so remove
						// it from the registry...
						try {
							registry.unbind(id);
						} catch (NotBoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Recreating RMI Service: " + id);
			registryConnect();
			
			try {
				RemoteAPI rapi = (RemoteAPI) UnicastRemoteObject.exportObject(api=new ServiceAPI(this), 0);
				registry.rebind(id, rapi);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void configure(String key, String value) {
	}

	private void registryConnect() {
		try {
			registry = LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			try {
				registry = LocateRegistry.getRegistry();
			} catch (RemoteException e1) {
				System.out.println("Serious Error with RMI registry");
				System.exit(0);
			}
		}
	}
	
	@Override
	public void start() {
		System.out.println("Creating RMI Service: " + id);
		registryConnect();
		
		try {
			RemoteAPI rapi = (RemoteAPI) UnicastRemoteObject.exportObject(api=new ServiceAPI(this), 0);
			registry.bind(id, rapi);
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

}

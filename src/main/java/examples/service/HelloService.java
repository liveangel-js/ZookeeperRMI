package examples.service;

/**
 * Created by sjiang3 on 4/19/17.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloService extends Remote {

    String sayHello(String name) throws RemoteException;
}
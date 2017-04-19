package zookeeper.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by sjiang3 on 4/19/17.
 */
public class ServiceConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProvider. class);
    public ServiceConsumer(){

    }

    public String publishRMIService(String host, int port, Remote remote){
        String url = null;
        try {
            url = String.format( "rmi://%s:%d/%s", host , port , remote.getClass().getName());
            LocateRegistry.createRegistry(port);
            Naming.rebind(url, remote);
            LOGGER.debug("publish rmi service (url: {})" , url );
        } catch (RemoteException e) {
            LOGGER.error("" , e );
        } catch (MalformedURLException e){
            LOGGER.error("", e);
        }
        return url ;

    }
    private static class A{
        public A(){
            System.out.println("I am constructed");
        }
        @Override
        public String toString(){
            System.out.println("I am called");
            return "dsad";
        }
    }
    public static void main(String args[]){
        A a = new A();
        LOGGER.debug("publish rmi service (url: {}) {}", a ,a );
        LOGGER.debug("publish rmi service (url: {}) {}", a ,a);
        LOGGER.debug("publish rmi service (url: {}) {}", a, a);
    }
}

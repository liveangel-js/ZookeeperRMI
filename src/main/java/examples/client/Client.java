package examples.client;

import examples.service.HelloService;
import zookeeper.rmi.ServiceConsumer;

import java.rmi.RemoteException;

/**
 * Created by sjiang3 on 4/20/17.
 */
public class Client {

    public static void main(String[] args) {
        ServiceConsumer consumer = new ServiceConsumer();
        while (true) {
            HelloService helloService = consumer.lookup(HelloService.class.getName());
            String result = null;
            try {
                result = helloService.sayHello("Jack");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            System.out.println(result);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

package examples.server;

import examples.service.HelloService;
import examples.service.HelloServiceImpl;
import zookeeper.rmi.ServiceProvider;

/**
 * Created by sjiang3 on 4/20/17.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        if (args .length != 2) {
            System. err.println("please using command: java Server <rmi_host> <rmi_port>");
            System. exit(-1);
        }


        String host = args[0];
        int port = Integer.parseInt (args[1]);
        // publish rmi to zookeeper
        ServiceProvider provider = new ServiceProvider();
        provider.publishToZookeeper(host, port);
        // register method to rmi
        HelloService helloService = new HelloServiceImpl();
        provider.registerService(host, port, helloService);

        Thread. sleep(Long.MAX_VALUE);
    }
}
package examples.server;

import examples.service.HelloService;
import examples.service.HelloServiceImpl;
import zookeeper.rmi.ServiceProvider;

/**
 * Created by sjiang3 on 4/20/17.
 */
public class Server {

    /**
     * java Server localhost 30000
     * java Server localhost 30001
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("please using command: java Server <rmi_host> <rmi_port>");
            System.exit(-1);
        }


        String host = args[0];
        int port = Integer.parseInt(args[1]);
        // publish rmi to zookeeper
        ServiceProvider provider = new ServiceProvider();
        provider.publishToZookeeper(host, port);
        // register method to rmi
        HelloService helloService = new HelloServiceImpl();
        String serviceName = HelloService.class.getName();
        provider.registerService(host, port, serviceName, helloService);

        Thread.sleep(Long.MAX_VALUE);
    }
}

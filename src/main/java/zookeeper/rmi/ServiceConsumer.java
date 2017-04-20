package zookeeper.rmi;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sjiang3 on 4/19/17.
 */
public class ServiceConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceProvider.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile List<String> rmiURLList = new ArrayList<String>();
    private static final Configuration configuration = Configuration.getInstance();

    public ServiceConsumer() {
        ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }

    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(configuration.getZKConnectionString(), configuration.getZKSessionTimeout(), new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOG.error("", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(configuration.getProviderDir(), new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        LOG.info("Zookeeper provider node change");
                        watchNode(zk);
                    }
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] data = zk.getData(configuration.getProviderDir() + "/" + node, false, null);
                dataList.add(new String(data));
            }
            LOG.debug("node data: {}", dataList);
            rmiURLList = dataList; // update service list
        } catch (KeeperException | InterruptedException e) {
            LOG.error("", e);
        }
    }

    private String chooseService() {
        int size = rmiURLList.size();
        if (size > 0) {
            String url;
            if (size == 1) {
                url = rmiURLList.get(0);
                LOG.debug("using only url: {}", url);
            } else {
                ThreadLocalRandom.current().nextInt();
                url = rmiURLList.get(ThreadLocalRandom.current().nextInt(size));
                LOG.debug("using random url: {}", url);
            }
            return url;
        } else {
            return null;
        }
    }

    public void listAllMethod() {
        String address = chooseService();
        Registry registry = null;
        if (address == null) {
            LOG.error(" rmi address is null");
        }

        try {
            String host = address.split("/")[2].split(":")[0];
            String port = address.split("/")[2].split(":")[1];
            registry = LocateRegistry.getRegistry(host, Integer.valueOf(port));
            String[] list = registry.list();
            for (String s : list) {
                LOG.debug("{}", s);
            }
        } catch (RemoteException e) {

        }

    }

    private <T> T lookupService(String url) {
        LOG.info("Find service ::" + url);
        T remote = null;
        try {
            remote = (T) Naming.lookup(url);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            if (e instanceof ConnectException) {
                //  If connection interrupt, use first RMI url, to this simple retry to reduce exception
                LOG.error("ConnectException -> url: {}", url);
                if (rmiURLList.size() != 0) {
                    url = rmiURLList.get(0);
                    return lookupService(url);
                }
            }
            LOG.error("{}", e);
        }
        return remote;
    }

    public <T> T lookup(String serviceName) {
        String serviceAddress = chooseService();
        if (serviceAddress.endsWith("/")) {
            return lookupService(serviceAddress + serviceName);
        } else {
            return lookupService(serviceAddress + "/" + serviceName);
        }
    }


}

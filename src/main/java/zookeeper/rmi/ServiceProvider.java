package zookeeper.rmi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sjiang3 on 4/19/17.
 */
public class ServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceProvider. class);

    private CountDownLatch latch = new CountDownLatch(1);

    public ServiceProvider(){

    }

    /**
     * publish rmi address to zookeeper
     *
     * @param host
     * @param port
     */
    public void publishToZookeeper(String host, int port){
        String rmiUrl = createRMI(host, port);
        if(rmiUrl != null){
            ZooKeeper zk = connectServer();

            if(zk!=null){
                createPreNode(zk);
                registerServiceNode(zk, rmiUrl);
            }else{
                LOG.error("Can't connect to zookeeper");
            }
        }else{
            LOG.error("Can't get RMI address");
        }


    }

    /**
     * Reigster method to rmi
     * @param host
     * @param port
     * @param remote
     */
    public void registerService(String host, int port, Remote remote){
        String methodURL = String.format( "rmi://%s:%d/%s", host , port , remote.getClass().getName());

        try {
            LocateRegistry.getRegistry(host, port);
            Naming.rebind(methodURL, remote);
            LOG.debug("publish rmi service (url: {})" , methodURL );
        } catch (RemoteException e) {
            LOG.error("", e);
        } catch (MalformedURLException e) {
            LOG.error("", e);
        }




    }

    private String createRMI(String host, int port){
        String url = null;
        try {
            url = String.format("rmi://%s:%d/", host, port);
            LocateRegistry.createRegistry(port);
            LOG.debug("publish rmi service (url: {})" , url );
        } catch (RemoteException e) {
            LOG.error("" , e );
        }

        return url;
    }

    public static void main(String args[]){
        LOG.info(Configuration.ConfVars.ZK_HOST.getVarName());
        LOG.info(Configuration.ConfVars.ZK_HOST.getStringValue());
    }
    private ZooKeeper connectServer(){
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(Configuration.getZKConnectionString(), Configuration.ConfVars.ZK_SESSION_TIMEOUT.getIntValue() , new Watcher() {
                public void process(WatchedEvent event) {
                    if (event .getState() == Event.KeeperState.SyncConnected ) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            LOG.error("" , e );
        } catch(InterruptedException e){
            LOG.error("" , e );
        }
        return zk ;
    }

    /**
     * Create Pre node if not exist
     * @param zk
     */
    private void createPreNode(ZooKeeper zk){
        String path = createNodeIfNotExists(zk, "/" + Configuration.ConfVars.ZK_ROOTNODE.getStringValue(), "This is root node".getBytes(), CreateMode.PERSISTENT);
        path = createNodeIfNotExists(zk, path + "/" + Configuration.ConfVars.ZK_SERVICE_PREFIX.getStringValue(), "service type: rmi".getBytes(), CreateMode.PERSISTENT);
        createNodeIfNotExists(zk, path + "/" + Configuration.ConfVars.ZK_SERVICE_NAME.getStringValue(), "This is service name".getBytes(), CreateMode.PERSISTENT);

    }

    private String createNodeIfNotExists(ZooKeeper zk, String node, byte[] data, CreateMode createMode){
        Stat stat = null;
        String path = null;
        try {
            stat = zk.exists(node, false);
            if(stat!=null){
                LOG.debug("Node {} already exists", node);
            }
        } catch (KeeperException e) {
            LOG.error("" , e );
        } catch (InterruptedException e) {
            LOG.error("" , e );
        }
        if(stat != null ){
            return node;
        }

        if(stat==null){
            try {
                path = zk.create(node, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                LOG.info("Create node {}", path);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(path!=null){
            return path;
        }else{
            return null;
        }
    }

    private void registerServiceNode(ZooKeeper zk, String url){
        try {
            byte[] data = url.getBytes();
            String path = zk .create(Configuration.getProviderPath(), data, ZooDefs.Ids.OPEN_ACL_UNSAFE , CreateMode.EPHEMERAL_SEQUENTIAL); // 创建一个临时性且有序的 ZNode
            LOG.debug("create zookeeper node ({} => {})" , path , url );
        } catch (KeeperException  e) {
            LOG.error("" , e );
        } catch (InterruptedException e){
            LOG.error("" , e );
        }

    }


}

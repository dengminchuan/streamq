package me.deve.streamq.nameserver;


import me.deve.streamq.common.config.NameserverConfig;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.common.address.KryoInetAddress;

public class NameserverStartup {
    private static NettyServerConfig nettyServerConfig;
    private static NettyClientConfig nettyClientConfig;

    private static NameserverConfig nameserverConfig;

    public static void main(String[] args) {
        startup(args);



    }

    private static void startup(String []args) {
        parseCommandlineAndConfigFile(args);
        NameserverController namespaceController = createAndStartNameserverController();
    }

    private static NameserverController createAndStartNameserverController() {
        NameserverController nameserverController = new NameserverController(nettyServerConfig, nettyClientConfig, nameserverConfig);
        start(nameserverController);
        return nameserverController;
    }

    private static void start(NameserverController nameserverController) {
        nameserverController.start();
    }

    /**
     * convert args to NettyServerConfig and NettyClientConfig
     */
    private static void parseCommandlineAndConfigFile(String[] args) {
        nettyClientConfig=new NettyClientConfig(new KryoInetAddress());
        nettyServerConfig=new NettyServerConfig();
        //todo:parse args and set them to nettyClientConfig nettyServerConfig

    }

}

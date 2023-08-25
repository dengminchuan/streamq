package me.deve.streamq.nameserver;


import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.config.NameserverConfig;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.common.address.KryoInetAddress;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NameserverStartup {
    /**
     * 默认port为10088
     */
    private static NettyServerConfig nettyServerConfig;
    private static NettyClientConfig nettyClientConfig;

    private static NameserverConfig nameserverConfig;

    public static List<InetSocketAddress> targetAddressList;

    public static void main(String[] args) throws IOException {
        //todo:解析args决定使用单机版还是集群部署
//        startupSingle(args);
        startupCluster(args);



    }

    private static void startupCluster(String[] args) throws IOException {
        //读取配置文件
        File seedFile = new File("nameserver/seedAddress.conf");
        if(!seedFile.exists()){
            throw new IOException("please set seed nameserver address");
        }
        List<InetSocketAddress> targetAddressList = loadSeedIpAndPort(seedFile);




    }

    /**
     * 单机启动
     * @param args
     */
    private static void startupSingle(String []args) throws IOException {
        parseCommandlineAndConfigFile(args);
        File seedFile = new File("nameserver/seedAddress.conf");
        if(!seedFile.exists()){
            throw new IOException("please set seed nameserver address");
        }
        targetAddressList = loadSeedIpAndPort(seedFile);
        NameserverController nameserverController = createAndStartNameserverControllerSingleton();

    }

    private static NameserverController createAndStartNameserverControllerSingleton() {
        NameserverController nameserverController = new NameserverController(nettyServerConfig, nettyClientConfig, nameserverConfig);
        start(nameserverController);
        return nameserverController;
    }

    private static NameserverController createAndStartNameserverControllerCluster(List<InetSocketAddress> targetAddressList) {
        NameserverController nameserverController = new NameserverController(nettyServerConfig, nettyClientConfig, nameserverConfig,targetAddressList);
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
    public static List<InetSocketAddress> loadSeedIpAndPort(File file) {
        List<InetSocketAddress> inetSocketAddressArrayList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line=reader.readLine())!=null){
                Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    String ip=matcher.group(1);
                    int port= Integer.parseInt(matcher.group(2));
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
                    inetSocketAddressArrayList.add(inetSocketAddress);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inetSocketAddressArrayList;
    }

}

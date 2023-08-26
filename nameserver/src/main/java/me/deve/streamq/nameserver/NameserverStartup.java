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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Map<String,InetSocketAddress> targetAddressMap;

    public static void main(String[] args) throws IOException {
        startupSingle(args);
    }

    private static void startupCluster(String[] args) throws IOException {
        File seedFile = new File("nameserver/seedAddress.conf");
        if(!seedFile.exists()){
            throw new IOException("please set seed nameserver address");
        }
        targetAddressMap = loadSeedIpAndPort(seedFile);
    }

    private static void startupSingle(String []args) throws IOException {
        parseCommandlineAndConfigFile(args);
        NameserverController nameserverController = createAndStartNameserverControllerSingleton();

    }

    private static NameserverController createAndStartNameserverControllerSingleton() {
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
    public static Map<String,InetSocketAddress> loadSeedIpAndPort(File file) {
        Map<String,InetSocketAddress> inetSocketAddressArrayMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line=reader.readLine())!=null){
                Pattern pattern = Pattern.compile("^\\w+,(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)$");
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    String name=matcher.group(1);
                    String ip=matcher.group(2);
                    int port= Integer.parseInt(matcher.group(3));
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
                    inetSocketAddressArrayMap.put(name,inetSocketAddress);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inetSocketAddressArrayMap;
    }

}

//-*- coding =utf-8 -*-
//@Time : 2023/6/17
//@Author: 邓闽川
//@File  NameServerController.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import me.deve.streamq.common.config.NameserverConfig;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.common.thread.ShutdownHookThread;
import me.deve.streamq.nameserver.gossip.GossipHandler;
import me.deve.streamq.nameserver.handler.NameServerDealFindingHandler;
import me.deve.streamq.nameserver.handler.NameServerDealHeartUploadHandler;
import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.netty.NettyServer;

import java.net.InetSocketAddress;
import java.util.List;

public class NameserverController {
    private NettyServerConfig nettyServerConfig;
    private NettyClientConfig nettyClientConfig;

    private NameserverConfig nameserverConfig;

    private NettyServer nettyServer;

    private NettyClient nettyClient;

    public NameserverController(){

    }
    public NameserverController(NettyServerConfig nettyServerConfig,NettyClientConfig nettyClientConfig,NameserverConfig nameserverConfig){
        this.nettyServerConfig = nettyServerConfig;
        this.nameserverConfig=nameserverConfig;
        this.nettyClientConfig=nettyClientConfig;
        initializeNetworkComponentsForSingle();
    }
    public NameserverController(NettyServerConfig nettyServerConfig, NettyClientConfig nettyClientConfig, NameserverConfig nameserverConfig, List<InetSocketAddress> targetAddress){
        this.nettyServerConfig = nettyServerConfig;
        this.nameserverConfig=nameserverConfig;
        this.nettyClientConfig=nettyClientConfig;
        initializeNetworkComponentsForCluster();
    }
    public void start(){
        nettyServer.start();
        /**
         * shutdown gracefully
         */
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(() -> {
            shutdown();
            return null;
        }));

    }
    public void startClient(){

    }
    private void initializeNetworkComponentsForSingle(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        NameServerDealHeartUploadHandler nameServerDealHeartUploadHandler = new NameServerDealHeartUploadHandler();
        NameServerDealFindingHandler nameServerDealFindingHandler = new NameServerDealFindingHandler();
        //addLast mode to add handler
        nettyServer = new NettyServer(bossGroup, workerGroup, bootstrap, nettyServerConfig, nameServerDealHeartUploadHandler,nameServerDealFindingHandler);

    }
    private void initializeNetworkComponentsForCluster(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        NameServerDealHeartUploadHandler nameServerDealHeartUploadHandler = new NameServerDealHeartUploadHandler();
        NameServerDealFindingHandler nameServerDealFindingHandler = new NameServerDealFindingHandler();
        GossipHandler gossipHandler = new GossipHandler();
        //addLast mode to add handler
        nettyServer = new NettyServer(bossGroup, workerGroup, bootstrap, nettyServerConfig, nameServerDealHeartUploadHandler,nameServerDealFindingHandler,gossipHandler);
    }
    public void shutdown(){
        nettyServer.shutdown();


    }

}

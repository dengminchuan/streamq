//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  BrokerController.java
//@software:IntelliJ IDEA
package me.deve.streamq.broker;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import me.deve.streamq.remoting.thread.NettyClientThread;
import me.deve.streamq.remoting.thread.NettyServerThread;
import me.deve.streamq.common.config.BrokerConfig;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.netty.NettyServer;

public class BrokerController {
    private BrokerConfig brokerConfig;

    private NettyClientConfig nettyClientConfig;

    private NettyServerConfig nettyServerConfig;

    private ChannelInboundHandlerAdapter[] clientHandlers;
    private ChannelInboundHandlerAdapter[] serverHandlers;

    private NettyClient nettyClient;

    private NettyServer nettyServer;
    private NettyClientThread clientThread ;
    private NettyServerThread serverThread ;




    public BrokerController(BrokerConfig brokerConfig,NettyClientConfig nettyClientConfig,NettyServerConfig nettyServerConfig,ChannelInboundHandlerAdapter []clientHandlers,ChannelInboundHandlerAdapter []serverHandlers){
        this.brokerConfig = brokerConfig;
        this.nettyClientConfig = nettyClientConfig;
        this.nettyServerConfig=nettyServerConfig;
        this.clientHandlers =clientHandlers;
        this.serverHandlers=serverHandlers;
        initializeInetComponents();
    }

    private void initializeInetComponents() {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        nettyClient = new NettyClient(eventExecutors, bootstrap, nettyClientConfig, clientHandlers);
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        nettyServer=new NettyServer(bossGroup,workerGroup,serverBootstrap,nettyServerConfig,true,serverHandlers);

    }

    public void start(){
        clientThread = new NettyClientThread(nettyClient);
        serverThread = new NettyServerThread(nettyServer);
        clientThread.start();
        serverThread.start();
    }
    public void shutdown(){
        clientThread.shutdown();
        serverThread.shutdown();
    }


}

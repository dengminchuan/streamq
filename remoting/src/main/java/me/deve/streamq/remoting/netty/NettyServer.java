//-*- coding =utf-8 -*-
//@Time : 2023/6/18
//@Author: 邓闽川
//@File  NettyServer.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.remoting.handler.ServerHandler;
@Data

public class NettyServer {
    //创建两个无限循环的线程组，BossGroup处理连接请求，workerGroup处理客户端业务
    //bossGroup和workerGroup含有的子线程个数默认为cpu核数*2
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private NettyServerConfig nettyServerConfig;
    private ChannelInboundHandlerAdapter []handlers;
    public NettyServer (NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, ServerBootstrap serverBootstrap, NettyServerConfig nettyServerConfig, ChannelInboundHandlerAdapter... handlers){
        this.bossGroup=bossGroup;
        this.workerGroup=workerGroup;
        this.serverBootstrap=serverBootstrap;
        this.nettyServerConfig=nettyServerConfig;
        this.handlers=handlers;
        initialize();
    }
    private void initialize() {
        //进行启动参数的设置
        serverBootstrap.
                group(bossGroup,workerGroup).
                channel(NioServerSocketChannel.class).//使用NIO作为通道
                option(ChannelOption.SO_BACKLOG, nettyServerConfig.getThreadConnectValue()).//设置线程队列连接个数
                childOption(ChannelOption.SO_KEEPALIVE,nettyServerConfig.getKeepConnectAlive()).//设置保持活动连接状态
                childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                for (int i = 0; i < handlers.length; i++) {
                    channel.pipeline().addLast(handlers[i]);
                }

            }
        });
    }
    public void start(){
        try {
            ChannelFuture cf = serverBootstrap.bind(nettyServerConfig.getBindPort()).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            bossGroup.shutdownGracefully();
        }

    }
    public void shutdown() {
        bossGroup.shutdownGracefully();
    }
}

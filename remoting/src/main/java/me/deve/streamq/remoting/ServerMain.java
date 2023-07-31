//-*- coding =utf-8 -*-
//@Time : 2023/6/18
//@Author: 邓闽川
//@File  ServerMain.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.remoting.handler.ServerHandler;
import me.deve.streamq.remoting.netty.NettyServer;

/**
 * Server功能测试类
 */
public class ServerMain {
    public static void main(String[] args) {
        new Thread(() -> new NettyServer(new NioEventLoopGroup(),new NioEventLoopGroup(),new ServerBootstrap(),new NettyServerConfig(8810),new ServerHandler()).start()).start();
        System.out.println("server init");
    }
}

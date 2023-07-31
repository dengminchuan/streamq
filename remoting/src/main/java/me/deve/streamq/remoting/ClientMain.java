//-*- coding =utf-8 -*-
//@Time : 2023/6/18
//@Author: 邓闽川
//@File  ClientMain.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;

import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.remoting.handler.ClientHandler;
import me.deve.streamq.remoting.netty.NettyClient;

import java.net.InetSocketAddress;

/**
 * Client功能测试类
 */
public class ClientMain {
    public static void main(String[] args) {
            NettyClient nettyClient = new NettyClient(new NioEventLoopGroup(), new Bootstrap(), new NettyClientConfig(new KryoInetAddress()), new ClientHandler());
            InetSocketAddress[]inetSocketAddress = new InetSocketAddress[2];
            inetSocketAddress[0]=new InetSocketAddress("127.0.0.1",8810);
            inetSocketAddress[1]=new InetSocketAddress("127.0.0.1",8811);


    }
}

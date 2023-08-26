//-*- coding =utf-8 -*-
//@Time : 2023/6/18
//@Author: 邓闽川
//@File  ClientMain.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.remoting.handler.ClientHandler;
import me.deve.streamq.remoting.netty.NettyClient;

import java.net.ConnectException;
import java.net.InetSocketAddress;

/**
 * Client功能测试类
 */
public class ClientMain {
    public static void main(String[] args) throws InterruptedException {
        try {
            NettyClient nettyClient = new NettyClient(new NioEventLoopGroup(), new Bootstrap(), new NettyClientConfig(new KryoInetAddress("127.0.0.1",8810)), new ClientHandler());
            nettyClient.start();
        } catch (Exception e) {
            if(e instanceof ConnectException){
                e.printStackTrace();
            }


        }
    }
}

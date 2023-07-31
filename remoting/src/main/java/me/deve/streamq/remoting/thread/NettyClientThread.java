//-*- coding =utf-8 -*-
//@Time : 2023/7/1
//@Author: 邓闽川
//@File  NettyServerThread.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.thread;

import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.netty.NettyServer;

import java.util.concurrent.CountDownLatch;

public class NettyClientThread extends Thread{
    private NettyClient nettyClient;

    public NettyClientThread(NettyClient nettyClient){
        this.nettyClient=nettyClient;
    }

    @Override
    public void run() {

        nettyClient.start();
    }
    public void shutdown(){
        nettyClient.shutdown();
    }


}

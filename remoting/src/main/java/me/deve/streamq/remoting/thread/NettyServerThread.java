//-*- coding =utf-8 -*-
//@Time : 2023/7/1
//@Author: 邓闽川
//@File  NettyServerThread.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.thread;

import me.deve.streamq.remoting.netty.NettyServer;

import java.util.concurrent.CountDownLatch;

public class NettyServerThread extends Thread{




    public NettyServerThread(NettyServer nettyServer){
        this.nettyServer=nettyServer;
    }
    private NettyServer nettyServer;
    @Override
    public void run() {

        nettyServer.start();
    }
    public void shutdown(){
        nettyServer.shutdown();
    }}

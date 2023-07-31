package me.deve.streamq.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.remoting.handler.ClientHandler;
import me.deve.streamq.remoting.handler.ServerHandler;
import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.netty.NettyServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;

class RemotingApplicationTests {

    @Test
    void testMultipleClient() {
        NettyClient nettyClient = new NettyClient(new NioEventLoopGroup(), new Bootstrap(), new NettyClientConfig(new KryoInetAddress()), new ClientHandler());
        InetSocketAddress []inetSocketAddress = new InetSocketAddress[2];
        inetSocketAddress[0]=new InetSocketAddress("127.0.0.1",8810);
        inetSocketAddress[1]=new InetSocketAddress("127.0.0.1",8811);
//        nettyClient.startMultiple(inetSocketAddress);
        System.out.println(1);
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> new NettyServer(new NioEventLoopGroup(),new NioEventLoopGroup(),new ServerBootstrap(),new NettyServerConfig(8811),new ServerHandler()).start()).start();
        new Thread(() -> new NettyServer(new NioEventLoopGroup(),new NioEventLoopGroup(),new ServerBootstrap(),new NettyServerConfig(8810),new ServerHandler()).start()).start();
        Thread.sleep(2000);
        new Thread(() -> {
            NettyClient nettyClient = new NettyClient(new NioEventLoopGroup(), new Bootstrap(), new NettyClientConfig(new KryoInetAddress()), new ClientHandler());
            InetSocketAddress []inetSocketAddress = new InetSocketAddress[2];
            inetSocketAddress[0]=new InetSocketAddress("127.0.0.1",8810);
            inetSocketAddress[1]=new InetSocketAddress("127.0.0.1",8811);
//            nettyClient.startMultiple(inetSocketAddress);
        }).start();


    }

}

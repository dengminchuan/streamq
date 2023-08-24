package me.deve.streamq.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.FileUtil;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.remoting.handler.ClientHandler;
import me.deve.streamq.remoting.handler.ServerHandler;
import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.netty.NettyServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

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
    @Test
    void testZeroCopy()  {
        String message="this is a test message";
        try (RandomAccessFile rf = new RandomAccessFile("test.txt", "rw");
             FileChannel fc=rf.getChannel();
             ) {
            MappedByteBuffer mappedByteBuffer=fc.map(FileChannel.MapMode.READ_WRITE,fc.size(),message.getBytes().length);
            mappedByteBuffer.put(message.getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    @Test
    void compareTraditionIOWithDMA(){
        File file = new File("tradition.txt");
        long start = System.currentTimeMillis();
        FurySerializer furySerializer = new FurySerializer();
        for(int i=0;i<1000;i++){
            Message message = new Message("topic", "test".getBytes());
            FileUtil.write2Binary(file,furySerializer.serialize(message),true);
        }
        System.out.println(System.currentTimeMillis()-start);
        File file2 = new File("mmap.txt");
        long start2 = System.currentTimeMillis();
        try (RandomAccessFile rf = new RandomAccessFile("test.txt", "rw");
             FileChannel fc=rf.getChannel();){
            for(int i=0;i<1000;i++){
                Message message = new Message("topic", "test".getBytes());
                byte[] serialize = furySerializer.serialize(message);
                MappedByteBuffer mappedByteBuffer=fc.map(FileChannel.MapMode.READ_WRITE,fc.size(),serialize.length);
                mappedByteBuffer.put(serialize);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(System.currentTimeMillis()-start2);

    }
    @Test
    void testWriteToDiskTime(){
            FurySerializer furySerializer = new FurySerializer();
            File file = new File("testWrite.txt");
            try (RandomAccessFile rf = new RandomAccessFile(file, "rw");
                 FileChannel fc=rf.getChannel();){
                Message message = new Message("topic", "test".getBytes());
                byte[] serialize = furySerializer.serialize(message);
                MappedByteBuffer mappedByteBuffer=fc.map(FileChannel.MapMode.READ_WRITE,fc.size(),400);
                    for(int i=0;i<5;i++){
                    mappedByteBuffer.put(serialize);
                    System.out.println(file.length());
                }
                System.out.println(fc.hashCode());
                mappedByteBuffer.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                RandomAccessFile rf = new RandomAccessFile(file, "rw");
                FileChannel channel = rf.getChannel();
                System.out.println(channel.hashCode());
                channel.truncate(100);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}


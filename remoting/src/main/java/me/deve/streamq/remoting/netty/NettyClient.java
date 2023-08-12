//-*- coding =utf-8 -*-
//@Time : 2023/6/18
//@Author: 邓闽川
//@File  NettyClient.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.config.NettyClientConfig;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class NettyClient {
    private NioEventLoopGroup eventExecutors;
    private Bootstrap bootstrap;
    private NettyClientConfig nettyClientConfig;

    private ChannelInboundHandlerAdapter[] handler;

    private ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(
            10,
            20,
            100,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public NettyClient(NioEventLoopGroup nioEventLoopGroup,Bootstrap bootstrap,NettyClientConfig nettyClientConfig,ChannelInboundHandlerAdapter... handler) {
        this.eventExecutors=nioEventLoopGroup;
        this.bootstrap=bootstrap;
        this.nettyClientConfig=nettyClientConfig;
        this.handler=handler;
        initialize();
    }


    /**
     * init netty server
     */
    private void initialize() {
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        for (int i = 0; i < handler.length; i++) {
                            channel.pipeline().addLast(handler[i]);
                        }

                    }
                });
    }

    public void setUseLineBasedFrameDecoder(Boolean useLineBasedFrameDecoder) {
        this.useLineBasedFrameDecoder = useLineBasedFrameDecoder;
    }

    private Boolean useLineBasedFrameDecoder = false;

    public void start(){
        try {
            //连接
            ChannelFuture cf = bootstrap.connect(nettyClientConfig.getInetAddress().getInetSocketAddress()).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.warn("connect error,error message:"+e.getMessage());
        }
        finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public void startMultiple(List<KryoInetAddress> kryoInetAddressList){
            //连接
            kryoInetAddressList.forEach(kryoInetAddress -> {
                threadPoolExecutor.execute(() -> {
                    try {
                        ChannelFuture cf = bootstrap.connect(kryoInetAddress.getInetSocketAddress());
                        cf.channel().closeFuture().sync();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
    }
    public void shutdown(){
        eventExecutors.shutdownGracefully();
    }



}

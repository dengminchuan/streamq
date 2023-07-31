//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  BrokerUploadClient.java
//@software:IntelliJ IDEA
package me.deve.streamq.broker.handler;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.config.BrokerConfig;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * handler for upload heartbeat
 */
@Slf4j
public class BrokerUploadHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;
    private NettyClientConfig nettyClientConfig;

    private BrokerConfig brokerConfig;

    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private NettyServerConfig nettyServerConfig;
    public BrokerUploadHandler(NettyClientConfig nettyClientConfig,BrokerConfig brokerConfig,NettyServerConfig nettyServerConfig){
        this.nettyClientConfig=nettyClientConfig;
        this.brokerConfig=brokerConfig;
        this.nettyServerConfig=nettyServerConfig;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*
          registry and start timer-task,send heartbeat to nameserver
         */
        FunctionMessage beatFunctionMessage = generateRegistryFunctionMessage(FunctionMessageType.BROKER_UPLOAD_HEARTBEAT);
        startTimerTask(ctx,beatFunctionMessage);
    }

    private void startTimerTask(ChannelHandlerContext ctx,FunctionMessage beatFunctionMessage) {
        Timer timer = new Timer();
        TimerTask uploadHeartBeat = new TimerTask() {
            @Override
            public void run() {
                ctx.channel().writeAndFlush(allocator.buffer().writeBytes(kryoSerializer.serialize(beatFunctionMessage)));
            }
        };
        //upload heart beat
        timer.schedule(uploadHeartBeat,1000,1000);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("server disconnect");
    }

    private FunctionMessage generateRegistryFunctionMessage(FunctionMessageType functionMessageType) {
        FunctionMessage functionMessage=null;
        try {
            functionMessage = new FunctionMessage(functionMessageType, new Broker(brokerConfig.getId(), brokerConfig.getName(), new KryoInetAddress(KryoInetAddress.getLocalhost(), nettyServerConfig.getBindPort()), "test topic"));
        } catch (UnknownHostException e) {
            log.error("get localhost error");
        }
        return functionMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("netty server error"+cause.getMessage());
    }
}

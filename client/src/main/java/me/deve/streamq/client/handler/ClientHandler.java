//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  NameServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.handler;

import cn.hutool.core.lang.func.Func;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.producer.DefaultMQProducer;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * netty server for client
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private Semaphore semaphore;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       // ctx.channel().writeAndFlush(allocator.buffer().writeBytes(kryoSerializer.serialize()));
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        DefaultMQProducer.topicRouteData= kryoSerializer.deserialize(array, HashMap.class);
        semaphore.release();

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FunctionMessage obtainBrokerMessage = generateFunctionMessage();
        byte[] messageByteArr = kryoSerializer.serialize(obtainBrokerMessage);
        ctx.channel().writeAndFlush(allocator.buffer().writeBytes(messageByteArr));
    }

    private FunctionMessage generateFunctionMessage() {
        return new FunctionMessage(FunctionMessageType.OBTAIN_BROKERS);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * Triggered when the client connection is disconnected
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        super.channelInactive(ctx);
    }


    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }
}

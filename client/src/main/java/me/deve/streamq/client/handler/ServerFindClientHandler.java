//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  NameServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.producer.DefaultMQProducer;
import me.deve.streamq.client.producer.MQProducer;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * netty server for client
 */
@Slf4j
public class ServerFindClientHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private CountDownLatch latch;

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    private DefaultMQProducer producer;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        producer.topicRouteData= kryoSerializer.deserialize(array, HashMap.class);
        latch.countDown();
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


    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}

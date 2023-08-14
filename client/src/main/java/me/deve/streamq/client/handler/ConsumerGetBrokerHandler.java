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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.consumer.MQConsumer;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.constant.TimerConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

@Slf4j
public class ConsumerGetBrokerHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private List<String> topics;

    @Getter
    private HashMap<String,List<Broker>> consumerTargetBrokers;

    private MQConsumer mqConsumer;

    @Getter
    private final Semaphore  semaphore=new Semaphore(0);



    public void setMqConsumer(MQConsumer consumer){
        this.mqConsumer=consumer;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        consumerTargetBrokers = kryoSerializer.deserialize(array, HashMap.class);
        mqConsumer.setConsumerTargetBrokers(consumerTargetBrokers);
        semaphore.release();


    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        registerConsumer(ctx);
    }





    private FunctionMessage generateFunctionMessage(FunctionMessageType type) {
        return new FunctionMessage(type,topics);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
    private void registerConsumer(ChannelHandlerContext ctx) {
        writeAndSendMessage(ctx,FunctionMessageType.CONSUMER_READY);
    }
    private void writeAndSendMessage(ChannelHandlerContext ctx,FunctionMessageType type){
        FunctionMessage functionMessage = generateFunctionMessage(type);
        byte[] messageByteArr = kryoSerializer.serialize(functionMessage);
        ctx.channel().writeAndFlush(allocator.buffer().writeBytes(messageByteArr));
    }

    public void setTopics(List<String> topics) {
        this.topics=topics;
    }
}

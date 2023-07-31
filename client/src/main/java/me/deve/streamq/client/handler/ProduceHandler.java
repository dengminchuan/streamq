//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  NameServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.handler;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;

/**
 * netty server for client
 */
@Slf4j
public class ProduceHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final static KryoSerializer kryoSerializer = new KryoSerializer();

    private ChannelHandlerContext ctx;

    public Boolean getConnected() {
        return isConnected;
    }

    private Boolean isConnected = false;

    public void sendMsg(Message message){
        FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.NORMAL_MESSAGE, message);
        byte[] serializeArr = kryoSerializer.serialize(functionMessage);
        int messageLength = serializeArr.length;
        ctx.channel().writeAndFlush(allocator.buffer(messageLength).writeBytes(serializeArr));
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
       isConnected=true;

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
        isConnected=false;
    }



}

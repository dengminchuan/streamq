//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  NameServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.handler;

import cn.hutool.core.util.ArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import me.deve.streamq.remoting.symbol.DelimiterSymbol;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;

/**
 * netty server for client
 */
@Slf4j
@ChannelHandler.Sharable
public class ProduceHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final FurySerializer furySerializer=new FurySerializer();

    private ChannelHandlerContext ctx;


    public void sendMsg(Message message){
        FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.NORMAL_MESSAGE, message);
        byte[] serializeArr = furySerializer.serialize(functionMessage);
        byte[] bytes = ArrayUtil.addAll(serializeArr, DelimiterSymbol.DELIMITER_SYMBOL);
        ByteBuf buffer = allocator.buffer();
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(buffer.writeBytes(bytes));
        if(channelFuture.isSuccess()) buffer.release();

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }


    /**
     * Triggered when the client connection is disconnected
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }



}

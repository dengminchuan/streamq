//-*- coding =utf-8 -*-
//@Time : 2023/4/17
//@Author: 邓闽川
//@File  ClientHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.handler;

import cn.hutool.core.util.ArrayUtil;
import io.fury.Fury;
import io.fury.Language;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;

@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 通道就绪触发方法
     * @param ctx
     * @throws Exception
     */
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
            FurySerializer fury = new FurySerializer();
            byte[] serialize = fury.serialize(new FunctionMessage(FunctionMessageType.NORMAL_MESSAGE));
            byte[] bytes = ArrayUtil.addAll(serialize, "\n".getBytes());
            ctx.channel().writeAndFlush(allocator.buffer().writeBytes(bytes));

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println("server reply:"+byteBuf.toString(CharsetUtil.UTF_8));
    }

}

//-*- coding =utf-8 -*-
//@Time : 2023/4/17
//@Author: 邓闽川
//@File  Handler.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.handler;

import io.fury.Fury;
import io.fury.Language;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.nio.charset.StandardCharsets;

//继承规定的某个Handler
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端数据
     * @param ctx 上下文对象，含有管道pipeline 通道 channel
     * @param msg 客户端发送的数据
     */
    FurySerializer furySerializer = new FurySerializer();
    @Override

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = getMessageBytes(msg);
        FurySerializer furySerializer = new FurySerializer();
        FunctionMessage functionMessage = furySerializer.deserialize(array, FunctionMessage.class);
        System.out.println(functionMessage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("channel active");
    }

    public byte[] getMessageBytes(Object msg){
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        return array;
    }

}

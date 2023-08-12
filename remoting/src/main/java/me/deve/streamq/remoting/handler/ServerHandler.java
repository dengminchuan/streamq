//-*- coding =utf-8 -*-
//@Time : 2023/4/17
//@Author: 邓闽川
//@File  Handler.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.message.FunctionMessage;
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
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
    }


    /**
     * 处理异常
     */
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//
//        ctx.close();
//    }
}

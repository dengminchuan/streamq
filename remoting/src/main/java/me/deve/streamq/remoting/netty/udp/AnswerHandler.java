//-*- coding =utf-8 -*-
//@Time : 2023/8/25
//@Author: 邓闽川
//@File  AnswerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.netty.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.Random;

public class AnswerHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                DatagramPacket packet)
            throws Exception {

        //获得请求
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println("接收到请求："+req);

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}

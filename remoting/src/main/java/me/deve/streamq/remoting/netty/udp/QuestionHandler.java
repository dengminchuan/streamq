//-*- coding =utf-8 -*-
//@Time : 2023/8/25
//@Author: 邓闽川
//@File  QuestionHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.netty.udp;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class QuestionHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
                throws Exception {
            //获得应答，DatagramPacket提供了content()方法取得报文的实际内容
            String response = msg.content().toString(CharsetUtil.UTF_8);
                System.out.println(response);
                ctx.close();

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            cause.printStackTrace();
            ctx.close();
        }


}

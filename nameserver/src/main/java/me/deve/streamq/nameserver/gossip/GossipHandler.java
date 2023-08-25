//-*- coding =utf-8 -*-
//@Time : 2023/8/25
//@Author: 邓闽川
//@File  GossipHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver.gossip;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.constant.TimerConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class GossipHandler extends ChannelInboundHandlerAdapter {
    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;
    /**
     * nameserver's name and address
     */
    private final ConcurrentHashMap<String, InetSocketAddress> nameserverList=new ConcurrentHashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


    }






    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("netty error,cause message:"+cause.getMessage());
        ctx.close();
    }



}

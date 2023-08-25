//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  NameServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver.handler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
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
import me.deve.streamq.nameserver.timertask.DeleteTimerTask;
import me.deve.streamq.nameserver.timertask.PersistTimerTask;
import me.deve.streamq.remoting.route.TopicRouteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * netty server for nameserver to deal with server find
 */
@Slf4j
@ChannelHandler.Sharable
public class NameServerDealFindingHandler extends ChannelInboundHandlerAdapter {
    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FunctionMessage functionMessage= (FunctionMessage) msg;
        NameServerDealHeartUploadHandler nameServerDealHeartUploadHandler = ctx.pipeline().get(NameServerDealHeartUploadHandler.class);
        ConcurrentHashMap<Broker, DateTime> livingBrokers = nameServerDealHeartUploadHandler.getLivingBrokers();
        HashMap<String, List<Broker>> topicRouteData = new HashMap<>();
        HashMap<String, List<Broker>> consumerTargetBrokers = new HashMap<>();
        livingBrokers.forEach((broker, dateTime) -> {
            if(checkValid(dateTime)){
                String topicName = broker.getTopicName();
                if(topicRouteData.containsKey(topicName)){
                    List<Broker> brokers = topicRouteData.get(topicName);
                    brokers.add(broker);
                    topicRouteData.put(topicName,brokers);
                }else{
                    List<Broker> brokers =new ArrayList<>();
                    brokers.add(broker);
                    topicRouteData.put(topicName,brokers);

                }
            }
        });
        if(functionMessage.getMessageType()== FunctionMessageType.CONSUMER_READY){
            List<String> topics = functionMessage.getTopics();
            topicRouteData.forEach((s, brokers) -> {
                if(topics.contains(s)){
                    consumerTargetBrokers.put(s,brokers);
                }
            });
            ctx.channel().writeAndFlush(allocator.buffer().writeBytes(kryoSerializer.serialize(consumerTargetBrokers)));
            return;
        }

        ctx.channel().writeAndFlush(allocator.buffer().writeBytes(kryoSerializer.serialize(topicRouteData)));
    }

    private boolean checkValid(DateTime dateTime) {
        DateTime date=DateUtil.date();
        long l = DateUtil.betweenMs(dateTime, date);
        return l <= TimerConstant.HEART_BEAT_ELIMINATE_TIME;
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("netty error,cause message:"+cause.getMessage());
        ctx.close();
    }

    /**
     * Triggered when the client connection is disconnected
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("broker断开连接");
        //todo:删除节点信息
        super.channelInactive(ctx);
    }
}

//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  NameServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver.handler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import me.deve.streamq.nameserver.timertask.DeleteTimerTask;
import me.deve.streamq.nameserver.timertask.PersistTimerTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * netty server for nameserver to deal with heart upload
 */
@Slf4j
@ChannelHandler.Sharable
public class NameServerDealHeartUploadHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap< Broker,DateTime> livingBrokers = new ConcurrentHashMap<>();
    private final KryoSerializer kryoSerializer = new KryoSerializer();

    private final AtomicBoolean timerStarted =new AtomicBoolean(false);

    private  final DeleteTimerTask deleteTimerTask=new DeleteTimerTask(livingBrokers);

    private  final PersistTimerTask persistTimerTask=new PersistTimerTask(livingBrokers);

    private final ThreadPoolExecutor threadPool=new ThreadPoolExecutor(
            5,
            10,
            1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        FunctionMessage message = kryoSerializer.deserialize(array, FunctionMessage.class);
        if(message.getMessageType()== FunctionMessageType.OBTAIN_BROKERS||message.getMessageType()==FunctionMessageType.CONSUMER_READY){
            ctx.fireChannelRead(message);
            return;
        }

        /*
         * start timer task
         */
        if(!timerStarted.get()){
            startTimerTask();
        }
        timerStarted.set(true);
        /*
          use file to store data
         */
        livingBrokers.put(message.getBroker(),DateUtil.date());
        byteBuf.release();

    }


    private void startTimerTask() {
        deleteTimerTask.start();
        persistTimerTask.start();

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
        super.channelInactive(ctx);
    }
    public ConcurrentHashMap<Broker, DateTime> getLivingBrokers() {
        return livingBrokers;
    }
}

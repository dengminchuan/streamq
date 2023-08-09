//-*- coding =utf-8 -*-
//@Time : 2023/6/30
//@Author: 邓闽川
//@File  MessageServerHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.broker.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.broker.MessageQueueController;
import me.deve.streamq.common.constant.TimerConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@ChannelHandler.Sharable
public class MessageServerHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    public void setMessageQueueController(MessageQueueController messageQueueController) {
        this.messageQueueController = messageQueueController;
    }

    private MessageQueueController messageQueueController;
    private final static KryoSerializer kryoSerializer = new KryoSerializer();

    private volatile ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final ConcurrentHashMap<ChannelHandlerContext,Long> pullRequestTable = new ConcurrentHashMap<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        startCheckPullRequestTable();
    }

    private void startCheckPullRequestTable() {
        ScheduledExecutorService checkPullRequestService = Executors.newScheduledThreadPool(1);
        Runnable checkRequestTask = () ->{
            Iterator<Map.Entry<ChannelHandlerContext, Long>> iterator = pullRequestTable.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<ChannelHandlerContext, Long> entry = iterator.next();
                ChannelHandlerContext ctx = entry.getKey();
                Long offset = entry.getValue();
                if(messageQueueController.getCommitLog().getCommitLogOffset()>offset){
                    pushMessage(offset,ctx);
                    iterator.remove();
                }
            }
        };
                pullRequestTable.forEach((channelHandlerContext, offset) -> {
            //check if there have new message
            if(messageQueueController.getCommitLog().getCommitLogOffset()>offset){
                pushMessage(offset,channelHandlerContext);

            }
        });
        checkPullRequestService.scheduleWithFixedDelay(checkRequestTask,0,TimerConstant.CHECK_PULL_REQUEST_TABLE_DELAY, TimeUnit.MILLISECONDS);
    }

    public void pushMessageToAllConsumer(){
        //write back a FunctionMessage
       channelGroup.writeAndFlush(allocator.buffer().writeBytes("first test message".getBytes()));
    }
    public void pushMessage(Long consumerOffset, ChannelHandlerContext ctx){
           //todo:find message and return
            
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] array = getMessageBytes(msg);
        FunctionMessage functionMessage = kryoSerializer.deserialize(array, FunctionMessage.class);
        Long consumerOffset = functionMessage.getOffset();
        //when producer provide message
        if(functionMessage.getMessageType()==FunctionMessageType.NORMAL_MESSAGE){
            Long offset = messageQueueController.add(functionMessage.getMessage());
        }
        else if(functionMessage.getMessageType()==FunctionMessageType.REGISTER_PULL_REQUEST){
            channelGroup.add(ctx.channel());
        }
        else if(functionMessage.getMessageType()==FunctionMessageType.PULL_MESSAGE){
            //pull message,use long cycle mode
            //todo:Determine if there is a message.
            // If there is no message, store the request and wait until there is a message before returning
            //exists new message
            if(consumerOffset <messageQueueController.getCommitLog().getCommitLogOffset()){
                pushMessage(consumerOffset,ctx);
            }else{
                pullRequestTable.put(ctx, consumerOffset);
            }

        }


    }
    public byte[] getMessageBytes(Object msg){
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        return array;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("netty server error:"+cause);
    }
}


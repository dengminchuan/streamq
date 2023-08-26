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
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.broker.MessageQueueController;
import me.deve.streamq.common.constant.TimerConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;


@Slf4j
@ChannelHandler.Sharable
public class MessageServerHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    public void setMessageQueueController(MessageQueueController messageQueueController) {
        this.messageQueueController = messageQueueController;
    }

    private MessageQueueController messageQueueController;

    private volatile ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final ConcurrentHashMap<ChannelHandlerContext,Long> pullRequestTable = new ConcurrentHashMap<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }
    private Boolean startCheckPullRequest=Boolean.FALSE;
    private void startCheckPullRequestTable() {
        ScheduledExecutorService checkPullRequestService = Executors.newScheduledThreadPool(1);
        Runnable checkRequestTask = () ->{
            Iterator<Map.Entry<ChannelHandlerContext, Long>> iterator = pullRequestTable.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<ChannelHandlerContext, Long> entry = iterator.next();
                ChannelHandlerContext ctx = entry.getKey();
                Long offset = entry.getValue();
                if(messageQueueController.getConsumeOffset()>offset){
                    pushMessage(offset,ctx);
                    iterator.remove();
                }
            }
        };
        checkPullRequestService.scheduleWithFixedDelay(checkRequestTask,0,TimerConstant.CHECK_PULL_REQUEST_TABLE_DELAY, TimeUnit.MILLISECONDS);
    }

    public void pushMessageToAllConsumer(){
        //write back a FunctionMessage
    }
    public void pushMessage(Long consumerOffset, ChannelHandlerContext ctx){
            Message message = messageQueueController.readMessage(consumerOffset+1);
            if(message==null){
                pullRequestTable.put(ctx,consumerOffset);
                return;
            }
            KryoSerializer messageSerializer = new KryoSerializer();
            FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.NORMAL_MESSAGE,message);
            byte[] messageBytes = messageSerializer.serialize(functionMessage);
            ctx.channel().writeAndFlush(allocator.buffer(messageBytes.length).writeBytes(messageBytes));
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FurySerializer furySerializer = new FurySerializer();
        byte[] array = getMessageBytes(msg);
        FunctionMessage functionMessage = furySerializer.deserialize(array,FunctionMessage.class);
        Long consumerOffset = functionMessage.getOffset();
        FunctionMessageType messageType = functionMessage.getMessageType();
        if(messageType ==FunctionMessageType.NORMAL_MESSAGE){
            Long offset = messageQueueController.add(functionMessage.getMessage());
            //todo:return OK if add successfully
        }
        else if(messageType ==FunctionMessageType.REGISTER_PULL_REQUEST){
            channelGroup.add(ctx.channel());
        }
        else if(messageType ==FunctionMessageType.PULL_MESSAGE){
            System.out.println("consumer offset:"+consumerOffset+" commit offset"+messageQueueController.getConsumeOffset());
            if(consumerOffset <messageQueueController.getConsumeOffset()){
                pushMessage(consumerOffset,ctx);
            }else{
                pullRequestTable.put(ctx, consumerOffset);
                if(startCheckPullRequest.equals(Boolean.FALSE)){
                    startCheckPullRequestTable();
                }
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
        super.exceptionCaught(ctx, cause);
    }
}


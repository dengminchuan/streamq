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
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.util.serializer.KryoSerializer;

@Slf4j
@ChannelHandler.Sharable
public class MessageServerHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    public void setMessageQueueController(MessageQueueController messageQueueController) {
        this.messageQueueController = messageQueueController;
    }

    private MessageQueueController messageQueueController;

    private final static KryoSerializer kryoSerializer = new KryoSerializer();
    private ChannelHandlerContext ctx;

    private volatile ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }
    public void pushMessageToAllConsumer(){
        //write back a FunctionMessage
       channelGroup.writeAndFlush(allocator.buffer().writeBytes("first test message".getBytes()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        ByteBuf byteBuf= (ByteBuf) msg;
//        byte[] array = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(array);

        byte[] array = getMessageBytes(msg);
        FunctionMessage functionMessage = kryoSerializer.deserialize(array, FunctionMessage.class);
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
            //

            Long offset = functionMessage.getOffset();
            pushMessageToAllConsumer();
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


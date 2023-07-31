//-*- coding =utf-8 -*-
//@Time : 2023/7/17
//@Author: 邓闽川
//@File  ConsumerPullHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.constant.MessageConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.MessageListenerConcurrently;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@ChannelHandler.Sharable
public class ConsumerPullHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final KryoSerializer kryoSerializer = new KryoSerializer();

    public void setCachedMessageCount(Integer cachedMessageCount) {
        this.cachedMessageCount = cachedMessageCount;
    }

    public Integer getCachedMessageCount() {
        return cachedMessageCount;
    }

    private Integer cachedMessageCount=0;

    public MessageListenerConcurrently getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListenerConcurrently messageListener) {
        this.messageListener = messageListener;
    }

    private MessageListenerConcurrently messageListener;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
//        byte[] array = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(array);
        System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
        //todo:store message
        cachedMessageCount++;
        byteBuf.release();
        //only push message when previous message return
        startPullMessage(ctx);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        register2Broker(ctx);
        startPullMessage(ctx);
        consumeMessage();
    }

    private void consumeMessage() {
        messageListener.consumeMessage();
        cachedMessageCount--;
    }

    private void register2Broker(ChannelHandlerContext ctx) {
        FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.REGISTER_PULL_REQUEST);
        byte[] serializeArr = kryoSerializer.serialize(functionMessage);
        int messageLength = serializeArr.length;
        ctx.channel().writeAndFlush(allocator.buffer(messageLength).writeBytes(serializeArr));
    }

    /**
     * 采用长轮询pull message,本质还是定时拉取消息，没有拉取到消息时就一直等待直到就消息才返回
     * @param ctx
     */
    public void startPullMessage(ChannelHandlerContext ctx){
                if(cachedMessageCount<=MessageConstant.PULL_MESSAGE_THRESHOLD){
                    KryoSerializer kryoSerializer = new KryoSerializer();
                    FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.PULL_MESSAGE);
                    byte[] serializeArr = kryoSerializer.serialize(functionMessage);
                    int messageLength = serializeArr.length;
                    ctx.channel().writeAndFlush(allocator.buffer(messageLength).writeBytes(serializeArr));
                }


    }



    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * Triggered when the client connection is disconnected
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        super.channelInactive(ctx);
    }


}
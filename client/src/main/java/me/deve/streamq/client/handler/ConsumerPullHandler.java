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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.constant.MessageConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageListenerConcurrently;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ChannelHandler.Sharable
public class ConsumerPullHandler extends ChannelInboundHandlerAdapter {
    private final ByteBufAllocator allocator= PooledByteBufAllocator.DEFAULT;

    private final ConcurrentHashMap<ChannelHandlerContext,Integer> consumerOffsetTable=new ConcurrentHashMap<>();

    public void setCachedMessageCount(Integer cachedMessageCount) {
        this.cachedMessageCount.set(cachedMessageCount);
    }

    @Getter
    private final AtomicReference<Integer> cachedMessageCount = new AtomicReference<>(0);

    private final LinkedBlockingQueue<Message> cacheMessage=new LinkedBlockingQueue<>();

    public void setMessageListener(MessageListenerConcurrently messageListener) {
        this.messageListener = messageListener;

    }

    @Getter
    private MessageListenerConcurrently messageListener;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf= (ByteBuf) msg;
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        byteBuf.release();
        KryoSerializer kryoSerializer = new KryoSerializer();
        FunctionMessage functionMessage = kryoSerializer.deserialize(array, FunctionMessage.class);
        if(functionMessage.getMessageType()==FunctionMessageType.NORMAL_MESSAGE){
            Message message = functionMessage.getMessage();
            System.out.println(message);
            cacheMessage.add(message);
            cachedMessageCount.getAndSet(cachedMessageCount.get() + 1);
            //only push message when previous message return
            consumerOffsetTable.put(ctx,consumerOffsetTable.getOrDefault(ctx,-1)+1);
            startPullMessage(ctx);
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("init cache message");
        register2Broker(ctx);
        startPullMessage(ctx);
//        consumeMessage(cacheMessage.get());

    }
    private final Integer CONSUMER_THREAD_COUNT =2;
    private ThreadPoolExecutor pool=new ThreadPoolExecutor(
            CONSUMER_THREAD_COUNT,
            100,
            100,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),new ThreadPoolExecutor.CallerRunsPolicy());
    private void consumeMessage(LinkedBlockingQueue<Message> messages) {
        pool.execute(() -> {
            while(true){
                Message message = messages.poll();
                messageListener.consumeMessage(message);
            }
        });
        cachedMessageCount.getAndSet(cachedMessageCount.get() - 1);
    }

    private void register2Broker(ChannelHandlerContext ctx) {
        KryoSerializer kryoSerializer = new KryoSerializer();
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
                if(cachedMessageCount.get() <=MessageConstant.PULL_MESSAGE_THRESHOLD){
                    KryoSerializer kryoSerializer = new KryoSerializer();
                    FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.PULL_MESSAGE);
                    functionMessage.setOffset(Long.valueOf(consumerOffsetTable.getOrDefault(ctx,-1)));
                    byte[] pullRequest = kryoSerializer.serialize(functionMessage);
                    int messageLength = pullRequest.length;
                    ctx.channel().writeAndFlush(allocator.buffer(messageLength).writeBytes(pullRequest));
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
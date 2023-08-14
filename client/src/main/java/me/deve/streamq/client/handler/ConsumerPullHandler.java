//-*- coding =utf-8 -*-
//@Time : 2023/7/17
//@Author: 邓闽川
//@File  ConsumerPullHandler.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.handler;

import cn.hutool.core.util.ArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.constant.MessageConstant;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageListenerConcurrently;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import me.deve.streamq.remoting.symbol.DelimiterSymbol;

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
            cacheMessage.add(message);
            cachedMessageCount.getAndSet(cachedMessageCount.get() + 1);
            //only push message when previous message return
            consumerOffsetTable.put(ctx,consumerOffsetTable.getOrDefault(ctx,-1)+1);
            System.out.println(cachedMessageCount.get());
            startPullMessage(ctx);
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active"+ctx.channel().remoteAddress());
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
    private FurySerializer furySerializer = new FurySerializer();

    private void register2Broker(ChannelHandlerContext ctx) {
        FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.REGISTER_PULL_REQUEST);
        byte[] pullRequest = furySerializer.serialize(functionMessage);
        byte[] separator = DelimiterSymbol.DELIMITER_SYMBOL;
        byte[] sendMsg = ArrayUtil.addAll(pullRequest, separator);
        ctx.channel().writeAndFlush(allocator.buffer().writeBytes(sendMsg));
    }

    /**
     * 采用长轮询pull message,本质还是定时拉取消息，没有拉取到消息时就一直等待直到就消息才返回
     * @param ctx
     */
    public void startPullMessage(ChannelHandlerContext ctx){
                if(cachedMessageCount.get() <=MessageConstant.PULL_MESSAGE_THRESHOLD){
                    FunctionMessage functionMessage = new FunctionMessage(FunctionMessageType.PULL_MESSAGE);
                    functionMessage.setOffset(Long.valueOf(consumerOffsetTable.getOrDefault(ctx,-1)));
                    byte[] pullRequest = furySerializer.serialize(functionMessage);
                    byte[] separator = DelimiterSymbol.DELIMITER_SYMBOL;
                    byte[] sendMsg = ArrayUtil.addAll(pullRequest, separator);
                    log.info("pull send msg length:"+sendMsg.length);
                    ctx.channel().writeAndFlush(allocator.buffer().writeBytes(sendMsg));
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
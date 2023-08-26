//-*- coding =utf-8 -*-
//@Time : 2023/6/15
//@Author: 邓闽川
//@File  Producer.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.producer;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.handler.ServerFindClientHandler;
import me.deve.streamq.client.handler.ProduceHandler;
import me.deve.streamq.client.loadbalance.BrokerLoadBalance;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.client.msgstrategy.BrokerChooseStrategy;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.util.IdDistributor;
import me.deve.streamq.remoting.netty.NettyClient;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;



/**
 * 默认生产者
 */
@Slf4j
public class DefaultMQProducer implements MQProducer{
    /**
     * 消息所在组
      */
    private String groupName;
    /**
     * mq服务器地址
     */
    private KryoInetAddress inetAddress;

    private NettyClient serverFindClient;

    private NettyClient messageSendClient;

    private final NettyClientConfig serverFindClientConfig;

    private NettyClientConfig messageSendClientConfig;

    private final ServerFindClientHandler serverFindClientHandler =new ServerFindClientHandler();

    private final ProduceHandler produceHandler=new ProduceHandler();

    public volatile HashMap<String, List<Broker>> topicRouteData;

    private CountDownLatch latch=new CountDownLatch(1);

    private BrokerChooseStrategy strategy= BrokerChooseStrategy.RANDOM;

    private IdDistributor idDistributor=IdDistributor.getInstance();


    private static final ThreadPoolExecutor threadPool=new ThreadPoolExecutor(
            20,
            100,
            1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public DefaultMQProducer(NettyClientConfig nettyClientConfig){
        this.serverFindClientConfig =nettyClientConfig;
        serverFindClientHandler.setProducer(this);
        serverFindClientHandler.setLatch(latch);
        serverFindClient=new NettyClient(new NioEventLoopGroup(),new Bootstrap(),serverFindClientConfig, serverFindClientHandler);
        messageSendClient=new NettyClient(new NioEventLoopGroup(),new Bootstrap(),messageSendClientConfig,produceHandler);
    }

    /**
     * obtain broker
     */
    @Override
    public void start() {
       serverFindClient.connectWithoutWaitForClose();
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("can not get topicRouteData");
        }
//        if(defaultTopic!=null){
//            BrokerLoadBalance brokerLoadBalance = new BrokerLoadBalance();
//            defaultBrokerAddress = brokerLoadBalance.chooseBroker(strategy, defaultTopic);
//             defaultSetting();
//        }

    }

    public void defaultSetting() {
        messageSendClientConfig=new NettyClientConfig(defaultBrokerAddress);
        messageSendClient.setNettyClientConfig(messageSendClientConfig);
        messageSendClient.connectWithoutWaitForClose();
    }

    @Getter
    private String defaultTopic;

    private KryoInetAddress defaultBrokerAddress;

    /**
     * 停止producer
     */
    public void shutdown(){
        serverFindClient.shutdown();
        messageSendClient.shutdown();
    }

    /**
     *
     * @param message
     * @return SendResult
     */
    public SendResult send(final Message message){
        return sendMessage(message,this.strategy);
    }
    private final BrokerLoadBalance brokerLoadBalance = new BrokerLoadBalance(this);
    private SendResult sendMessage(Message message,BrokerChooseStrategy strategy) {
        idDistributor.setIdByAnnotation(message);
        KryoInetAddress brokerAddress = brokerLoadBalance.chooseBroker(strategy,message.getTopic(),message);
        messageSendClientConfig=new NettyClientConfig(brokerAddress);
        messageSendClient.setNettyClientConfig(messageSendClientConfig);
        messageSendClient.connectWithoutWaitForClose();
        produceHandler.sendMsg(message);
        //todo:obtain return result
        return null;
    }

    public SendResult sendWithStrategy(final Message message,BrokerChooseStrategy strategy1){
        return sendMessage(message,strategy1);
    }

//    /**
//     * send to default topic
//     * @param message
//     * @return
//     */
//    public SendResult continuousSend(final Message message){
//           produceHandler.sendMsg(message);
//           return null;
//    }




    @Override
    public void setMsgStrategy(BrokerChooseStrategy strategy) {
        this.strategy=strategy;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }
}

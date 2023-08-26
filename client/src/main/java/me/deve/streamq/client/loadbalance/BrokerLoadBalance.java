//-*- coding =utf-8 -*-
//@Time : 2023/6/29
//@Author: 邓闽川
//@File  ClientUtil.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.loadbalance;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.msgstrategy.BrokerChooseStrategy;
import me.deve.streamq.client.producer.DefaultMQProducer;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.Message;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


@Slf4j
public class BrokerLoadBalance {

    private DefaultMQProducer producer;
    /**
     * RoundRobin offset
     */
    private int offset=0;

    private ConsistentHashingWithVirtualNode consistentHashingWithVirtualNode;


    public BrokerLoadBalance(){

    }
    public BrokerLoadBalance(DefaultMQProducer producer){
        this.producer=producer;
    }
    public  KryoInetAddress chooseBroker(BrokerChooseStrategy strategy, String topicName, Message message){
        List<Broker> brokers = producer.topicRouteData.get(topicName);
        return
                switch (strategy) {
                    case ROUND_ROBIN-> chooseBrokerByRoundRobin(brokers);
                    case RANDOM -> chooseBrokerByRandom(brokers);
                    case HASH ->   chooseBrokerByHash(brokers);
                    case CONSISTENCY_HASH -> chooseBrokerByConsistencyHash(brokers,message);
                };
    }

    /**
     * 一致性hash算法，即使broker数量发生变化前面的映射也不会受到影响
     * 1.节点数量发生变化时尽量保证相同线程相同tags消息发送到同一节点,单tag 单线程发送没必要使用此负载均衡方式
     * 2.只有少量生产者会被重定向到不同broker节点,broker端压力不会突然改变
     * 3.使负载均衡更加平滑
     * 4.在分布式缓存中使用效果最好,扩容缩容时防止过多数据迁移和缓存雪崩
     * 缺点:需要的消耗较大
     * @param brokers
     * @return
     */
    private  KryoInetAddress chooseBrokerByConsistencyHash(List<Broker> brokers,Message message) {
        Broker broker = consistentHashingWithVirtualNode.getNode(message, brokers);
        return broker.getInetAddress();
    }

    private  KryoInetAddress chooseBrokerByHash(List<Broker> brokers) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            int hashCode = hash(hostAddress);
            int size = brokers.size();
            return brokers.get(hashCode%size).getInetAddress();
        } catch (UnknownHostException e) {
            log.error("获取本机ip错误");
        }

        return null;
    }

    private  KryoInetAddress chooseBrokerByRandom(List<Broker> brokers) {
        if(brokers==null){
            return null;
        }
        int index = RandomUtil.randomInt(0, brokers.size());
        return brokers.get(index).getInetAddress();
    }

    /**
     * 轮询
     * @param brokers
     * @return
     */
    private  KryoInetAddress chooseBrokerByRoundRobin(List<Broker> brokers) {
        offset%=brokers.size();
        return brokers.get(offset++).getInetAddress();

    }
    public static int hash(Object obj) {
        return obj.hashCode()>>16^obj.hashCode();
    }



}

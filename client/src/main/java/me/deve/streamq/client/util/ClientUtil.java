//-*- coding =utf-8 -*-
//@Time : 2023/6/29
//@Author: 邓闽川
//@File  ClientUtil.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.util;

import cn.hutool.core.util.RandomUtil;
import me.deve.streamq.client.msgstrategy.BrokerChooseStrategy;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.component.Broker;

import java.util.HashMap;
import java.util.List;

public class ClientUtil {

    public static KryoInetAddress chooseBroker(BrokerChooseStrategy strategy, HashMap<String, List<Broker>> topicRouteData, String topicName){
        List<Broker> brokers = topicRouteData.get(topicName);
        return
                switch (strategy) {
                    case ROUND_ROBIN-> chooseBrokerByRoundRobin(brokers);
                    case RANDOM -> chooseBrokerByRandom(brokers);
                    case HASH ->   chooseBrokerByHash(brokers);
        };
    }

    private static KryoInetAddress chooseBrokerByHash(List<Broker> brokers) {

        return null;
    }

    private static KryoInetAddress chooseBrokerByRandom(List<Broker> brokers) {
        if(brokers==null){
            return null;
        }
        int index = RandomUtil.randomInt(0, brokers.size());
        return brokers.get(index).getInetAddress();
    }

    private static KryoInetAddress chooseBrokerByRoundRobin(List<Broker> brokers) {
        return null;




    }


}

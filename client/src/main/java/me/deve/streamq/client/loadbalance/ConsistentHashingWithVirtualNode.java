//-*- coding =utf-8 -*-
//@Time : 2023/8/26
//@Author: 邓闽川
//@File  ConsistentHashingWithVirtualNode.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.loadbalance;

import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.Message;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashingWithVirtualNode {
       private List<Broker> brokers;

       private final SortedMap<Integer,VirtualBroker> hashRing=new TreeMap<>();

       private final int VIRTUAL_NODE_NUMBER=10;




       public ConsistentHashingWithVirtualNode(List<Broker> brokers){
           this.brokers = brokers;
       }

       public void buildHashRing(List<Broker> brokers){
           for (Broker broker : brokers) {
               for(int i=0;i<VIRTUAL_NODE_NUMBER;i++){
                   VirtualBroker virtualBroker = new VirtualBroker(broker, i);
                   int hash = BrokerLoadBalance.hash(virtualBroker);
                   hashRing.put(hash, virtualBroker);
               }
           }
       }

    /**\
     * 获取应该发送到的节点
      * @return
     */
    public Broker getNode(Message message,List<Broker> brokers){
        //hashRing发生了变化就重建
        if(this.brokers==null||!this.brokers.equals(brokers)){
            this.brokers=brokers;
            buildHashRing(brokers);
        }
        //计算key
        int key1 = BrokerLoadBalance.hash(message.getTags());
        int key2=BrokerLoadBalance.hash(Thread.currentThread());
        int key=key1^key2;
        SortedMap<Integer, VirtualBroker> subMap = hashRing.tailMap(key);
        //说明是第一个
        if(subMap.isEmpty()){
            VirtualBroker virtualBroker = hashRing.get(hashRing.firstKey());
            return virtualBroker.getBroker();
        }else{
            VirtualBroker virtualBroker = hashRing.get(subMap.firstKey());
            return virtualBroker.getBroker();
        }
    }
}

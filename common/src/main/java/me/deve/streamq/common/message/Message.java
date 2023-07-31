//-*- coding =utf-8 -*-
//@Time : 2023/6/15
//@Author: 邓闽川
//@File  Message.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;



import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;

import java.util.Arrays;
import java.util.Map;

/**
 * 消息对象
 */
public class Message {

        public String getId() {
                return id;
        }

        private String id= IdUtil.simpleUUID();

        @Override
        public String toString() {
                return "Message{" +
                        "id='" + id + '\'' +
                        ", messageType=" + messageType +
                        ", topic='" + topic + '\'' +
                        ", MAX_MESSAGE_SIZE=" + MAX_MESSAGE_SIZE +
                        ", body=" + Arrays.toString(body) +
                        ", tags=" + Arrays.toString(tags) +
                        ", key='" + key + '\'' +
                        ", delayTimeLevel=" + delayTimeLevel +
                        ", flag=" + flag +
                        ", extraProperty=" + extraProperty +
                        '}';
        }

        /**
         * 消息类型
         */
        private MessageType messageType;

        /**
         * 消息的topic名称,全局唯一
         */
        private String topic;
        /**
         * 消息体最大大小,默认4m
         */
        private final int MAX_MESSAGE_SIZE=1024*4*4;
        /**
         * 消息体
         */
        private byte[] body;
        /**
         * 消息所属tag,可用于筛选消息
         */
        private String []tags;
        /**
         * 代表消息的关键词
         */
        private String key;
        /**
         * 消息被延迟消费的时间，默认为0
         */
        private int delayTimeLevel=0;
        /**
         * 业务自定义
          */
        private int flag=0;
        /**
         * 额外属性
         */
        private Map<String,String> extraProperty;;

        public Message(String topic,byte[]body){
                this.topic=topic;
                this.body=body;

        }
        public Message(String topic,byte[]body,String []tags){
                this(topic, body);
                this.tags=tags;

        }
        public Message(String topic,byte[]body,String []tags,String key){
                this(topic, body, tags);
                this.key=key;
        }
        public Message(){

        }
        /**
         * 用户添加额外属性
         * @param key
         * @param value
         * @return hashmap.put()
         */
        public String  addExtraProperty(String key,String value){
                 return extraProperty.put(key, value);
        }

        /**
         * 添加额外属性
         * @param porpertyMap
         */
        public void addExtraProperty(Map<String,String> porpertyMap){
                 extraProperty.putAll(porpertyMap);
        }

        public String getTopic() {
                return topic;
        }

        public void setTopic(String topic) {
                this.topic = topic;
        }
        public void setBody(byte[]body){
                this.body=body;
        }

}

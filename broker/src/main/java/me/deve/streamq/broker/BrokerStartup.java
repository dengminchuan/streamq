//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  BrokerStartup.java
//@software:IntelliJ IDEA
package me.deve.streamq.broker;

import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.broker.handler.BrokerUploadHandler;
import me.deve.streamq.broker.handler.MessageServerHandler;
import me.deve.streamq.common.config.BrokerConfig;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.config.NettyServerConfig;
import me.deve.streamq.common.address.KryoInetAddress;

import java.net.UnknownHostException;
@Slf4j
public class BrokerStartup {
    private static NettyServerConfig nettyServerConfig;
    private static  NettyClientConfig nettyClientConfig;
    private static BrokerConfig brokerConfig;

    private static ChannelInboundHandlerAdapter[] clientHandlers;
    private static ChannelInboundHandlerAdapter[] serverHandlers;


    static{
        try {
            //default nameserver address
            nettyClientConfig=new NettyClientConfig(new KryoInetAddress(KryoInetAddress.getLocalhost(),10088));
            nettyServerConfig = new NettyServerConfig(10011);
            brokerConfig=new BrokerConfig();
        } catch (UnknownHostException e) {
            log.error("get localhost error,error message:"+e.getMessage());
        }
    }
    public static void main(String[] args) {
        BrokerUploadHandler brokerUploadHandler = new BrokerUploadHandler(nettyClientConfig,brokerConfig,nettyServerConfig);
        MessageServerHandler messageServerHandler = new MessageServerHandler();
        createMessageQueue(messageServerHandler);
        setClientHandler(brokerUploadHandler);
        setServerHandler(messageServerHandler);
        parseAndLoadArgs(args);
        startup();
    }

    private static void createMessageQueue(MessageServerHandler messageServerHandler) {
        MessageQueueController messageQueueController = new MessageQueueController();
        messageServerHandler.setMessageQueueController(messageQueueController);
    }

    private static void startup() {
        BrokerController brokerController=createAndStartBrokerController();

    }

    private static void start(BrokerController brokerController) {
        brokerController.start();
    }

    private static BrokerController createAndStartBrokerController() {
        BrokerController brokerController = new BrokerController(brokerConfig,nettyClientConfig,nettyServerConfig, clientHandlers,serverHandlers);
        start(brokerController);
        return brokerController;
    }

    private static void parseAndLoadArgs(String[] args) {

         brokerConfig=new BrokerConfig();
        //todo:parse and load args to config

    }
    public static void setClientHandler(ChannelInboundHandlerAdapter ...outHandlers){
        clientHandlers =outHandlers;
    }
    public static void setServerHandler(ChannelInboundHandlerAdapter ...outHandlers){
        serverHandlers =outHandlers;
    }
}

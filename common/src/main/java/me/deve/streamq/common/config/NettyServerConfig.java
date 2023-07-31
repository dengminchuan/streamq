//-*- coding =utf-8 -*-
//@Time : 2023/6/17
//@Author: 邓闽川
//@File  NettyServerConfig.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.config;

import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyServerConfig  {
    private Integer threadConnectValue=128;

    private Boolean keepConnectAlive=true;

    private Integer bindPort=10088;

    public NettyServerConfig(Integer port){
        this.bindPort=port;
    }
}


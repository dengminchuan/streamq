//-*- coding =utf-8 -*-
//@Time : 2023/6/20
//@Author: 邓闽川
//@File  KryoInetAddress.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KryoInetAddress {
    /**
     * ip
     */
    private String address;

    private int port;
    public static String getLocalhost() throws UnknownHostException {
            return InetAddress.getLocalHost().getHostAddress();

    }
    public InetSocketAddress getInetSocketAddress(){
        return new InetSocketAddress(address,port);
    }
}

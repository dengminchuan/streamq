//-*- coding =utf-8 -*-
//@Time : 2023/8/14
//@Author: 邓闽川
//@File  DelimiterSymbol.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.symbol;

import io.netty.util.CharsetUtil;

public class DelimiterSymbol {
    public static final byte[] DELIMITER_SYMBOL = "_$".getBytes(CharsetUtil.UTF_8);
}

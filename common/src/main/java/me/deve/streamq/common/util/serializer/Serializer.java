//-*- coding =utf-8 -*-
//@Time : 2023/6/20
//@Author: 邓闽川
//@File  Serializer.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util.serializer;

public interface Serializer {
    byte [] serialize(Object object);

    <T> T deserialize(byte []byteArray,Class<T> clazz);
}

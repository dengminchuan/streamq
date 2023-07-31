//-*- coding =utf-8 -*-
//@Time : 2023/7/19
//@Author: 邓闽川
//@File  FurySerializer.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util.serializer;

import io.fury.Fury;
import io.fury.Language;
import io.fury.ThreadSafeFury;

/**
 * todo:change to FurySerializer when it official release
 */
public class FurySerializer implements Serializer{

    private Fury fury=Fury.builder()
            .withLanguage(Language.JAVA)
            .withRefTracking(true)
            .requireClassRegistration(false)
            .build();


    @Override
    public byte[] serialize(Object object) {


        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] byteArray, Class<T> clazz) {
        return null;
    }
}

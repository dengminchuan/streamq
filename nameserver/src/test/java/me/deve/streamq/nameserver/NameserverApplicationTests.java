package me.deve.streamq.nameserver;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.nameserver.manager.KVManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class NameserverApplicationTests {

    @Test
    void testFile() throws IOException {
        File file = new File("test.txt");
        file.createNewFile();


    }
    @Test
    void testJson(){
        String jsonStr = JSONUtil.toJsonStr(new KryoInetAddress("127.0.0.1", 12));
        System.out.println(JSONUtil.toBean(jsonStr, KryoInetAddress.class));

    }

}

package me.devedmc.streamq.commitlog;

import com.esotericsoftware.kryo.Kryo;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


class CommitLogApplicationTests {

    @Test
    void testPath() {
        CommitLog instance = CommitLog.getInstance();
        instance.add(new Message());
    }
    @Test
    void testFormat(){
        System.out.println(String.format("%020d", 123));
    }
    @Test
    void readFile() throws IOException {
        File file = new File("C:\\Users\\lv jiang er hao\\Desktop\\streamq\\commitLog\\00000000000000000000.bin");
        System.out.println(file.length());
//        FileInputStream fileInputStream = new FileInputStream(file);
//        byte[] bytes = fileInputStream.readAllBytes();
//        KryoSerializer kryoSerializer = new KryoSerializer();
//        System.out.println(kryoSerializer.deserialize(bytes, Message.class));
    }
}

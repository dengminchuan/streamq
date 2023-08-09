package me.deve.streamq.client;

import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;


class ClientApplicationTests {

    @Test
    void testSchedule() throws InterruptedException {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println("act");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.schedule(timerTask,0,1000);
        Thread.sleep(1000000);
    }
    @Test
    void testRandomRead() throws IOException {
        RandomAccessFile r = new RandomAccessFile(new File("C:\\Users\\lv jiang er hao\\Desktop\\streamq\\00000000000000000000.bin"), "r");
        byte[] bytes = new byte[1024];
        r.seek(54);
        r.read(bytes,0,27);
        KryoSerializer kryoSerializer = new KryoSerializer();
        Message deserialize = kryoSerializer.deserialize(bytes, Message.class);
        System.out.println(deserialize);
    }
}

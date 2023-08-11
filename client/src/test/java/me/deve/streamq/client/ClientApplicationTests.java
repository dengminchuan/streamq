package me.deve.streamq.client;

import cn.hutool.core.lang.hash.Hash;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;


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
    @Test
    void testMartix(){
        int[][] matrix={{1,2,3,4},{5,6,7,8},{9,10,11,12}};
        List<Integer> list=new ArrayList<>();
        int cnt=1;
        int maxCnt=matrix.length*matrix[0].length;
        //横着
        int step1=matrix[0].length-1;
        //竖着
        int step2=matrix.length-1;
        int i=0;
        int j=0;
        while(cnt<=maxCnt){
            if(step1==0&&step2==0){
                list.add(matrix[i][j]);
                break;
            }else if(step1==0){
                while(cnt<=maxCnt){
                    list.add(matrix[i++][j]);
                    cnt++;
                    break;
                }
            }else if(step2==0){
                while(cnt<=maxCnt){
                    list.add(matrix[i][j++]);
                    cnt++;
                    break;
                }
            }

            for(int k=0;k<step1;k++){
                list.add(matrix[i][j++]);
                cnt++;
            }
            if(cnt>maxCnt){
                break;
            }
            for(int k=0;k<step2;k++){
                list.add(matrix[i++][j]);
                cnt++;
            }
            if(cnt>maxCnt){
                break;
            }
            for(int k=0;k<step1;k++){
                list.add(matrix[i][j--]);
                cnt++;
            }
            if(cnt>maxCnt){
                break;
            }
            for(int k=0;k<step1 ;k++){
                list.add(matrix[i--][j]);
                cnt++;
            }
            if(cnt>maxCnt){
                break;
            }
            step1-=2;
            step2-=2;
            i++;
            j++;
        }
    }


}

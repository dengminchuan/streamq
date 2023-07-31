package me.deve.streamq.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.fury.Fury;
import io.fury.Language;
import io.fury.ThreadSafeFury;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.util.FileUtil;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


class CommonApplicationTests {

    @Test
    void testHost() throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
    @Test
    void testKyroSer(){
        Kryo kryo = new Kryo();
        kryo.register(KryoInetAddress.class);
        KryoInetAddress kryoInetAddress = new KryoInetAddress("127.0.0.1",52);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output,kryoInetAddress);
        byte[] byteArray = output.toBytes();
        Input input = new Input(byteArray);
        KryoInetAddress kryoInetAddress1 = kryo.readObject(input, KryoInetAddress.class);
        System.out.println(kryoInetAddress1);

    }
    @Test
    void testRegistry(){
        Kryo kryo = new Kryo();
        registerAllClass(FunctionMessage.class, kryo);
    }
    private void registerAllClass(Class clazz,Kryo kryo){
        Field[] declaredFields = clazz.getDeclaredFields();
        if(declaredFields.length==0){
            return;
        }
        for (int i = 0; i < declaredFields.length; i++) {
            Class<?> type = declaredFields[i].getType();
            kryo.register(type);
            registerAllClass(declaredFields[i].getClass(),kryo);
        }
    }
    @Test
    void testFileUtil(){
        File file = new File("test.conf");
        FileUtil.string2File(file,"test data",false);
        boolean replace = FileUtil.replace(file, new File("testNew.conf"));
        System.out.println(replace);
    }
    @Test
    void testTimer(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(111);
            }
        },1000,1000);

    }
    @Test
    void testFury(){
        Fury fury= Fury.builder()
                .withLanguage(Language.JAVA)
                .withRefTracking(true)
                .requireClassRegistration(false)
                .build();

        byte[] serialize = fury.serialize(new Message("test", "test message".getBytes()));
        Message deserialize = (Message) fury.deserialize(serialize);
        System.out.println(deserialize);
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(111);
            }
        },1000,1000);
        System.out.println("timer end");
    }
}


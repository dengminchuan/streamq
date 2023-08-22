package me.deve.streamq.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.fury.Fury;
import io.fury.Language;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageInfo;
import me.deve.streamq.common.message.MessageType;
import me.deve.streamq.common.util.FileUtil;
import me.deve.streamq.common.util.IdDistributor;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    void testLength(){
        MessageInfo messageInfo = new MessageInfo(12L, 32L);
        System.out.println(messageInfo);
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            executorService.execute(() -> {
                KryoSerializer kryoSerializer = new KryoSerializer();
                byte[] serialize = kryoSerializer.serialize(new Message("test", "test topic".getBytes()));
                Message deserialize = kryoSerializer.deserialize(serialize, Message.class);
                System.out.println(deserialize);
            });
    }




    }
    @Test
    void test(){
        FurySerializer furySerializer = new FurySerializer();
        byte[] serialize = furySerializer.serialize(new Message("topic", "body".getBytes()));
        System.out.println(furySerializer.deserialize(serialize,Message.class));

    }
    @Test
    void testFury(){
        Message object = new Message("topic","body".getBytes());
        // 注意应该在多次序列化之间复用Fury实例
        {
            Fury fury = Fury.builder().withLanguage(Language.JAVA)
                    // 允许反序列化未知类型，如果未知类型包含恶意代码则会有安全风险
                    // .requireClassRegistration(false)
                    .build();
            KryoSerializer kryoSerializer = new KryoSerializer();
            // 注册类型可以减少类名称序列化，但不是必须的。
            // 如果安全模式开启(默认开启)，所有自定义类型必须注册。
            fury.register(Message.class);
            fury.register(MessageType.class);
            byte[] bytes =
            kryoSerializer.serialize(object);
            System.out.println(fury.deserialize(bytes));
        }
    }
    @Test
    void testIdDistributer(){
        IdDistributor instance = IdDistributor.getInstance();
        long begin=System.currentTimeMillis();
        for(int i=0;i<1000000;i++){
            Message message = new Message("test", "test message".getBytes());
            instance.setIdByAnnotation(message);
        }
        long end=System.currentTimeMillis();
        System.out.println(end-begin);
    }


}


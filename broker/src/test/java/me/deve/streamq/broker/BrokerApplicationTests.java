package me.deve.streamq.broker;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.queue.ProcessQueue;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import me.devedmc.streamq.commitlog.CommitLog;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;


class BrokerApplicationTests {

    @Test
    void contextLoads() {
        Kryo kryo = new Kryo();
        kryo.register(KryoInetAddress.class);
        KryoInetAddress kryoInetAddress = new KryoInetAddress("127.0.0.1",52);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        output.flush();
        kryo.writeObject(output,kryoInetAddress);
        byte[] byteArray = output.toBytes();
        Input input = new Input(byteArray);
        KryoInetAddress kryoInetAddress1 = kryo.readObject(input, KryoInetAddress.class);
        System.out.println(kryoInetAddress1);
    }
    @Test
    void testSerialzer(){
        FunctionMessage functionMessage = new FunctionMessage( FunctionMessageType.BROKER_REGISTRY,new Broker(1L,"just",new KryoInetAddress("127.0.0.1",52),"first topic"));
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] serializeByteArr = kryoSerializer.serialize(functionMessage);
        FunctionMessage functionMessage2 = kryoSerializer.deserialize(serializeByteArr, FunctionMessage.class);
        System.out.println(functionMessage2);
    }
    @Test
    void testCombineBytes(){
        byte[] bytes = CommitLog.combineBytes("i love".getBytes(), "you".getBytes());
        System.out.println(new String(bytes));

    }
    @Test
    void testPath(){
        System.out.println(System.getProperty("user.dir"));
    }
    @Test
    void testMessageQueueController(){

        MessageQueueController messageQueueController = new MessageQueueController();
        messageQueueController.add(new Message("test1","this is a test message1".getBytes()));
        messageQueueController.add(new Message("test2","hello".getBytes()));
        messageQueueController.add(new Message("test3","streamq come on".getBytes()));
        messageQueueController.add(new Message("test4","wow".getBytes()));
        messageQueueController.add(new Message("test5","User username={wow}".getBytes()));
        messageQueueController.add(new Message("test6","last".getBytes()));
    }
    @Test
    void testDeserialize() throws IOException {
        KryoSerializer kryoSerializer = new KryoSerializer();
        File file = new File("C:\\Users\\lv jiang er hao\\Desktop\\streamq\\broker\\messageQueue.log");
        byte[] bytes = new FileInputStream(file).readAllBytes();
        ProcessQueue deserialize = kryoSerializer.deserialize(bytes, ProcessQueue.class);
        System.out.println(deserialize.toString());
    }
    @Test
    void testGetFile(){
        File folder = new File("C:\\Users\\lv jiang er hao\\Desktop\\java\\git\\streamq");
        System.out.println(folder.getAbsolutePath());
        File[] files = folder.listFiles();
        List<File> matchingFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().matches("\\d{20}\\.bin")) {
                matchingFiles.add(file);
            }
        }

        // 处理匹配的文件
        for (File file : matchingFiles) {
            System.out.println(file.getName());
            // 在这里可以对匹配的文件进行进一步的操作
        }

    }
    @Test
    void testKryo(){
        Message message = new Message("test", "test message".getBytes());
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] serialize = kryoSerializer.serialize(message);
        byte[] bytes = "wow".getBytes();
        byte[] newSerialize = new byte[serialize.length+bytes.length];
        for(int i=0;i<serialize.length;i++){
            newSerialize[i]=serialize[i];
        }
        for(int i=serialize.length;i<bytes.length+serialize.length;i++){
            newSerialize[i]=bytes[i-serialize.length];
        }
        Message deserialize = kryoSerializer.deserialize(newSerialize, Message.class);
        System.out.println(deserialize);

    }
    @Test
    void testMessageQueue(){
        MessageQueueController messageQueueController = new MessageQueueController();
        Message message = messageQueueController.readMessage(99L);
    }




}

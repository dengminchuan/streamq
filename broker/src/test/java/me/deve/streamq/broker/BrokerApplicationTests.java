package me.deve.streamq.broker;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.util.serializer.KryoSerializer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;


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

}

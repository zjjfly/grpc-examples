package com.siemens.datalayer.realtime.rpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.siemens.datalayer.realtime.rpc.api.EndPointGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author zijunjie[[https://github.com/zjjfly]]
 * @date 2021/10/9
 */
public class Client {

  private final ManagedChannel channel;

  private final EndPointGrpc.EndPointBlockingStub stub;

  private static final String host = "127.0.0.1";

  private static final int ip = 50051;

  public Client(String host, int port) {
    //usePlaintext表示明文传输，否则需要配置ssl
    //channel  表示通信通道
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    //存根
    stub = EndPointGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void read() {
    Empty request = Empty.newBuilder().build();
    Iterator<Struct> structIterator = stub.read(request);
    structIterator.forEachRemaining(struct -> {
      struct.getFieldsMap().forEach((s, value) -> {
        System.out.println(s + " = " + value);
      });
    });
  }

  public static void main(String[] args) {
    Client client = new Client(host, ip);
    client.read();
  }

}

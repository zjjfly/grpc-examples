package io.grpc.examples;

import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author zijunjie[[https://github.com/zjjfly]]
 * @date 2021/10/9
 */
public class GreeterClient {

  private final ManagedChannel channel;

  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  private static final String host = "127.0.0.1";

  private static final int ip = 50051;

  public GreeterClient(String host, int port) {
    //usePlaintext表示明文传输，否则需要配置ssl
    //channel  表示通信通道
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    //存根
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void testResult(String name) {
    Empty request = Empty.newBuilder().build();
    Struct response = blockingStub.testSomeThing(request);
    System.out.println(response.getFieldsMap());
  }

  public static void main(String[] args) {
    GreeterClient client = new GreeterClient(host, ip);
    for (int i = 0; i <= 5; i++) {
      client.testResult("<<<<<result>>>>>:" + i);
    }
  }

}

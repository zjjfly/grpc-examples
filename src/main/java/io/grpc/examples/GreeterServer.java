package io.grpc.examples;

import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * @author zijunjie[[https://github.com/zjjfly]]
 * @date 2021/10/9
 */
public class GreeterServer {

  /**
   * 定义端口
   */
  private final int port = 50051;

  /**
   * 服务
   */
  private Server server;

  /**
   * 启动服务,并且接受请求
   */
  private void start() throws IOException {
    server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();
    System.out.println("服务开始启动-------");
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("------shutting down gRPC server since JVM is shutting down-------");
        GreeterServer.this.stop();
        System.err.println("------server shut down------");
      }
    });
  }

  /**
   * 实现服务接口的类
   */
  private class GreeterImpl extends io.grpc.examples.GreeterGrpc.GreeterImplBase {

    @Override
    public void testSomeThing(Empty request, StreamObserver<Struct> responseObserver) {
      Struct build = Struct.newBuilder()
          .putFields("end-time", Value.newBuilder().setNumberValue(System.currentTimeMillis()).build())
          .build();
      //onNext()方法向客户端返回结果
      responseObserver.onNext(build);
      //告诉客户端这次调用已经完成
      responseObserver.onCompleted();
    }
  }

  /**
   * stop服务
   */
  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * server阻塞到程序退出
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final GreeterServer server = new GreeterServer();
    server.start();
    server.blockUntilShutdown();
  }
}

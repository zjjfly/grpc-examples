package com.siemens.datalayer.realtime.rpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.protobuf.Struct.Builder;
import com.google.protobuf.Value;
import com.siemens.datalayer.realtime.rpc.api.EndPointGrpc;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

/**
 * @author zijunjie[[https://github.com/zjjfly]]
 * @date 2021/10/9
 */
public class Server {

  /**
   * 定义端口
   */
  private final int port = 50051;

  /**
   * 服务
   */
  private io.grpc.Server server;

  /**
   * 启动服务,并且接受请求
   */
  private void start() throws IOException {
    server = ServerBuilder.forPort(port).addService(new EndPointImpl()).build().start();
    System.out.println("服务开始启动-------");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("------shutting down gRPC server since JVM is shutting down-------");
      Server.this.stop();
      System.err.println("------server shut down------");
    }));
  }

  /**
   * 实现服务接口的类
   */
  private static class EndPointImpl extends EndPointGrpc.EndPointImplBase {

    @Override
    public void read(Empty request, StreamObserver<Struct> responseObserver) {
      for (int i = 0; i < 5; i++) {
        Builder builder = Struct.newBuilder();
        builder.putFields("id", Value.newBuilder().setNumberValue(i).build());
        builder.putFields("name", Value.newBuilder().setStringValue("no." + i).build());
        responseObserver.onNext(builder.build());
      }
      //告诉客户端这次调用已经完成
      responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Struct> write(StreamObserver<Empty> responseObserver) {
      return super.write(responseObserver);
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
    final Server server = new Server();
    server.start();
    server.blockUntilShutdown();
  }
}

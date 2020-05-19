package com.grpc.demo.test;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.grpc.demo.api.GreeterGrpc;
import com.grpc.demo.api.HelloReply;
import com.grpc.demo.api.HelloRequest;
import com.grpc.demo.client.GrpcClient;
import com.grpc.demo.constants.GrpcConst;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 罗涛
 * @title GrpcTest
 * @date 2020/5/19 10:33
 */
@Slf4j
public class GrpcTest {
    public static void main(String[] args) throws Exception {
        GrpcClient client = new GrpcClient(GrpcConst.GRPC_SERVER_HOST, GrpcConst.GPRC_SERVER_PORT);
        try {
            // 同步请求，并等待结果返回
            GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(client.channel);
            HelloRequest request = HelloRequest.newBuilder().setName("GRPC").build();
//            HelloReply reply = blockingStub.sayHello(request);
//            log.info("收到服务器回复：{}",reply.getMessage());

            // 异步请求，加入callback
            GreeterGrpc.GreeterFutureStub futureStub = GreeterGrpc.newFutureStub(client.channel);
            ListenableFuture<HelloReply> replyFuture = futureStub.sayHello(request);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Futures.addCallback(replyFuture, new FutureCallback<HelloReply>() {
                @Override
                public void onSuccess(@Nullable HelloReply result) {
                    log.info("收到服务器回复: {}", result.getMessage());
                    executor.shutdown();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    log.error("rpc过程发生异常：{}",throwable);
                    executor.shutdown();
                }
            }, executor);
            log.info("rpc请求已发送！");
        } finally {
            client.shutdown();
        }
    }
}

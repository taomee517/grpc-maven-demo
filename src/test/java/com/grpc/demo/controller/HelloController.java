package com.grpc.demo.controller;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.grpc.demo.api.GreeterGrpc;
import com.grpc.demo.api.HelloReply;
import com.grpc.demo.api.HelloRequest;
import com.grpc.demo.client.GrpcClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.support.ExecutorServiceAdapter;

import javax.annotation.Nullable;
import java.util.concurrent.*;

/**
 * HelloWorldClient
 *
 * @Author: taomee
 * @Date: 2020/5/18 0018 22:00
 * @Description:
 */
@Slf4j
public class HelloController {
    private GreeterGrpc.GreeterBlockingStub blockingStub;
    private GreeterGrpc.GreeterFutureStub futureStub;

    public HelloController(GrpcClient client){
        blockingStub = GreeterGrpc.newBlockingStub(client.channel);
        futureStub = GreeterGrpc.newFutureStub(client.channel);
    }

    public String greet(String name) {
        try {
            log.info("Will try to greet " + name + " ...");
            HelloRequest request = HelloRequest.newBuilder().setName(name).build();
            HelloReply response;
            response = blockingStub.sayHello(request);
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            log.error("RPC failed: {0}", e.getStatus());
            return null;
        }
    }

    public CompletableFuture<String> asynGreet(String name) {
        CompletableFuture<String> replyMsg = new CompletableFuture<>();
        try {
            log.info("Will try to greet " + name + " ...");
            HelloRequest request = HelloRequest.newBuilder().setName(name).build();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            ListenableFuture<HelloReply> replyFuture = futureStub.sayHello(request);
            Futures.addCallback(replyFuture, new FutureCallback<HelloReply>() {
                @Override
                public void onSuccess(@Nullable HelloReply result) {
                    log.info("收到服务器回复: {}", result.getMessage());
                    replyMsg.complete(result.getMessage());
                    executor.shutdown();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    log.error("rpc过程发生异常：{}",throwable);
                    replyMsg.completeExceptionally(throwable);
                    executor.shutdown();
                }
            }, executor);
            return replyMsg;
        } catch (Exception e) {
            log.error("RPC failed: {}", e);
            return null;
        }
    }
}

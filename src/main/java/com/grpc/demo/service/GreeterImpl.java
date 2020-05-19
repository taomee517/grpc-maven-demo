package com.grpc.demo.service;

import com.grpc.demo.api.GreeterGrpc;
import com.grpc.demo.api.HelloReply;
import com.grpc.demo.api.HelloRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 罗涛
 * @title GreeterImpl
 * @date 2020/5/19 9:53
 */
@Slf4j
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        log.info("执行sayHello方法！");
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
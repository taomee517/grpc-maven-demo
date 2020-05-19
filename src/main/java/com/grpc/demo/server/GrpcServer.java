package com.grpc.demo.server;

import com.grpc.demo.service.GreeterImpl;
import com.grpc.demo.service.SearchServiceImpl;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * HelloWorldServer
 *
 * @Author: taomee
 * @Date: 2020/5/18 0018 20:45
 * @Description:
 */
@Slf4j
@Component
public class GrpcServer implements SmartLifecycle {

    private Server server;

    @Override
    public void start() {
        try {
            /* The port on which the server should run */
            int port = 50051;
            server = ServerBuilder.forPort(port)
                    .addService((BindableService) new GreeterImpl())
                    .addService((BindableService) new SearchServiceImpl())
                    .build()
                    .start();
            log.info("Server started, listening on " + port);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    // Use stderr here since the log may have been reset by its
                    // JVM shutdown hook.
                    System.err.println("*** shutting down gRPC server since JVM is shutting down");
                    GrpcServer.this.stop();
                    System.err.println("*** server shut down");
                }
            });
            blockUntilShutdown();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("server start error：{}", e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}

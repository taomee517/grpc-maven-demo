package com.grpc.demo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * SearchClient
 *
 * @Author: taomee
 * @Date: 2020/5/18 0018 22:00
 * @Description:
 */
@Slf4j
public class GrpcClient {
    public final ManagedChannel channel;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public GrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example
                // we disable TLS to avoid
                // needing certificates.
                .usePlaintext());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing
     * channel.
     */
    GrpcClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}

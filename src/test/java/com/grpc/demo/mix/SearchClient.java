package com.grpc.demo.mix;

import com.grpc.demo.api.Search;
import com.grpc.demo.api.SearchServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
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
public class SearchClient {
    private final ManagedChannel channel;
    private final SearchServiceGrpc.SearchServiceBlockingStub blockingStub;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public SearchClient(String host, int port) {
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
    SearchClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = SearchServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** rpc search */
    public void search(String keyword) {
        log.info("Will try to search " + keyword + " ...");
        Search.SearchRequest request = Search.SearchRequest.newBuilder()
                .setRequest(keyword)
                .build();
        Search.SearchResponse response;
        try {
            response = blockingStub.search(request);
        } catch (StatusRuntimeException e) {
            log.error("RPC failed: {0}", e.getStatus());
            return;
        }
        log.info("Searching: " + response.getResponse());
    }

    public static void main(String[] args) throws Exception {
        SearchClient client = new SearchClient("localhost", 50051);
        try {
            String keyword = "virus";
            if (args.length > 0) {
                keyword = args[0];
            }
            client.search(keyword);
        } finally {
            client.shutdown();
        }
    }
}

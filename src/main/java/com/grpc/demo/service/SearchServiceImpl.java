package com.grpc.demo.service;

import com.grpc.demo.api.Search;
import com.grpc.demo.api.SearchServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @author 罗涛
 * @title SearchServiceImpl
 * @date 2020/5/19 10:15
 */
public class SearchServiceImpl extends SearchServiceGrpc.SearchServiceImplBase {
    @Override
    public void search(Search.SearchRequest request, StreamObserver<Search.SearchResponse> responseObserver) {
        Search.SearchResponse response = Search.SearchResponse.newBuilder()
                .setResponse("Search Data:" + request.getRequest())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

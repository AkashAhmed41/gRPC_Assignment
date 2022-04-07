package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.Service;

import java.io.IOException;

public class GRPCServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting GRPC Server.....");

        Server server = ServerBuilder.forPort(8080).addService(new Service()).build();
        server.start();
        System.out.println("Server started at port : " + server.getPort());

        server.awaitTermination();
    }
}

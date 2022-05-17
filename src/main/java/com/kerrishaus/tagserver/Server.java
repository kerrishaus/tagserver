package com.kerrishaus.tagserver;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class Server
{
    public static void main(String[] args)
    {
        System.out.println("Starting TagServer.");

        String host = "localhost";
        int port = 8887;

        WebSocketServer server = new TagServer(new InetSocketAddress(host, port));
        server.run();
    }
}

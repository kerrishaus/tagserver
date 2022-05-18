package com.kerrishaus.tagserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class TagServer extends org.java_websocket.server.WebSocketServer
{
    public HashMap<Integer, Player> players = new HashMap<>();

    public TagServer(InetSocketAddress address)
    {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        System.out.println("New connection established: " + conn.getRemoteSocketAddress());

        Player newPlayer = new Player(players.size() + 1, new Vector3());
        players.put(newPlayer.id, newPlayer);

        conn.send("1:" + newPlayer.id + ":" + newPlayer.position.x + ";" + newPlayer.position.y + ";" + newPlayer.position.z);
        broadcast("2:" + newPlayer.id + ":" + newPlayer.position.x + ";" + newPlayer.position.y + ";" + newPlayer.position.z);

        for (HashMap.Entry<Integer, Player> player : players.entrySet())
            if (player.getValue().id != newPlayer.id)
                conn.send("2:" + player.getKey() + ":" + player.getValue().position.x + ";" + player.getValue().position.y + ";" + player.getValue().position.z);

        System.out.println("New player created: " + newPlayer.id);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        System.out.println(conn.getRemoteSocketAddress() + ": " + message);

        String[] messageInfo = message.split(":");

        switch (messageInfo[0])
        {
            case "1": // Assigning a new player. The server should not receive this command.
            case "2": // A new player joined the game. The server should not receive this message.
                System.out.println("The server received a client-bound command.");
                break;
            case "3": // A player moved. The server should receive this message from the moving client and then rebroadcast it to all other clients.
            {
                if (messageInfo[1].equals("-1"))
                    break;

                broadcast(message);
                System.out.println("A client is moving.");
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message)
    {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart()
    {
        System.out.println("TagServer is ready.");
    }
}
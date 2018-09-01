package com.duvarapps.bitfinexwebsocket.helper;

public interface ServerListener
{
    void onNewMessage(String message);

    void onStatusChange(ConnectionStatus status);
}

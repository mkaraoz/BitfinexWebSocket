package com.duvarapps.bitfinexwebsocket.callback.command;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONObject;

public class ConnectionHeartbeatCallback implements CommandCallbackHandler
{
    @Override
    public void handleChannelData(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject)
    {
        bitfinexBroker.updateConnectionHeartbeat();
    }
}

package com.duvarapps.bitfinexwebsocket.callback.command;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONObject;

public class DoNothingCommandCallback implements CommandCallbackHandler
{
    @Override
    public void handleChannelData(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject)
    {
        // Do nothing
    }
}

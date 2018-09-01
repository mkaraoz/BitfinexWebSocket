package com.duvarapps.bitfinexwebsocket.callback.channel;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONArray;

public interface ChannelCallbackHandler
{
    void handleChannelData(final BitfinexBroker bitfinexBroker, final String channelSymbol, final JSONArray jsonArray);
}

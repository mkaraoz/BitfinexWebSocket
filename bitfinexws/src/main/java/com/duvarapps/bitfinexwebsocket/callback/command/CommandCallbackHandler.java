package com.duvarapps.bitfinexwebsocket.callback.command;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONException;
import org.json.JSONObject;

public interface CommandCallbackHandler
{
    void handleChannelData(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject) throws JSONException;
}

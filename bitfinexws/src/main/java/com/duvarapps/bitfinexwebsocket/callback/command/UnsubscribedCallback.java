package com.duvarapps.bitfinexwebsocket.callback.command;

import android.util.Log;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONException;
import org.json.JSONObject;

public class UnsubscribedCallback implements CommandCallbackHandler
{
    private static final String TAG = UnsubscribedCallback.class.getName();

    @Override
    public void handleChannelData(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject) throws JSONException
    {
        final int channelId = jsonObject.getInt("chanId");
        final String symbol = bitfinexBroker.getFromChannelSymbolMap(channelId);
        Log.d(TAG, "Channel is unsubscribed: " + channelId + ":" + symbol);

        bitfinexBroker.removeChannel(channelId);
    }
}

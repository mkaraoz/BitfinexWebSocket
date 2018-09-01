package com.duvarapps.bitfinexwebsocket.callback.command;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

public class SubscribedCallback implements CommandCallbackHandler
{
    private static final String TAG = SubscribedCallback.class.getName();

    @Override
    public void handleChannelData(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject) throws JSONException
    {
        /*
         * "event": "subscribed",
         * "channel": "ticker",
         * "chanId": 179,
         * "symbol": "tBTCUSD",
         * "pair": "BTCUSD"
         */

        final String channel = jsonObject.getString("channel");
        final int channelId = jsonObject.getInt("chanId");

        switch (channel)
        {
            case "ticker":
                handleTickerCallback(bitfinexBroker, jsonObject, channelId);
                break;
            default:
                Log.e(TAG, "Unknown or not supported channel {} " + jsonObject.toString());
        }

    }

    private void handleTickerCallback(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject, final int channelId) throws JSONException
    {
        final String tickerSymbol = jsonObject.getString("symbol");
        bitfinexBroker.addToChannelSymbolMap(channelId, tickerSymbol);
    }
}

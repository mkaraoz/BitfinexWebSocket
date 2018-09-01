package com.duvarapps.bitfinexwebsocket.callback.command;

import android.util.Log;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONObject;

public class ErrorCallback implements CommandCallbackHandler
{
    private static final String TAG = ErrorCallback.class.getName();

    @Override
    public void handleChannelData(final BitfinexBroker bitfinexBroker, final JSONObject jsonObject)
    {
        // {"channel":"ticker","symbol":"tLTCUSD","event":"error","msg":"subscribe: dup","code":10301,"pair":"LTCUSD"}
        Log.e(TAG, "Got error callback: " + jsonObject.toString());
    }
}

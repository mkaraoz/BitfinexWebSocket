package com.duvarapps.bitfinexwebsocket.commands;

import android.util.Log;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONException;
import org.json.JSONObject;

public class SubscribeTickerCommand extends AbstractAPICommand
{
    private String symbol;

    public SubscribeTickerCommand(final String symbol)
    {
        this.symbol = symbol;
    }

    @Override
    public String getCommand(final BitfinexBroker bitfinexBroker)
    {
        final JSONObject subscribeJson = new JSONObject();
        try
        {
            subscribeJson.put("event", "subscribe");
            subscribeJson.put("channel", "ticker");
            subscribeJson.put("symbol", symbol);
        }
        catch (JSONException e)
        {
            Log.e("MK", e.getMessage(), e);
        }

        return subscribeJson.toString();
    }
}

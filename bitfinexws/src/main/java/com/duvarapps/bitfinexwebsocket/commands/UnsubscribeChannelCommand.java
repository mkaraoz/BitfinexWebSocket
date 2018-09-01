package com.duvarapps.bitfinexwebsocket.commands;

import android.util.Log;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONException;
import org.json.JSONObject;

public class UnsubscribeChannelCommand extends AbstractAPICommand
{
    private final int mChannel;

    public UnsubscribeChannelCommand(final int channel)
    {
        mChannel = channel;
    }

    @Override
    public String getCommand(final BitfinexBroker bitfinexApiBroker)
    {
        final JSONObject subscribeJson = new JSONObject();
        try
        {
            subscribeJson.put("event", "unsubscribe");
            subscribeJson.put("chanId", mChannel);
        }
        catch (JSONException e)
        {
            Log.e("MK", e.getMessage(), e);
        }
        return subscribeJson.toString();
    }
}
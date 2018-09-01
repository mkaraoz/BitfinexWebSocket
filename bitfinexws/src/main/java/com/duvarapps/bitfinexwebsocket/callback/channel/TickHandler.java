package com.duvarapps.bitfinexwebsocket.callback.channel;

import android.util.Log;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;
import com.duvarapps.bitfinexwebsocket.entity.BitfinexTicker;

import org.json.JSONArray;
import org.json.JSONException;

public class TickHandler implements ChannelCallbackHandler
{
    private static final String TAG = TickHandler.class.getName();

    @Override
    public void handleChannelData(final BitfinexBroker bitfinexBroker, final String channelSymbol, final JSONArray jsonArray)
    {
        // 0 = BID
        // 1 = BID SIZE
        // 2 = ASK
        // 3 = ASK SIZE
        // 4 = Daily Change
        // 5 = Daily Change %
        // 6 = Last Price
        // 7 = Volume
        // 8 = High
        // 9 = Low

        BitfinexTicker ticker = new BitfinexTicker();
        try
        {
            ticker.setSymbol(channelSymbol);
            ticker.setBid(jsonArray.getDouble(0));
            ticker.setBidSize(jsonArray.getDouble(1));
            ticker.setAsk(jsonArray.getDouble(2));
            ticker.setAskSize(jsonArray.getDouble(3));
            ticker.setDailyChange(jsonArray.getDouble(4));
            ticker.setDailyChangePerc(jsonArray.getDouble(5));
            ticker.setLastPrice(jsonArray.getDouble(6));
            ticker.setVolume(jsonArray.getDouble(7));
            ticker.setHigh(jsonArray.getDouble(8));
            ticker.setLow(jsonArray.getDouble(9));
        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }

        bitfinexBroker.getQuoteManager().handleNewTick(channelSymbol, ticker);
    }
}

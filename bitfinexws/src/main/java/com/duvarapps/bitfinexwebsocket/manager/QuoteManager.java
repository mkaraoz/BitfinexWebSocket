package com.duvarapps.bitfinexwebsocket.manager;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;
import com.duvarapps.bitfinexwebsocket.callback.channel.TickerCallback;
import com.duvarapps.bitfinexwebsocket.commands.SubscribeTickerCommand;
import com.duvarapps.bitfinexwebsocket.commands.UnsubscribeChannelCommand;
import com.duvarapps.bitfinexwebsocket.entity.BitfinexTicker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuoteManager
{
    private TickerCallback tickerCallback;
    private final BitfinexBroker bitfinexBroker;
    private final Map<String, Long> lastTickerActivity;

    public QuoteManager(final BitfinexBroker bitfinexBroker)
    {
        this.bitfinexBroker = bitfinexBroker;
        this.lastTickerActivity = new ConcurrentHashMap<>();
    }

    public void registerTickCallback(final TickerCallback callback)
    {
        this.tickerCallback = callback;
    }

    public void subscribeTicker(final String tickerSymbol)
    {
        final SubscribeTickerCommand command = new SubscribeTickerCommand(tickerSymbol);
        bitfinexBroker.sendCommand(command);
    }

    public void unsubscribeTicker(final String tickerSymbol)
    {
        lastTickerActivity.remove(tickerSymbol);

        final int channel = bitfinexBroker.getChannelForSymbol(tickerSymbol);

        if (channel == -1)
        {
            throw new IllegalArgumentException("Unknown symbol: " + tickerSymbol);
        }

        final UnsubscribeChannelCommand command = new UnsubscribeChannelCommand(channel);
        bitfinexBroker.sendCommand(command);
        bitfinexBroker.removeChannelForSymbol(tickerSymbol);
    }

    public void handleNewTick(final String currencyPair, final BitfinexTicker tick)
    {
        updateChannelHeartbeat(currencyPair);
        tickerCallback.handleEvent(currencyPair, tick);
    }

    public void updateChannelHeartbeat(String channelSymbol)
    {
        lastTickerActivity.put(channelSymbol, System.currentTimeMillis());
    }
}

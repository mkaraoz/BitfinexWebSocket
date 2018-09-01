package com.duvarapps.bitfinexwebsocket.callback.channel;

import com.duvarapps.bitfinexwebsocket.entity.BitfinexTicker;

public interface TickerCallback
{
    void handleEvent(String currencyPair, BitfinexTicker tick);
}

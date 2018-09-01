# BitfinexWebSocket
Overly simplified Android version of bitfinex-v2-wss-api-java library. For a fully functioning lib check out: https://github.com/jnidzwetzki/bitfinex-v2-wss-api-java/tree/master/src/main/java/com/github/jnidzwetzki/bitfinex/v2

Only subscribes to Ticker channel.
```
String SERVER_URL = "wss://api.bitfinex.com/ws/2";
BitfinexBroker mBitfinexBroker = new BitfinexBroker(SERVER_URL);
mBitfinexBroker.connect();

TickerCallback callback = new TickerCallback()
{
    @Override
    public void handleEvent(String currencyPair, BitfinexTicker tick)
    {
        Log.d("_MK", tick.getSymbol() + " " + tick.getLastPrice());
    }
};

final QuoteManager quoteManager = mBitfinexBroker.getQuoteManager();
quoteManager.registerTickCallback(callback);
quoteManager.subscribeTicker("tBTCUSD");
```

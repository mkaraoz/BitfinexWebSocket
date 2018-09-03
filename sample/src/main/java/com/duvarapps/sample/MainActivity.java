package com.duvarapps.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;
import com.duvarapps.bitfinexwebsocket.callback.channel.TickerCallback;
import com.duvarapps.bitfinexwebsocket.entity.BitfinexTicker;
import com.duvarapps.bitfinexwebsocket.manager.QuoteManager;

public class MainActivity extends AppCompatActivity
{
    private final String SERVER_URL = "wss://api.bitfinex.com/ws/2";
    private BitfinexBroker mBitfinexBroker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitfinexBroker = new BitfinexBroker(SERVER_URL);
        mBitfinexBroker.connect();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
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

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);

                    mBitfinexBroker.disconnect();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).run();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBitfinexBroker.disconnect();
    }
}

package com.duvarapps.bitfinexwebsocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.duvarapps.bitfinexwebsocket.callback.channel.ChannelCallbackHandler;
import com.duvarapps.bitfinexwebsocket.callback.channel.TickHandler;
import com.duvarapps.bitfinexwebsocket.callback.command.CommandCallbackHandler;
import com.duvarapps.bitfinexwebsocket.callback.command.ConnectionHeartbeatCallback;
import com.duvarapps.bitfinexwebsocket.callback.command.DoNothingCommandCallback;
import com.duvarapps.bitfinexwebsocket.callback.command.ErrorCallback;
import com.duvarapps.bitfinexwebsocket.callback.command.SubscribedCallback;
import com.duvarapps.bitfinexwebsocket.callback.command.UnsubscribedCallback;
import com.duvarapps.bitfinexwebsocket.commands.AbstractAPICommand;
import com.duvarapps.bitfinexwebsocket.helper.ConnectionStatus;
import com.duvarapps.bitfinexwebsocket.helper.ServerListener;
import com.duvarapps.bitfinexwebsocket.manager.QuoteManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class BitfinexBroker
{
    private static final String TAG = BitfinexBroker.class.getName();

    private final String serverUrl;
    private final Handler statusHandler;
    private final Handler messageHandler;
    private final AtomicLong lastHeartbeat;
    private final QuoteManager quoteManager;
    private final Map<Integer, String> channelIdSymbolMap;

    private WebSocket webSocket;
    private OkHttpClient okHttpClient;
    private ServerListener serverListener;
    private Map<String, CommandCallbackHandler> commandCallbacks;
    private ConnectionStatus status = ConnectionStatus.DISCONNECTED;

    private Queue<String> messageQueue = new LinkedList<>();

    public BitfinexBroker(final String url)
    {
        okHttpClient = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS).pingInterval(10,
                TimeUnit.SECONDS) //if don't ping server kicks me out
                .retryOnConnectionFailure(true).build();

        this.serverUrl = url;
        this.statusHandler = new Handler(new StatusHandler());
        this.messageHandler = new Handler(new MessageHandler());
        this.quoteManager = new QuoteManager(this);
        this.lastHeartbeat = new AtomicLong();
        this.channelIdSymbolMap = new HashMap<>();

        setupCommandCallbacks();
    }

    public void connect()
    {
        Request request = new Request.Builder().url(serverUrl).build();
        webSocket = okHttpClient.newWebSocket(request, new SocketListener());
        serverListener = new ServerListener()
        {
            @Override
            public void onNewMessage(String message)
            {
                Log.d(TAG, message);
                handleMessage(message);
            }

            @Override
            public void onStatusChange(ConnectionStatus status)
            {
                String statusMsg = (status == ConnectionStatus.CONNECTED ? "connected" : "disconnceted");
                BitfinexBroker.this.status = status;
                Log.d(TAG, statusMsg);
            }
        };
    }

    public void disconnect()
    {
        webSocket.cancel();
        serverListener = null;
        messageHandler.removeCallbacksAndMessages(null);
        statusHandler.removeCallbacksAndMessages(null);
    }

    public String getFromChannelSymbolMap(int channelId)
    {
        synchronized (channelIdSymbolMap)
        {
            return channelIdSymbolMap.get(channelId);
        }
    }

    public void removeChannel(int channelId)
    {
        synchronized (channelIdSymbolMap)
        {
            channelIdSymbolMap.remove(channelId);
            channelIdSymbolMap.notifyAll();
        }
    }

    public QuoteManager getQuoteManager()
    {
        return quoteManager;
    }

    public int getChannelForSymbol(final String symbol)
    {
        synchronized (channelIdSymbolMap)
        {
            for (Map.Entry<Integer, String> entry : channelIdSymbolMap.entrySet())
            {
                if (entry.getValue().equals(symbol))
                {
                    return entry.getKey();
                }
            }
            return -1;
        }
    }

    public void updateConnectionHeartbeat()
    {
        lastHeartbeat.set(System.currentTimeMillis());
    }

    public void addToChannelSymbolMap(final int channelId, final String tickerSymbol)
    {
        synchronized (channelIdSymbolMap)
        {
            channelIdSymbolMap.put(channelId, tickerSymbol);
            channelIdSymbolMap.notifyAll();
        }
    }

    private void setupCommandCallbacks()
    {
        commandCallbacks = new HashMap<>();
        commandCallbacks.put("info", new DoNothingCommandCallback());
        commandCallbacks.put("subscribed", new SubscribedCallback());
        commandCallbacks.put("pong", new ConnectionHeartbeatCallback());
        commandCallbacks.put("unsubscribed", new UnsubscribedCallback());
        commandCallbacks.put("error", new ErrorCallback());
    }

    private void handleMessage(String message)
    {
        if (message.startsWith("{"))
        {
            handleCommandCallback(message);
        }
        else if (message.startsWith("["))
        {
            handleChannelCallback(message);
        }
        else
        {
            Log.d(TAG, "Got unknown callback: {} " + message);
        }
    }

    private void handleCommandCallback(String message)
    {
        try
        {
            final JSONTokener tokener = new JSONTokener(message);
            final JSONObject jsonObject = new JSONObject(tokener);

            final String eventType = jsonObject.getString("event");

            if (!commandCallbacks.containsKey(eventType))
            {
                Log.e(TAG, "Unknown event: " + message);
            }
            else
            {
                final CommandCallbackHandler callback = commandCallbacks.get(eventType);
                callback.handleChannelData(this, jsonObject);
            }
        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private void handleChannelCallback(final String message)
    {
        try
        {
            // Channel callback
            Log.d(TAG, "Channel callback");
            updateConnectionHeartbeat();

            // JSON callback
            final JSONTokener tokener = new JSONTokener(message);
            final JSONArray jsonArray = new JSONArray(tokener);

            handleChannelData(jsonArray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void handleChannelData(final JSONArray jsonArray) throws JSONException
    {
        final int channel = jsonArray.getInt(0);
        final String channelSymbol = getFromChannelSymbolMap(channel);

        if (channelSymbol == null)
        {
            Log.e(TAG, "Unable to determine symbol for channel: " + channel + " " + jsonArray);
            return;
        }

        if (jsonArray.get(1) instanceof String)
        {
            handleChannelDataString(jsonArray, channelSymbol);
        }
        else
        {
            handleChannelDataArray(jsonArray, channelSymbol);
        }
    }

    private void handleChannelDataString(final JSONArray jsonArray, final String channelSymbol) throws JSONException
    {
        final String value = jsonArray.getString(1);

        if ("hb".equals(value))
        {
            quoteManager.updateChannelHeartbeat(channelSymbol);
        }
        else
        {
            Log.e(TAG, "Unable to process: " + jsonArray.toString());
        }
    }

    private void handleChannelDataArray(final JSONArray jsonArray, final String channelSymbol) throws JSONException
    {
        final JSONArray subarray = jsonArray.getJSONArray(1);

        if (channelSymbol.startsWith("t") && channelSymbol.length() >= 7) // BitfinexTickerSymbol
        {
            final ChannelCallbackHandler handler = new TickHandler();
            handler.handleChannelData(this, channelSymbol, subarray);
        }
        else
        {
            Log.e(TAG, "Unknown or not supported type: " + channelSymbol);
        }
    }

    private void emptyMessageQueue()
    {
        Queue<String> copyQueue = new LinkedList<>(messageQueue);
        messageQueue.clear();
        for (String message : copyQueue)
        {
            sendMessage(message);
        }
    }

    public void sendCommand(final AbstractAPICommand apiCommand)
    {
        final String command = apiCommand.getCommand(this);
        Log.d(TAG, "Sending to server: " + command);
        sendMessage(command);
    }

    private void sendMessage(String message)
    {
        if (webSocket == null || status == ConnectionStatus.DISCONNECTED)
        {
            messageQueue.add(message);
        }
        else
        {
            webSocket.send(message);
        }
    }

    public void removeChannelForSymbol(final String symbol)
    {
        final int channel = getChannelForSymbol(symbol);

        if (channel != -1)
        {
            synchronized (channelIdSymbolMap)
            {
                channelIdSymbolMap.remove(channel);
            }
        }
    }

    private class SocketListener extends WebSocketListener
    {
        @Override
        public void onOpen(WebSocket webSocket, Response response)
        {
            status = ConnectionStatus.CONNECTED;
            Message m = statusHandler.obtainMessage(0, ConnectionStatus.CONNECTED);
            statusHandler.sendMessage(m);

            emptyMessageQueue();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text)
        {
            Message m = messageHandler.obtainMessage(0, text);
            messageHandler.sendMessage(m);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason)
        {
            status = ConnectionStatus.DISCONNECTED;
            Message m = statusHandler.obtainMessage(0, ConnectionStatus.DISCONNECTED);
            statusHandler.sendMessage(m);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response)
        {
            disconnect();
            Log.e(TAG, t.getMessage(), t);
        }
    }

    private class MessageHandler implements Handler.Callback
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            serverListener.onNewMessage((String) msg.obj);
            return true;
        }
    }

    private class StatusHandler implements Handler.Callback
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            serverListener.onStatusChange((ConnectionStatus) msg.obj);
            return true;
        }
    }
}
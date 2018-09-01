package com.duvarapps.bitfinexwebsocket.commands;

import com.duvarapps.bitfinexwebsocket.BitfinexBroker;

import org.json.JSONException;

public abstract class AbstractAPICommand
{
    public abstract String getCommand(final BitfinexBroker bitfinexBroker);
}

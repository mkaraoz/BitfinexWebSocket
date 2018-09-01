package com.duvarapps.bitfinexwebsocket.entity;

public class BitfinexTicker
{
    private String symbol;
    private double bid;
    private double bidSize;
    private double ask;
    private double askSize;
    private double dailyChange;
    private double dailyChangePerc;
    private double lastPrice;
    private double volume;
    private double high;
    private double low;

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public double getBid()
    {
        return bid;
    }

    public void setBid(double bid)
    {
        this.bid = bid;
    }

    public double getBidSize()
    {
        return bidSize;
    }

    public void setBidSize(double bidSize)
    {
        this.bidSize = bidSize;
    }

    public double getAsk()
    {
        return ask;
    }

    public void setAsk(double ask)
    {
        this.ask = ask;
    }

    public double getAskSize()
    {
        return askSize;
    }

    public void setAskSize(double askSize)
    {
        this.askSize = askSize;
    }

    public double getDailyChange()
    {
        return dailyChange;
    }

    public void setDailyChange(double dailyChange)
    {
        this.dailyChange = dailyChange;
    }

    public double getDailyChangePerc()
    {
        return dailyChangePerc;
    }

    public void setDailyChangePerc(double dailyChangePerc)
    {
        this.dailyChangePerc = dailyChangePerc;
    }

    public double getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice)
    {
        this.lastPrice = lastPrice;
    }

    public double getVolume()
    {
        return volume;
    }

    public void setVolume(double volume)
    {
        this.volume = volume;
    }

    public double getHigh()
    {
        return high;
    }

    public void setHigh(double high)
    {
        this.high = high;
    }

    public double getLow()
    {
        return low;
    }

    public void setLow(double low)
    {
        this.low = low;
    }
}

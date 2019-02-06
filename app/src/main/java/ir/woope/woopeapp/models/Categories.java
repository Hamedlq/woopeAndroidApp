package ir.woope.woopeapp.models;

import com.google.gson.annotations.SerializedName;

public enum Categories {
    @SerializedName("8")
    OnlineService("OnlineService", 8);

    private String stringValue;
    private int intValue;

    private Categories(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int value() {
        return intValue;
    }
}

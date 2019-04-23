package ir.woope.woopeapp.models;

import com.google.gson.annotations.SerializedName;

public enum ListTypes {
    @SerializedName("31")
    StoreList("StoreList", 31),
    @SerializedName("32")
    MallList("MallList", 32),
    @SerializedName("33")
    ProductList("ProductList", 33),
    @SerializedName("34")
    BannerList("BannerList", 34);

    private String stringValue;
    private int intValue;

    private ListTypes(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

}

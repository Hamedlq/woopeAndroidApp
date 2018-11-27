package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Store extends ApiResponse implements Serializable {

    public Store() {
    }

    public Store(String name,long point, int numOfSongs, int thumbnail) {
        this.storeName = name;
        this.returnPoint = point;
        this.thumbnail = thumbnail;
        isFollowed=false;
        isCashPayAllowed=true;
        isAdvertise=false;
    }

    public long storeId;
    public String storeName;
    public String storeDescription;
    public long basePrice;
    public long returnPoint;
    public String discountPercent;
    public String imageUId;
    public String logoSrc;
    public String coverSrc;
    public int thumbnail;
    public boolean isFollowed;
    public String firstPhone;
    public String secondPhone;
    public boolean isCashPayAllowed;
    public boolean isAdvertise;
    public String website;
    public String woopeThreshold;
}

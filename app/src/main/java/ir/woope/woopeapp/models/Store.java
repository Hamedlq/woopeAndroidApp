package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Store extends ApiResponse implements Serializable {
    public String storeId;
    public String storeName;
    public String storeDescription;
    public String basePrices;
    public String basePrice;
    public String returnPoint;
    public String discountPercent;
    public String imageUId;
    public String thumbnailUId;
}

package ir.woope.woopeapp.models;

import java.io.Serializable;

public class StoreGalleryItem extends ApiResponse implements Serializable {

    public long id;

    public String storeName;

    public long branchId;

    public Float price;

    public String name;

    public String description;

    public String postImageAddress;

    public int countLike;

    public boolean isLiked;

    public boolean canBeSold;

}

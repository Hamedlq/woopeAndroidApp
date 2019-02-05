package ir.woope.woopeapp.models;

import java.io.Serializable;

public class StoreGalleryItem extends ApiResponse implements Serializable {

    public long id;

    public long branchId;

    public Float price;

    public String name;

    public String description;

    public String productImageAddress;

    public int countLike;

    public boolean isLiked;

}

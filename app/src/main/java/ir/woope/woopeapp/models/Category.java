package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Category extends ApiResponse implements Serializable {

    public Category() {
    }

    public Category(String name, int thumbnail) {
        this.categoryName = name;
        this.thumbnail = thumbnail;
    }
    public long categoryId;
    public String categoryName;
    public int thumbnail;
}

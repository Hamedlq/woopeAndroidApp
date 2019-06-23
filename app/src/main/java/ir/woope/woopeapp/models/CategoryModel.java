package ir.woope.woopeapp.models;

import java.io.Serializable;

public class CategoryModel extends ApiResponse implements Serializable {

    public long id;
    public String name;
    public String image;

    public Boolean isSelected=false;

}

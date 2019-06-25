package ir.woope.woopeapp.models;

import java.io.Serializable;

public class SortType extends ApiResponse implements Serializable {

    public Short id;
    public String name;
    public String imgUrl;
    public Boolean isSelected = false;

}

package ir.woope.woopeapp.models;

import java.io.Serializable;

public class ZoneModel extends ApiResponse implements Serializable {

    public long id;
    public String name;
    public Boolean isChecked = false;

}

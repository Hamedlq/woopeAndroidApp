package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class MallModel extends ApiResponse implements Serializable {

    public MallModel() {
    }

    public int id;
    public String name;
    public String address;
    public String srcImage;
}

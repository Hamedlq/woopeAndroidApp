package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class MainListModel extends ApiResponse implements Serializable {

    public MainListModel() {
    }

    public ListTypes listType;
    public int numberPerList;
    public int listOrder;
    public String listTitle;
    public Boolean hasMoreBranches;
}

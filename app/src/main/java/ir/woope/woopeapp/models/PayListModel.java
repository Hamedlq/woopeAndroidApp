package ir.woope.woopeapp.models;


import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class PayListModel extends ApiResponse implements Serializable {

    public long id;
    public String storeName;
    public long storeId;
    public long pointPay;
    public long totalPrice;
    public long returnPoints;
    public int payType;
    public String confirmationCode;

    public PayListModel() {

    }

}

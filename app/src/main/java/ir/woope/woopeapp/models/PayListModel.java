package ir.woope.woopeapp.models;


import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class TransactionModel implements Serializable {

    public long Id;
    public String storeName;
    public long pointPay;
    public long totalPrice;
    public long returnPoints;
    public int payType;
    public String confirmCode;

    public TransactionModel() {

    }

}

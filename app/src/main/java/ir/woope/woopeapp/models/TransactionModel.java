package ir.woope.woopeapp.models;


import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class TransactionModel extends ApiResponse implements Serializable {

    public long payListId;
    public String storeName;
    public long totalPrice;
    public long toman;
    public long woope;
    public long pointPay;
    public int storeId;
    public int payType;
    public String returnWoop;
    public TransactionModel() {

    }

}

package ir.woope.woopeapp.models;


import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class TransactionModel implements Serializable {

    public long payListId;
    public String currencyType;
    public long amount;
    public String transType;
    public String payType;
    public String time;
}

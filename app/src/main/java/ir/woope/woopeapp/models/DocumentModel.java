package ir.woope.woopeapp.models;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Hamed on 6/10/2018.
 */

public class DocumentModel extends ApiResponse implements Serializable {

    public long id;
    public long payListId;
    public String storeName;
    public long totalPrice;
    public long toman;
    public long woope;
    public long pointPay;
    public int storeId;
    public int payType;
    public String logoSrc;
    public String returnWoop;
    public List<TransactionModel> transactionList;

    public DocumentModel() {

    }

}

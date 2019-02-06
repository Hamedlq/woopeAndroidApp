package ir.woope.woopeapp.models;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Hamed on 6/10/2018.
 */

public class PayListModel extends ApiResponse implements Serializable {

    public long id;
    public String storeName;
    public long branchId;
    public long pointPay;
    public long totalPrice;
    public long returnWoope;
    public int payType;
    public String confirmationCode;
    public String logoSrc;
    public boolean switchCredit;
    public long basePrice;
    public long returnPoint;
    public boolean switchWoope;
    public List<Long> categoryId;

    public PayListModel() {

    }

    public String getWoopePriceString() {
        long wprice = totalPrice - (pointPay * 1000);
        return String.valueOf(wprice);
    }

    public String pointPayString() {
        if (pointPay > 0) {
            return String.valueOf(pointPay);
        }
        return "0";
    }
}

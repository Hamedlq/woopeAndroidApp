package ir.woope.woopeapp.models;


import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class PayResponseModel extends ApiResponse implements Serializable {

    public String bankUrl;
    public String token;
    public String redirectURL;

    public PayResponseModel() {

    }
}

package ir.woope.woopeapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hamed on 6/10/2018.
 */

public class ApiResponse {

    @SerializedName("errors")
    @Expose
    private List<String> errors;

    @SerializedName("warnings")
    @Expose
    private String warnings;

    @SerializedName("info")
    @Expose
    private String info;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    public int status;

    public List<String> getErrors() {
        return errors;
    }

    public String getWarnings() {
        return warnings;
    }

    public String getInfo() {
        return info;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public int giftPrice;

}

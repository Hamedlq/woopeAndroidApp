package ir.woope.woopeapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Splash {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("mobile")
    @Expose
    private String mobile;

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
    private int status;

    public String getUsername() {
        return username;
    }

    public String getMobile() {
        return mobile;
    }

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

}

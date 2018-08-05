package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Profile extends ApiResponse implements Serializable{
    private String name;
    private String family;
    private String username;
    private String userBio;
    private String imageUid;
    private int woopeCredit =0;
    private int tomanCredit =0;
    private int transactionCount =0;

    public Profile() {
    }

    public String getName() {
        if(name!=null){
            return name;
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        if(name!=null){
            return family;
        }
        return "";
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public int getWoopeCredit() {
        return woopeCredit;
    }

    public void setWoopeCredit(int woopeCredit) {
        this.woopeCredit = woopeCredit;
    }

    public int getTomanCredit() {
        return tomanCredit;
    }

    public String getCreditString() {
        if(tomanCredit >0){
            return String.valueOf(tomanCredit);
        }
        return "0";
    }
    public String getWoopeCreditString() {
        if(woopeCredit >0){
            return String.valueOf(woopeCredit);
        }
        return "0";
    }
    public String getUseNumberString() {
        if(transactionCount >0){
            return String.valueOf(transactionCount);
        }
        return "0";
    }
    public void setTomanCredit(int tomanCredit) {
        this.tomanCredit = tomanCredit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}

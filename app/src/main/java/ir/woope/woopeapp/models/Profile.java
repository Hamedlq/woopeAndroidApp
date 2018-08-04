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
    private int woopeCredit=0;
    private int credit=0;
    private int useNumber=0;

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

    public int getCredit() {
        return credit;
    }

    public String getCreditString() {
        if(credit>0){
            return String.valueOf(credit);
        }
        return "0";
    }
    public String getWoopeCreditString() {
        if(woopeCredit>0){
            return String.valueOf(woopeCredit);
        }
        return "0";
    }
    public String getUseNumberString() {
        if(useNumber>0){
            return String.valueOf(useNumber);
        }
        return "0";
    }
    public void setCredit(int credit) {
        this.credit = credit;
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

    public int getUseNumber() {
        return useNumber;
    }

    public void setUseNumber(int useNumber) {
        this.useNumber = useNumber;
    }
}

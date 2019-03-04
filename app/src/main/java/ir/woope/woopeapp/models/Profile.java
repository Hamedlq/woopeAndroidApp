package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Profile extends ApiResponse implements Serializable {
    private String name;
    private String family;
    private String username;
    private String userBio;
    private String age;
    private String email;
    private int gender;
    private String imageSrc;
    private String mobile;
    private boolean phoneNumberConfirmed;
    private int woopeCredit = 0;
    private long moneyCredit;
    private int transactionCount = 0;

    private int birthYear;
    private int birthMonth;
    private int birthDay;

    public Profile() {
        name = "";
        family = "";
        username = "";
        userBio = "";
        imageSrc = "";
        moneyCredit = 0;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        if (name != null) {
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

    public long getTomanCredit() {
        return moneyCredit;
    }

    public String getCreditString() {
        if (moneyCredit != 0) {
            return String.valueOf(Math.round(moneyCredit));
        }
        return "0";
    }

    public String getWoopeCreditString() {
        if (woopeCredit > 0) {
            return String.valueOf(woopeCredit);
        }
        return "0";
    }

    public String getUseNumberString() {
        if (transactionCount > 0) {
            return String.valueOf(transactionCount);
        }
        return "0";
    }

    public void setTomanCredit(long moneyCredit) {
        this.moneyCredit = moneyCredit;
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

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public int getGender() {
        return gender;
    }

    public boolean getConfirmed() {
        return phoneNumberConfirmed;
    }

    public String getMobile() {
        return mobile;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public int getAgeYear() {
        return birthYear;
    }
    public int getAgeMonth() {
        return birthMonth;
    }
    public int getAgeDay() {
        return birthDay;
    }

    public void setBirthYear(int year) { this.birthYear = year; }
    public void setBirthMonth(int month) {
        this.birthMonth = month;
    }
    public void setBirthDay(int day) { this.birthDay = day; }
}

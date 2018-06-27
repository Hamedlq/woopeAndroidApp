package ir.woope.woopeapp.models;

import java.io.Serializable;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Profile implements Serializable{
    private String name;
    private String family;
    private int woopeCredit;
    private int credit;

    public Profile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
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

    public void setCredit(int credit) {
        this.credit = credit;
    }
}

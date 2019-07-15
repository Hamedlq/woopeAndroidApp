package ir.woope.woopeapp.models;

import java.io.Serializable;
import java.util.List;

public class SpecialOfferItem extends ApiResponse implements Serializable {

    public long id;

    public String storeName;

    public long branchId;

    public int price;

    public String name;

    public String description;

    public String postImageAddress;

    public List<String> listOfImageAddress;

    public int countLike;

    public boolean isLiked;

    public boolean canBeSold;

    public String persianRegisterDate;

    public String persianExpireDate;

    public String title;

    public String weekDayName;

    public short discountPercent;

}

package ir.woope.woopeapp.models;

public class PayState extends ApiResponse{
    String mode;
    String credit;

    public PayState(String n, String c) {
        mode = n;
        credit = c;
    }

    public String getMode() {
        return mode;
    }

    public String getCredit() {
        return credit;
    }
}

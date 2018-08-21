package ir.woope.woopeapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessToken {
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    private int userId;
    private int id;
    private String title;
    private String body;

    @SerializedName("status")
    @Expose
    public int status;

    public String getAccessToken() {
        return accessToken;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }public int getStatus() {
        return status;
    }

}

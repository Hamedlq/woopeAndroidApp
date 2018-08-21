package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.models.AccessToken;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {

    @POST("/connect/token")
    @FormUrlEncoded
    Call<AccessToken> send_info(@Field("username") String userphone, @Field("password") String password, @Field("grant_type") String grant_type);

}

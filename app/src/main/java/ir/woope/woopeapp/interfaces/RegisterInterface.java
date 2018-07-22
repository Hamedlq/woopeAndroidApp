package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

        @POST("/Account/RegisterApp")
        @FormUrlEncoded
        Call<ApiResponse> send_info(@Field("username") String username, @Field("mobile") String mobile, @Field("password") String password);

        @POST("/Account/RegisterApp")
        @FormUrlEncoded
        Call<ApiResponse> send_verif_code(@Field("Mobile") String mobile, @Field("VerifyCode") String code);
}

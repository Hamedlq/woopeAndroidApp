package ir.woope.woopeapp.interfaces;

import org.jetbrains.annotations.Nullable;

import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

    @POST("api/Account/RegisterApp")
    @FormUrlEncoded
    Call<ApiResponse> send_info(@Nullable @Field("username") String username, @Field("mobile") String mobile, @Field("password") String password);

    @POST("api/Account/ConfirmCode")
    @FormUrlEncoded
    Call<ApiResponse> send_verif_code(@Field("Mobile") String mobile, @Field("VerifyCode") String code);

    @POST("api/Account/SendForgetPassCode")
    @FormUrlEncoded
    Call<ApiResponse> send_code(@Field("Mobile") String mobile);

}

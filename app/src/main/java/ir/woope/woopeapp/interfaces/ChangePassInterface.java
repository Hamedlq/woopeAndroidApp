package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChangePassInterface {

    @POST("/Account/SendForgetPassCode")
    @FormUrlEncoded
    Call<ApiResponse> send_mobile(@Field("Mobile") String mobile);

    @POST("/Account/ConfirmCodeAndUpdate")
    @FormUrlEncoded
    Call<ApiResponse> change_pass(@Field("Mobile") String mobile, @Field("Password") String password, @Field("VerifyCode") String verifycode);

}

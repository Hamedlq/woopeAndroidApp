package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ForgetPassInterface {

    @POST("Account/SendForgetPassCode")
    @FormUrlEncoded
    Call<ApiResponse> send_mobile(@Field("Mobile") String mobile);

}

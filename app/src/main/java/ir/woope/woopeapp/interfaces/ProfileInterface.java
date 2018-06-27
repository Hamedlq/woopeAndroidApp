package ir.woope.woopeapp.interfaces;

import java.util.List;

import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.TransactionModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileInterface {
    @GET("api/Profile/Get")
    Call<ApiResponse<Profile>> getProfileFromServer(@Query("token") String authtoken);

    @FormUrlEncoded
    @POST("api/Transaction/InsertUserPayList")
    Call<ApiResponse> InsertTransaction(@Field("token") String authtoken,
                                        @Field("StoreId") String StoreId,
                                        @Field("TotalPrice") String Amount,
                                        @Field("PayType") int payType);
}

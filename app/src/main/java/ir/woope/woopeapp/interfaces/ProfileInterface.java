package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ProfileInterface {

    @POST("api/Profile/GetProfile")
    Call<Profile> getProfileFromServer(@Header("Authorization")String auth);

    @FormUrlEncoded
    @POST("api/Transaction/InsertUserPayList")
    Call<ApiResponse> InsertTransaction(@Field("token") String authtoken,
                                        @Field("StoreId") String StoreId,
                                        @Field("TotalPrice") String Amount,
                                        @Field("PayType") int payType);

    @FormUrlEncoded
    @POST("api/Profile/sendOneSignalToken")
    Call<ApiResponse> sendOneSignalToken(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                        @Field("OneSignalToken") String oneSignalToken);

    @Multipart
    @POST("api/Profile/SetProfileImage")
    Call<ApiResponse> updateImage(@Part MultipartBody.Part file,
                                  @Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);

}

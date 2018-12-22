package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface EditProfileInterface {

    @POST("api/Profile/SetProfile")
    @FormUrlEncoded
    Call<ApiResponse> send_edit(@Header("Authorization") String auth, @Field("Name") String name, @Field("Family") String family, @Field("UserBio") String userbio, @Field("Email") String email, @Field("Gender") String gender, @Field("Age") String age);

    @POST("api/Profile/GetProfile")
    Call<Profile> getProfileFromServer(@Header("Authorization") String auth);
}

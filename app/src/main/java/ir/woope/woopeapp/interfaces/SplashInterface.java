package ir.woope.woopeapp.interfaces;

import ir.woope.woopeapp.models.Splash;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SplashInterface {
    @POST("api/Profile/GetProfile")
    Call<Splash> check_connection(@Header("Authorization")String auth);
}
package ir.woope.woopeapp.interfaces;

import java.util.List;

import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Store;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StoreInterface {
    @GET("api/Store/GetStores")
    Call<List<Store>> getStoreFromServer(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);

    @GET("api/Store/FindStore")
    Call<List<Store>> FindStore(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                @Query("query") String query);

    @POST(Constants.Actions.FOLLOW_STORE)
    @FormUrlEncoded
    Call<ApiResponse> followStore(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                            @Field("branchId") long s);

    @GET(Constants.Actions.GET_FOLLOW_STORE)
    Call<List<Store>> getFollowStore(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);

    @POST(Constants.Actions.GET_STORE)
    @FormUrlEncoded
    Call<Store> getStore(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                  @Field("branchId") long s);

}

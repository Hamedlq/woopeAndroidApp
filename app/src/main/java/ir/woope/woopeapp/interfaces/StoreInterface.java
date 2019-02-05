package ir.woope.woopeapp.interfaces;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.StoreGalleryItem;
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

    @POST("api/Store/FindStorebyPage")
    @FormUrlEncoded
    Call<List<Store>> FindStoreByPage(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Query("query") String query,
                                      @Field("pageNumber") int pageNumber);

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

    @POST("api/Store/GetStoresbyPage")
    @FormUrlEncoded
    Call<List<Store>> getStoresbyPage(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Field("pageNumber") int pageNumber);

    @GET("api/Product/GetActiveProduct")
    Call<List<StoreGalleryItem>> getActiveBranchProduct(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                                        @Query("ProductId") Long productId,
                                                        @Query("branchId") long branchId,
                                                        @Query("page") int page,
                                                        @Query("count") int count);

    @GET("api/Product/GetActiveProduct")
    Call<List<StoreGalleryItem>> getActiveBranchSingleProduct(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                                              @Query("ProductId") long productId,
                                                              @Query("branchId") long branchId,
                                                              @Query("page") int page,
                                                              @Query("count") int count);

    @POST("api/Product/ChangeLikeImage")
    @FormUrlEncoded
    Call<ApiResponse> LikeImage(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                @Field("ImageID") long imageId);

    @POST("api/Branch/NonCooperation")
    @FormUrlEncoded
    Call<ApiResponse> notCooperating(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                     @Field("BranchId") long branchId);

//    api/Branch/NonCooperation(long BranchId)

}

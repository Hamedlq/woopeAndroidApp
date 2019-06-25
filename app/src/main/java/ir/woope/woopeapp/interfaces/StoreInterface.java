package ir.woope.woopeapp.interfaces;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.MallModel;
import ir.woope.woopeapp.models.SortType;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.models.ZoneModel;
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
                                      @Field("pageNumber") int pageNumber,
                                      @Nullable @Field("Zones") List<Long> zones,
                                      @Nullable @Field("CategoryId") Long categoryId,
                                      @Nullable @Field("SortType") Short sortType );

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

    @POST("api/Store/GetStoresFilter")
    @FormUrlEncoded
    Call<List<Store>> GetStoresFilter(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Field("pageNumber") int pageNumber,
                                      @Field("listOrder") int listOrder,
                                      @Field("countOfList") int countOfList,
                                      @Field("MallId") Integer mallId);

    @POST("api/Store/GetMallList")
    @FormUrlEncoded
    Call<List<MallModel>> GetMallList(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Field("pageNumber") int pageNumber,
                                      @Field("countOfList") int countOfList);


    @GET("api/Branch/GetMainLists")
    Call<List<MainListModel>> getMainLists(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);

    @GET("api/Post/GetActivePost")
    Call<List<StoreGalleryItem>> getActiveBranchProduct(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                                        @Query("PostId") Long productId,
                                                        @Query("branchId") long branchId,
                                                        @Query("page") int page,
                                                        @Query("count") int count);

    @GET("api/Post/GetActivePost")
    Call<List<StoreGalleryItem>> getActiveBranchSingleProduct(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                                              @Query("PostId") long productId,
                                                              @Query("branchId") long branchId,
                                                              @Query("page") int page,
                                                              @Query("count") int count);

    @POST("api/Post/ChangeLikeImage")
    @FormUrlEncoded
    Call<StoreGalleryItem> LikeImage(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                     @Field("ImageID") long imageId);

    @GET("api/Branch/NonCooperation")
    Call<ApiResponse> notCooperating(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                     @Query("BranchId") long branchId);

    @GET("api/Post/GetAllActivePosts")
    Call<List<StoreGalleryItem>> getAllActiveProducts(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                                      @Query("page") int page,
                                                      @Query("count") int count);

    @GET("api/Post/SaveOnlineRequest")
    Call<ApiResponse> sendOnlineRequest(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                        @Query("productId") long productId);

    @GET("api/Branch/SaveVIPRequest")
    Call<ApiResponse> sendOnlineRequestStore(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                             @Query("branchId") long branchId);

//    @GET("api/store/GetBannerList")
//    Call<List<Store>> getBanners(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
//                                @Query("countOfList") int count);

    @POST("api/store/GetBannerList")
    @FormUrlEncoded
    Call<List<Store>> getBanners(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                 @Field("countOfList") int count);

    @POST("api/store/ShareStore")
    @FormUrlEncoded
    Call<ApiResponse> shareStore(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                 @Field("branchId") long branchId);

    @POST("api/Transaction/CheckDiscountCode")
    @FormUrlEncoded
    Call<ApiResponse> checkDiscountCode(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                        @Field("discountCode") String code,
                                        @Field("branchId") long branchId,
                                        @Field("Amount") long amount);

    @POST("api/Branch/GetCategories")
    Call<List<CategoryModel>> getCategories(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);

    @POST("api/Location/GetAllActiveZones")
    @FormUrlEncoded
    Call<List<ZoneModel>> getAllActiveZones(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                            @Field("cityId") long cityId);

    @POST("api/Store/SortingItems")
    Call<List<SortType>> getSortItems(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);
}

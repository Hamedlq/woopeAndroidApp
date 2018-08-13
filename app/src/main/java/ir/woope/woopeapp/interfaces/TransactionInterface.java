package ir.woope.woopeapp.interfaces;

import java.util.List;

import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.TransactionModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TransactionInterface {

    @POST("api/Transaction/GetUserAllActivePaylists")
    Call<List<PayListModel>> getTransactionsFromServer(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);

    @FormUrlEncoded
    @POST("api/Transaction/InsertUserPayList")
    Call<PayListModel> InsertTransaction(@Field("token") String authtoken,
                                                      @Field("StoreId") String StoreId,
                                                      @Field("TotalPrice") String Amount,
                                                      @Field("PayType") int payType);

    @FormUrlEncoded
    @POST("api/Transaction/GetConfirmCode")
    Call<PayListModel> GetConfirmCode(@Field("token") String authtoken,
                                                   @Field("Id") long paylistId,
                                                   @Field("Woope") String pointPay);

    @FormUrlEncoded
    @POST("api/Transaction/SendConfirmCode")
    Call<ApiResponse> SendConfirmCode(@Field("token") String authtoken,
                                      @Field("id") long payListId,
                                      @Field("code") String confirmationCode);


    @GET("api/Transaction/GetUserTransactions")
    Call<List<TransactionModel>> getUserTransactionsFromServer(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);
}

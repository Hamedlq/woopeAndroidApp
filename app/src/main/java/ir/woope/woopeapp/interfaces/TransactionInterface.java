package ir.woope.woopeapp.interfaces;

import java.util.List;

import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.DocumentModel;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.PayResponseModel;
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
    Call<PayListModel> InsertTransaction(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                         @Field("Id") long payListId,
                                         @Field("BranchId") long StoreId,
                                         @Field("TotalPrice") String Amount,
                                         @Field("PayType") int payType,
                                         @Field("SwitchCredit") boolean switch_credit,
                                         @Field("SwitchWoope") boolean switch_woope,
                                         @Field("DiscountCode") String discountCode
    );

    @FormUrlEncoded
    @POST("api/Transaction/GetConfirmCodeNew")
    Call<PayListModel> GetConfirmCode(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Field("Id") long paylistId);

    @FormUrlEncoded
    @POST("api/Pay/GetPayInfo")
    Call<PayResponseModel> GetPayInfo(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Field("paylistId") long paylistId);

    @FormUrlEncoded
    @POST("api/Transaction/SubmitCashConfirmationCode")
    Call<ApiResponse> SendConfirmCode(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken,
                                      @Field("paylistId") long payListId,
                                      @Field("confirmationCode") String confirmationCode);


    @GET("api/Transaction/GetUserTransactions")
    Call<List<DocumentModel>> getUserTransactionsFromServer(@Header(Constants.Actions.PARAM_AUTHORIZATION) String authToken);
}

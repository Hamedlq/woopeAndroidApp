package ir.woope.woopeapp.interfaces;

import java.util.List;

import ir.woope.woopeapp.models.TransactionModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TransactionInterface {
    @FormUrlEncoded
    @POST("item/supplierItems")
    Call<List<TransactionModel>> getTransactionsFromServer(@Field("token") String authtoken);

}

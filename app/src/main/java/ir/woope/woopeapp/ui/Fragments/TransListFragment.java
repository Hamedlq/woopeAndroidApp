package ir.woope.woopeapp.ui.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.TransactionListAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.CashPayActivity;
import ir.woope.woopeapp.ui.Activities.CreditPayActivity;
import ir.woope.woopeapp.ui.Activities.TransactionActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

/**
 * Created by alireza on 3/26/18.
 */

public class TransListFragment extends Fragment {
    private RecyclerView ordersList;
    private List<PayListModel> orderModelList;
    /*private List<ItemModel> userOrderModelList;*/
    private TransactionListAdapter adapter;
    private String authToken;
    private PayTransactionTouchListener payTransactionTouchListener;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_list, null, false);


        //((TransactionActivity) getActivity()).setToolbarTitle(getString(R.string.transaction_list));
        progressBar = view.findViewById(R.id.progressBar);
        ordersList = view.findViewById(R.id.orders_list);
        orderModelList = new ArrayList<>();
        adapter = new TransactionListAdapter(orderModelList, payTransactionTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        ordersList.setLayoutManager(mLayoutManager);

        ordersList.setAdapter(adapter);
        getOrderListFromServer();

        payTransactionTouchListener = new PayTransactionTouchListener() {
            @Override
            public void onBtnClick(View view, int position) {
                PayListModel model = (PayListModel) orderModelList.get(position);
                if(model.payType==1){
                    //go to cash pay
                    gotoPayCash(model);
                }else {
                    //go to credit pay
                    gotoCreditCash(model);
                }

                /*Intent myIntent = new Intent(getActivity(), PayActivity.class);
                myIntent.putExtra(STORE_NAME, model.storeName); //Optional parameters
                myIntent.putExtra(TOTAL_PRICE, model.totalPrice); //Optional parameters
                getActivity().startActivity(myIntent);*/

            }
        };
        return view;
    }


    private void getOrderListFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        TransactionInterface providerApiInterface =
                retrofit.create(TransactionInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<List<PayListModel>> call =
                providerApiInterface.getTransactionsFromServer("bearer " + authToken);


        call.enqueue(new Callback<List<PayListModel>>() {
            @Override
            public void onResponse(Call<List<PayListModel>> call, Response<List<PayListModel>> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    orderModelList = response.body();
                    adapter.notifyDataSetChanged();

                    adapter = new TransactionListAdapter(orderModelList, payTransactionTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                    ordersList.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<PayListModel>> call, Throwable t) {
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });

    }


    private void gotoOrderListActivity() {
        ///((NavigationActivity) getActivity()).setItem(R.id.orders_list);
        /*Intent intent = new Intent(getActivity(), OrdersListActivity.class);
        getActivity().finish();
        startActivity(intent);*/

    }

    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

    public interface PayTransactionTouchListener {
        public void onBtnClick(View view, int position);
    }

    public void gotoPayCash(PayListModel model){

        Profile profile=((TransactionActivity)getActivity()).getProfile();
        Intent myIntent = new Intent(getActivity(), CashPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        this.startActivity(myIntent);
    }

    public void gotoCreditCash(PayListModel model){
        Profile profile=((TransactionActivity)getActivity()).getProfile();
        Intent myIntent = new Intent(getActivity(), CreditPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        this.startActivity(myIntent);
    }
}

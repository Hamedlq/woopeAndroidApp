package ir.woope.woopeapp.ui.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.TransactionListAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.ListPaddingDecoration;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;

public class TransactionActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    Profile profile;
    private RecyclerView recyclerView;
    private List<PayListModel> orderModelList;
    private PayTransactionTouchListener payTransactionTouchListener;

    /*private List<ItemModel> userOrderModelList;*/
    private TransactionListAdapter adapter;
    private String authToken;

    //Store store;
    //String LIST_FRAGMENT = "ListFragment";
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            //store = (Store) getIntent().getExtras().getSerializable(STORE);
        }

        progressBar = findViewById(R.id.transaction_progressBar);


        FragmentManager fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //toolbar.setTitle(R.string.app_name);
        /*fragmentManager.beginTransaction()
                .add(R.id.frame_layout, new TransListFragment(), LIST_FRAGMENT)
                .commit();*/
        recyclerView = findViewById(R.id.recycler_view);
        orderModelList = new ArrayList<>();
        adapter = new TransactionListAdapter(this, orderModelList, payTransactionTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new ListPaddingDecoration());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);
        getPayListFromServer();

        payTransactionTouchListener = new PayTransactionTouchListener() {
            @Override
            public void onBtnClick(View view, int position) {
                PayListModel model = (PayListModel) orderModelList.get(position);
                if (model.payType == 1) {
                    //go to cash pay
                    gotoPayCash(model);
                } else {
                    //go to credit pay
                    gotoCreditCash(model);
                }
            }
        };


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RELOAD_LIST) {
                getPayListFromServer();
            }
        }
    }

    public Profile getProfile() {
        return profile;
    }


    public void getPayListFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        TransactionInterface providerApiInterface =
                retrofit.create(TransactionInterface.class);

        SharedPreferences prefs =
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
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

                    adapter = new TransactionListAdapter(TransactionActivity.this, orderModelList, payTransactionTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<PayListModel>> call, Throwable t) {
                Toast.makeText(TransactionActivity.this, R.string.error, Toast.LENGTH_LONG).show();
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

    public void gotoPayCash(PayListModel model) {

        Profile profile = getProfile();
        Intent myIntent = new Intent(this, CashPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        startActivityForResult(myIntent, RELOAD_LIST);
    }

    public void gotoCreditCash(PayListModel model) {
        Profile profile = getProfile();
        Intent myIntent = new Intent(this, PayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        startActivityForResult(myIntent, RELOAD_LIST);
    }

}

package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;

public class StoreActivity extends AppCompatActivity {

    @BindView(R.id.logo)
    protected ImageView logo;
    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.store_name)
    protected TextView store_name;
    @BindView(R.id.store_desc)
    protected TextView store_desc;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R.id.store_point)
    protected TextView store_point;
    @BindView(R.id.store_discount)
    protected TextView store_discount;
    @BindView(R.id.store_phones)
    protected TextView store_phones;


    String STORE_FRAGMENT = "StoreFragment";
    String authToken = null;
    Profile profile = null;
    Store store;
    String profileString;
    //ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            store = (Store) getIntent().getExtras().getSerializable(STORE);
        }

        getStore(store.storeId);


        Button payBtn = (Button) findViewById(R.id.payBtn);
        payBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    goToPaying();
                }
                return false;
            }
        });


        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        store_phones.setVisibility(View.GONE);

    }

    private void getStore(long storeId) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<Store> call =
                providerApiInterface.getStore("bearer " + authToken, storeId);


        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    store = response.body();
                    Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).into(logo);
                    Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.coverSrc).into(backdrop);
                    store_name.setText(store.storeName);
                    store_desc.setText(store.storeDescription);
                    String phones="";
                    if (!TextUtils.isEmpty(store.firstPhone)){
                        store_phones.setVisibility(View.VISIBLE);
                        phones = store.firstPhone;
                    }
                    if (!TextUtils.isEmpty(store.secondPhone)){
                        store_phones.setVisibility(View.VISIBLE);
                        phones += " - "+store.secondPhone;
                    }
                    store_phones.setText(phones);
                    if (!TextUtils.isEmpty(store.discountPercent)){
                        store_discount.setVisibility(View.VISIBLE);
                        store_discount.setText(store.discountPercent + " درصد تخفیف ");
                    }
                    if (!TextUtils.isEmpty(store.basePrice)){
                        store_point.setVisibility(View.VISIBLE);
                        store_point.setText("به ازای هر "+store.basePrice+" تومان خرید "+store.returnPoint+" ووپ دریافت می‌کنید" );
                    }
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });


    }

    public void goToPaying() {
        /*final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PROFILE, "");

        Profile obj = gson.fromJson(json, Profile.class);*/
        Intent myIntent = new Intent(this, PayActivity.class);
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(STORE, store);
        PayListModel model=new PayListModel();
        model.storeName=store.storeName;
        model.branchId=store.storeId;
        model.totalPrice=0;
        model.logoSrc=store.logoSrc;
        model.switchWoope=false;
        model.switchCredit=false;
        myIntent.putExtra(PAY_LIST_ITEM, model);
        this.startActivity(myIntent);
        this.finish();
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

}

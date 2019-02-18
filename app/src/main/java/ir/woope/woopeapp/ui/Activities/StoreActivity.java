package ir.woope.woopeapp.ui.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.adapters.StoreViewPagerAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class StoreActivity extends AppCompatActivity {

    @BindView(R.id.logo)
    protected ImageView logo;
    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.store_name)
    protected TextView store_name;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R.id.store_point)
    protected TextView store_point;
    @BindView(R.id.store_discount)
    protected TextView store_discount;
    @BindView(R.id.payBtn)
    protected CardView payBtn;
    ImageView close_btn;
    TextView confirm_pay;
    String STORE_FRAGMENT = "StoreFragment";
    String authToken = null;
    Profile profile = null;
    Store store;
    long totalPrice = 0;
    //ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        ButterKnife.bind(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            store = (Store) getIntent().getExtras().getSerializable(STORE);
            Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(logo);
        }

        getStore(store.storeId);

        //Button payBtn = (Button) findViewById(R.id.payBtn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayDialog();
            }
        });

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = findViewById(R.id.store_viewpager);
        viewPager.getParent().requestDisallowInterceptTouchEvent(true);
        viewPager.requestDisallowInterceptTouchEvent(true);

        /*viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        // Create an adapter that knows which fragment should be shown on each page
        StoreViewPagerAdapter adapter = new StoreViewPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.storeTabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void showPayDialog() {
        final Dialog dialog = new Dialog(StoreActivity.this);
        dialog.setContentView(R.layout.pay_dialog);
        dialog.setTitle("");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        close_btn = (ImageView) dialog.findViewById(R.id.close_btn);
        confirm_pay = (TextView) dialog.findViewById(R.id.confirm_pay);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Do your code here
            }
        });

        confirm_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPaying();
            }
        });

        final EditText amount = (EditText) dialog.findViewById(R.id.amount);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amount.removeTextChangedListener(this);

                try {

                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    totalPrice = longval;
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                amount.addTextChangedListener(this);
            }
        });
        dialog.show();

    }


    /* DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             switch (which) {
                 case DialogInterface.BUTTON_POSITIVE:
                     break;
                 case DialogInterface.BUTTON_NEGATIVE:
                     break;
             }
         }
     };*/
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
                    Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(logo);
                    Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.coverSrc).into(backdrop);
                    store_name.setText(store.storeName);
//                    if (!TextUtils.isEmpty(store.storeDescription)) {
//                        desc_layout.setVisibility(View.VISIBLE);
//                        store_desc.setText(store.storeDescription);
//                    }
                    String phones = "";
//                    if (!TextUtils.isEmpty(store.firstPhone)) {
//                        store_phones_layout.setVisibility(View.VISIBLE);
//                        phones = store.firstPhone;
//                    }
//                    if (!TextUtils.isEmpty(store.secondPhone)) {
//                        store_phones_layout.setVisibility(View.VISIBLE);
//                        phones += " - " + store.secondPhone;
//                    }
//                    store_phones.setText(phones);
                    if (!TextUtils.isEmpty(store.discountPercent)) {
                        store_discount.setVisibility(View.VISIBLE);
                        store_discount.setText(store.discountPercent + " درصد تخفیف ");
                    }
//                    if (!TextUtils.isEmpty(store.address)) {
//                        store_address_layout.setVisibility(View.VISIBLE);
//                        store_address.setText(store.address);
//                    }

                    if (store.basePrice != 0) {
//                        point_layout.setVisibility(View.VISIBLE);
                        store_point.setText(store.returnPoint + " عدد ووپ ");
//                        point_desc.setText("به ازای هر " + store.basePrice + " تومان خرید " + store.returnPoint + " ووپ دریافت می‌کنید");
                    }
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_splash), R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    public void goToPaying() {

        Intent myIntent = new Intent(this, PayActivity.class);
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(STORE, store);
        PayListModel model = new PayListModel();
        model.storeName = store.storeName;
        model.branchId = store.storeId;
        model.totalPrice = totalPrice;
        model.logoSrc = store.logoSrc;
        model.categoryId = store.categoryId;
        model.switchWoope = false;
        model.switchCredit = false;
        model.woopeThreshold=store.woopeThreshold;
        model.basePrice = store.basePrice;
        model.returnPoint = store.returnPoint;
        myIntent.putExtra(PAY_LIST_ITEM, model);
        this.startActivity(myIntent);
        this.finish();
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
        payBtn.setEnabled(false);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
        payBtn.setEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public long getBranchId() {
        return store.storeId;
    }

}

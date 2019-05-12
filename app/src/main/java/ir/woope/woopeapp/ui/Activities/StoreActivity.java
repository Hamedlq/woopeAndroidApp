package ir.woope.woopeapp.ui.Activities;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.Nullable;

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
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

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
//    @BindView(R.id.store_discount)
//    protected TextView store_discount;
    @BindView(R.id.payBtn)
    protected CardView payBtn;
    @BindView(R.id.bookmark_storeActivity)
    SparkButton bookmark;
    ImageView close_btn;
    TextView confirm_pay;
    AVLoadingIndicatorView dialogProgressBar;
    EditText discountCode;
    TextView invalidDiscountCode;
    String STORE_FRAGMENT = "StoreFragment";
    String authToken = null;
    Profile profile = null;
    Store store;
    long totalPrice = 0;

    @BindView(R.id.share_store_image)
    ImageView share;

    @BindView(R.id.progressBar_shareStore)
    AVLoadingIndicatorView shareLoading;
    //ProgressBar progressBar;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        context = this;

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

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (store.describeCountDiscountCode == null)
                    new GuideView.Builder(context)
                            .setTitle(" ")
                            .setContentText("برای این فروشگاه طرح اشتراک گذاری فعال نگردیده است")
                            .setGravity(Gravity.auto) //optional
                            .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                            .setTargetView(share)
                            .setContentTextSize(12)//optional
                            .setTitleTextSize(14)//optional
                            .build()
                            .show();
                else
                    shareStore(store.storeId);

            }
        });

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_apps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);

        toolbar.setTitle("woope");

//        getSupportActionBar().setIcon(R.drawable.ic_apps);
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

        if (store.isFollowed)
            bookmark.setChecked(true);
        else if (!store.isFollowed)
            bookmark.setChecked(false);

        bookmark.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                followStore(store);
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

    }

    private void followStore(Store s) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        final SharedPreferences settings = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = settings.getString(TOKEN, null);

        Call<ApiResponse> call =
                providerApiInterface.followStore("bearer " + authToken, s.storeId);


        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int code = response.code();
                if (code == 200) {
                    ApiResponse ar = response.body();
                    Utility.showSnackbar(findViewById(R.id.activity_store_layout), ar.getMessage(), Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    private void showPayDialog() {
        final Dialog dialog = new Dialog(StoreActivity.this);
        dialog.setContentView(R.layout.pay_dialog);
        dialog.setTitle("");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        close_btn = (ImageView) dialog.findViewById(R.id.close_btn);
        confirm_pay = (TextView) dialog.findViewById(R.id.confirm_pay);
        dialogProgressBar = dialog.findViewById(R.id.progressBar_payDialog);
        discountCode = dialog.findViewById(R.id.discountCode);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final EditText amount = (EditText) dialog.findViewById(R.id.amount);
        final TextView invalidPrice = (TextView) dialog.findViewById(R.id.pay_dialog_priceError);
        invalidDiscountCode = dialog.findViewById(R.id.pay_dialog_invalidDiscountCode);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalPrice > 0 && totalPrice<=10000000) {
                    if (discountCode.getText().toString().matches("")) {
                        goToPaying(null, null);
                    } else if (!discountCode.getText().toString().matches("")) {
                        checkCode(discountCode.getText().toString(), store.storeId);
                    }
                } else if(totalPrice>10000000){
                    invalidPrice.setText(R.string.overflowed_price);
                    invalidPrice.setVisibility(View.VISIBLE);
                }

                else{
                    invalidPrice.setText(R.string.invalid_price);
                    invalidPrice.setVisibility(View.VISIBLE);
                }
            }
        });

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
                    longval = Long.parseLong(Utility.arabicToDecimal(originalString));
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
//                    if (!TextUtils.isEmpty(store.discountPercent)) {
//                        store_discount.setVisibility(View.VISIBLE);
//                        //store_discount.setText(store.discountPercent + " درصد تخفیف ");
//                        store_discount.setText("");
//                    }
//                    if (!TextUtils.isEmpty(store.address)) {
//                        store_address_layout.setVisibility(View.VISIBLE);
//                        store_address.setText(store.address);
//                    }

//                    if (store.basePrice != 0) {
////                        point_layout.setVisibility(View.VISIBLE);
//                        store_point.setText(store.returnPoint + " عدد ووپ ");
////                        point_desc.setText("به ازای هر " + store.basePrice + " تومان خرید " + store.returnPoint + " ووپ دریافت می‌کنید");
//                    }

                    if (store.returnPoint == 0)
                        store_point.setVisibility(View.INVISIBLE);
                    else if (store.returnPoint != 0) {
                        store_point.setVisibility(View.VISIBLE);
                        store_point.setText(store.returnPoint + " عدد ووپ");
                    }

                    if(store.describeCountDiscountCode==null)
                        share.setBackground(ContextCompat.getDrawable(context,R.drawable.share_gray));
                    else if(store.describeCountDiscountCode!=null)
                        share.setBackground(ContextCompat.getDrawable(context,R.drawable.share_purple));
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    private void shareStore(long branchId) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<ApiResponse> call =
                providerApiInterface.shareStore("bearer " + authToken, branchId);

        showShareProgreeBar();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideShareProgreeBar();
                int code = response.body().status;
                if (code == 101)
                    shareText(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideShareProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });
    }

    private void shareText(String text) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");// Plain format text

        // You can add subject also
        /*
         * sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT,
         * "Subject Here");
         */
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share Text Using"));
    }

    private void checkCode(final String Code, long branchId) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<ApiResponse> call =
                providerApiInterface.checkDiscountCode("bearer " + authToken, Code, branchId);

        showDialogProgreeBar();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideDialogProgreeBar();
                int code = response.body().status;
                if (code == 101)
                    goToPaying(response.body().getMessage(), Code);
                else if (code == 202)
                    invalidDiscountCode.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideDialogProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });
    }

    public void goToPaying(@Nullable String extraWoope, @Nullable String discountCode) {

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
        model.woopeThreshold = store.woopeThreshold;
        model.basePrice = store.basePrice;
        model.returnPoint = store.returnPoint;
        if (extraWoope == null)
            model.extraDiscountWoope = "0";
        else if (extraWoope.equals(""))
            model.extraDiscountWoope = "0";
        else
            model.extraDiscountWoope = extraWoope;
        model.discountCode = discountCode;
        myIntent.putExtra(PAY_LIST_ITEM, model);
        myIntent.putExtra("LogoUrl",store.logoSrc);
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

    public void showShareProgreeBar() {
        shareLoading.smoothToShow();
        share.setVisibility(View.GONE);
    }

    public void hideShareProgreeBar() {
        shareLoading.hide();
        share.setVisibility(View.VISIBLE);
    }

    public void showDialogProgreeBar() {
        dialogProgressBar.smoothToShow();
        confirm_pay.setText("");
    }

    public void hideDialogProgreeBar() {
        dialogProgressBar.hide();
        confirm_pay.setText(R.string.accept);
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

    @Override
    public void onBackPressed() {

        finish();

        super.onBackPressed();
    }
}

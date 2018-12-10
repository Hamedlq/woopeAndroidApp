package ir.woope.woopeapp.ui.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.PayResponseModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Store;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;

public class PayActivity extends AppCompatActivity implements View.OnTouchListener {
    //@BindView(R.id.StoreName) TextView StoreName_tv;
    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.switch_credit)
    protected Switch switch_credit;
    @BindView(R.id.switch_woope)
    protected Switch switch_woope;
   /* @BindView(R.id.amount)
    protected EditText amount;*/
    @BindView(R.id.store_name)
    protected TextView StoreName_tv;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R.id.total_price)
    protected TextView total_price;
    @BindView(R.id.tr0)
    protected RelativeLayout tr0;
    @BindView(R.id.pay_price)
    protected TextView pay_price;
    @BindView(R.id.tr1)
    protected RelativeLayout tr1;
    @BindView(R.id.toman_use)
    protected TextView toman_use;
    @BindView(R.id.tr2)
    protected RelativeLayout tr2;
    @BindView(R.id.woope_use)
    protected TextView woope_use;
    @BindView(R.id.tr3)
    protected RelativeLayout tr3;
    @BindView(R.id.return_woope)
    protected TextView return_woope;
    @BindView(R.id.tr4)
    protected RelativeLayout tr4;
    @BindView(R.id.remain_toman)
    protected TextView remain_toman;
    @BindView(R.id.tr5)
    protected RelativeLayout tr5;
    @BindView(R.id.tax)
    protected TextView tax;
    @BindView(R.id.cash_card)
    protected CardView cash_card;
    @BindView(R.id.online_card)
    protected CardView online_card;
    @BindView(R.id.woope_layout)
    protected CardView woope_layout;
    @BindView(R.id.credit_layout)
    protected CardView credit_layout;
    @BindView(R.id.pay_radio_cash)
    protected ImageView pay_radio_cash;
    @BindView(R.id.pay_radio_credit)
    protected ImageView pay_radio_credit;
    @BindView(R.id.drawer_icon)
    protected ImageView drawer_icon;
    @BindView(R.id.drawer_pull)
    protected RelativeLayout drawer_pull;

    @BindView(R.id.detail_layout)
    protected LinearLayout detail_layout;



    @BindView(R.id.payBtn)
    protected Button btn;
    //amount = findViewById(R.id.amount);
    Store store;
    //long totalPrice;
    //Spinner spinner;
    //EditText amount;
    TextView woope_credit;
    TextView toman_credit;
    String authToken;
    Profile profile;
    String profileJson;
    //ProgressBar progressBar;


    String profileString;
    boolean automaticPayFlag=false;
    boolean isOnline=true;
    boolean isDetailShow=false;
    //boolean saveTransactionFlag=true;
    PayListModel savedPayListModel;
    long totalPrice=0;
    long payPriceValue=0;


    View helpButton;

    Toolbar toolbar;

//    @Override
//    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
////        helpButton=findViewById(R.id.action_support);
//
//        showhint();
//
//        return super.onCreateView(parent, name, context, attrs);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.pay_toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ButterKnife.bind(this);
        //amount.addTextChangedListener(onTextChangedListener());

        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            savedPayListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
            if(savedPayListModel==null){
                //fetch from shared preferences because of going to bank page and return
                profile=getUserProfile();
                savedPayListModel=getSavedPayList();
                ConfirmPayment(savedPayListModel);
            }
            totalPrice=savedPayListModel.totalPrice;
            getStore(savedPayListModel.branchId);
            //amount.setText(String.valueOf(savedPayListModel.totalPrice));
            StoreName_tv.setText(savedPayListModel.storeName);
            Picasso.with(PayActivity.this).load(Constants.GlobalConstants.LOGO_URL + savedPayListModel.logoSrc).transform(new CircleTransformation()).into(backdrop);
            calculateValues();
        } else {
            savedPayListModel = new PayListModel();
        }

        if (profile != null) {
            woope_credit = (TextView) findViewById(R.id.woope_credit);
            woope_credit.setText(" استفاده از ووپ ("+String.valueOf(profile.getWoopeCreditString())+")");

            toman_credit = (TextView) findViewById(R.id.toman_credit);
            toman_credit.setText("استفاده از موجودی تومانی(" + String.valueOf(profile.getCreditString())+")");
        }

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();

        btn.setOnTouchListener(this);
        online_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline=true;
                pay_radio_cash.setVisibility(View.GONE);
                pay_radio_credit.setVisibility(View.VISIBLE);
                credit_layout.setVisibility(View.VISIBLE);
                woope_layout.setVisibility(View.VISIBLE);

            }
        });
        cash_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline=false;
                pay_radio_cash.setVisibility(View.VISIBLE);
                pay_radio_credit.setVisibility(View.GONE);
                credit_layout.setVisibility(View.GONE);
                woope_layout.setVisibility(View.GONE);
                switch_credit.setChecked(false);
                switch_woope.setChecked(false);
            }
        });
        drawer_pull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDetailShow){
                    isDetailShow=false;
                    drawer_icon.setBackgroundDrawable(ContextCompat.getDrawable(PayActivity.this, R.drawable.up_arrow));
                    detail_layout.setVisibility(View.GONE);
                }else{
                    isDetailShow=true;
                    drawer_icon.setBackgroundDrawable(ContextCompat.getDrawable(PayActivity.this, R.drawable.down_arrow));
                    detail_layout.setVisibility(View.VISIBLE);
                }
            }
        });


        switch_woope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateValues();
            }
        });
        switch_credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateValues();
            }
        });


        toolbar = (Toolbar) findViewById(R.id.pay_toolbar);
        toolbar.inflateMenu(R.menu.pay_toolbar_items);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }



    public void showhint() {

        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        // Likewise, this tap target will target the search button
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_support, "تماس با پشتیبانی", "در صورت وجود هرگونه مشکل یا ابهام در پرداخت با پشتیبانی تماس بگیرید")
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.colorAccent)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .textColor(android.R.color.black)
                                .id(2)
                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        SharedPreferences prefs =
                                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("PAYACTIVITYFIRSTRUN", false);
                        editor.commit();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });

        sequence.start();

    }

    private void calculateValues() {
        //int selectedId = payType.getCheckedRadioButtonId();

        total_price.setText(String.valueOf(totalPrice));
        long rw=0;
        if(savedPayListModel.returnPoint!=0){
            rw=((totalPrice)/savedPayListModel.basePrice)*savedPayListModel.returnPoint;
        }

        return_woope.setText(String.valueOf(rw));
        if (!isOnline) {
            pay_price.setText(commaSeprate(totalPrice));
            btn.setText("پرداخت ("+commaSeprate(totalPrice)+" تومان)");
            payPriceValue=totalPrice;
            toman_use.setText("0");
            woope_use.setText("0");
            remain_toman.setText("0");
            tax.setText("0");
        } else {
            long alpha=totalPrice-profile.getTomanCredit();
            long beta=totalPrice-(profile.getWoopeCredit()*1000);
            long gama=totalPrice-profile.getTomanCredit()-(profile.getWoopeCredit()*1000);

            //    long  remainToman=profile.getTomanCredit()-totalprice;
            toman_use.setText("0");
            if(!switch_credit.isChecked()&&!switch_woope.isChecked()){
                pay_price.setText(commaSeprate(totalPrice));
                btn.setText("پرداخت ("+commaSeprate(totalPrice)+" تومان)");
                payPriceValue=totalPrice;
                toman_use.setText("0");
                woope_use.setText("0");
                remain_toman.setText("0");
                tax.setText("0");
            }
            if(switch_credit.isChecked()&&!switch_woope.isChecked()){
                if(alpha==0){
                    pay_price.setText("0");
                    btn.setText("پرداخت ("+"0"+" تومان)");
                    payPriceValue=0;
                    toman_use.setText(commaSeprate(totalPrice));
                    woope_use.setText("0");
                    remain_toman.setText("0");
                    tax.setText("0");
                }else if(alpha<0){
                    pay_price.setText("0");
                    btn.setText("پرداخت ("+"0"+" تومان)");
                    payPriceValue=0;
                    toman_use.setText(commaSeprate(totalPrice));
                    woope_use.setText("0");
                    remain_toman.setText("0");
                    tax.setText("0");
                }else if(alpha>0){
                    pay_price.setText(commaSeprate(alpha));
                    btn.setText("پرداخت ("+commaSeprate(alpha)+" تومان)");
                    payPriceValue=alpha;
                    toman_use.setText(commaSeprate(profile.getTomanCredit()));
                    woope_use.setText("0");
                    remain_toman.setText("0");
                    tax.setText("0");
                }
            }
            if(!switch_credit.isChecked()&&switch_woope.isChecked()){
                if(beta==0){
                    pay_price.setText("0");
                    btn.setText("پرداخت ("+"0"+" تومان)");
                    payPriceValue=0;
                    woope_use.setText(commaSeprate(profile.getWoopeCredit()));
                    toman_use.setText("0");
                    remain_toman.setText("0");
                    tax.setText("0");
                }else if(beta<0){
                    double integerPart = Math.ceil((double) totalPrice/1000);
                    int remainder=(int)Math.abs(beta%1000);
                    pay_price.setText("0");
                    btn.setText("پرداخت ("+"0"+" تومان)");
                    payPriceValue=0;
                    toman_use.setText("0");
                    woope_use.setText(String.valueOf(Math.abs(integerPart)));
                    remain_toman.setText(commaSeprate(remainder));
                    tax.setText("0");
                }else if(beta>0){
                    pay_price.setText(commaSeprate(beta));
                    btn.setText("پرداخت ("+commaSeprate(beta)+" تومان)");
                    payPriceValue=beta;
                    woope_use.setText(commaSeprate(profile.getWoopeCredit()));
                    toman_use.setText("0");
                    remain_toman.setText("0");
                    tax.setText("0");
                }
            }
            if(switch_credit.isChecked()&&switch_woope.isChecked()){
                if(alpha<=0){
                    pay_price.setText("0");
                    btn.setText("پرداخت ("+"0"+" تومان)");
                    payPriceValue=0;
                    toman_use.setText(commaSeprate(totalPrice));
                    woope_use.setText("0");
                    remain_toman.setText("0");
                    tax.setText("0");
                }else {
                    if(gama==0){
                        pay_price.setText("0");
                        btn.setText("پرداخت ("+"0"+" تومان)");
                        payPriceValue=0;
                        toman_use.setText(commaSeprate(profile.getTomanCredit()));
                        woope_use.setText(commaSeprate(profile.getWoopeCredit()));
                        remain_toman.setText("0");
                        tax.setText("0");
                    }else if(gama>0){
                        pay_price.setText(commaSeprate(gama));
                        btn.setText("پرداخت ("+commaSeprate(gama)+" تومان)");
                        payPriceValue=gama;
                        toman_use.setText(commaSeprate(profile.getTomanCredit()));
                        woope_use.setText(commaSeprate(profile.getWoopeCredit()));
                        remain_toman.setText("0");
                        tax.setText("0");
                    }else if(gama<0){
                        double integerPart = Math.ceil((double) alpha/1000);
                        int remainder=(int)Math.abs(gama%1000);
                        pay_price.setText("0");
                        btn.setText("پرداخت ("+"0"+" تومان)");
                        payPriceValue=0;
                        toman_use.setText(commaSeprate(profile.getTomanCredit()));
                        woope_use.setText(String.valueOf(Math.abs(integerPart)));
                        remain_toman.setText(String.valueOf(remainder));
                        tax.setText("0");
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (automaticPayFlag) {
            ConfirmPayment(savedPayListModel);
            //getProfileFromServer();
        }

//        SharedPreferences prefs =
//                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
//
//        boolean isFirstRun = prefs.getBoolean("PAYFIRSTRUN", true);
//        if (isFirstRun) {
//
//            showhint();
//
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.pay_toolbar_items, menu);

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
////                view = findViewById(R.id.menu_refresh_button);
//                // view.startAnimation(animation);
//
//                showhint();
//
//            }
//        });

        SharedPreferences prefs =
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        boolean isFirstRun = prefs.getBoolean("PAYACTIVITYFIRSTRUN", true);
        if (isFirstRun)
        {
            // Code to run once

            showhint();

        }

        return super.onCreateOptionsMenu(menu);
    }

    public void gotoPayCash(PayListModel model) {
        Intent myIntent = new Intent(PayActivity.this, CashPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile); //Optional parameters
        this.startActivityForResult(myIntent, SHOULD_GET_PROFILE);
        this.finish();
    }

    public void setNext(PayListModel payListModel) {
        calculateValues();

        if(payPriceValue==0){

            ConfirmPayment(payListModel);
        } else {
            GetPayInfo(payListModel);
        }
    }

    private void saveTransaction() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        TransactionInterface providerApiInterface =
                retrofit.create(TransactionInterface.class);


        //int selectedId = payType.getCheckedRadioButtonId();
        int pt = 0;
        //String payType = sp.getMode();
        if (!isOnline) {
            //go to cash pay
            //gotoPayCash();
            pt = 1;
        } else {
            //go to credit pay
            pt = 2;
        }

        SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<PayListModel> call =
                providerApiInterface.InsertTransaction("bearer "+authToken, savedPayListModel.id, savedPayListModel.branchId, String.valueOf(totalPrice), pt,switch_credit.isChecked(),switch_woope.isChecked());


        call.enqueue(new Callback<PayListModel>() {
            @Override
            public void onResponse(Call<PayListModel> call, Response<PayListModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayListModel model = response.body();
                    savedPayListModel = model;
                    //PayState sp = (PayState) spinner.getSelectedItem();
                    if (!isOnline) {
                        //go to cash pay
                        gotoPayCash(model);
                    } else {
                        //go to credit pay
                        setNext(model);
                    }
                }
            }

            @Override
            public void onFailure(Call<PayListModel> call, Throwable t) {
                Toast.makeText(PayActivity.this, "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });

    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
        btn.setEnabled(false);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
        btn.setEnabled(true);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.payBtn:
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        //if (!TextUtils.isEmpty(amount.getText())) {
                            saveTransaction();
                        /*} else {
                            showFillError();
                        }*/
                        return true;
                    }
                    break;

                /*case R.id.pay_cash_radio:
                    if (!cash_radio.isEnabled()) {

                        Toast.makeText(
                                this
                                , getResources().getString(R.string.askStoreForCashPay),
                                Toast.LENGTH_LONG).show();
                    }
                    break;*/
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if(isDetailShow){
                isDetailShow=false;
                drawer_icon.setBackgroundDrawable(ContextCompat.getDrawable(PayActivity.this, R.drawable.up_arrow));
                detail_layout.setVisibility(View.GONE);
            }else{
                showDialog(); // close this activity and return to preview activity (if there is any)
            }
        }
        if (item.getItemId() == R.id.action_support) {

            Intent goto_verifphone = new Intent(this,
                    ContactUsActivity.class);
            startActivity(goto_verifphone);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isDetailShow){
            isDetailShow=false;
            drawer_icon.setBackgroundDrawable(ContextCompat.getDrawable(PayActivity.this, R.drawable.up_arrow));
            detail_layout.setVisibility(View.GONE);
        }else{
            showDialog(); // close this activity and return to preview activity (if there is any)
        }
    }

    public void showDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PayActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("خروج از پرداخت؟");
        alertBuilder.setMessage("شما در هر زمان می‌توانید پرداخت خود را از لیست پرداخت‌ها کامل کنید");
        alertBuilder.setPositiveButton("باشه",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertBuilder.setNegativeButton("نه، ادامه میدم",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /*private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    totalPrice=longval;
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                    calculateValues();
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                amount.addTextChangedListener(this);
            }
        };
    }*/

    private String commaSeprate(long longval){

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        String formattedString = formatter.format(longval);
        return formattedString;
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
                    calculateValues();
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });


    }

    public void ConfirmPayment(final PayListModel payListModel) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        TransactionInterface providerApiInterface =
                retrofit.create(TransactionInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<PayListModel> call =
                providerApiInterface.GetConfirmCode("Bearer "+authToken, payListModel.id);
        call.enqueue(new Callback<PayListModel>() {
            @Override
            public void onResponse(Call<PayListModel> call, Response<PayListModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    if (response.body().getStatus() == 101) {
                        //PayListModel trans = response.body();
                        payListModel.confirmationCode=response.body().getMessage();
                        gotoPayCodeActivity(payListModel);
                    } else {

                        /*Toast.makeText(
                                PayActivity.this
                                , response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();*/

                    }
                }
            }

            @Override
            public void onFailure(Call<PayListModel> call, Throwable t) {
                hideProgreeBar();
            }
        });

    }

    public void getProfileFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        ProfileInterface providerApiInterface =
                retrofit.create(ProfileInterface.class);
        showProgreeBar();
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");
        Call<Profile> call =
                providerApiInterface.getProfileFromServer("bearer " + authToken);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    profile = response.body();
                    //profile=user.getMessage();
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(profile);
                    prefsEditor.putString(PROFILE, json);
                    prefsEditor.apply();
                    calculateValues();
                    //setNext();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                hideProgreeBar();
            }
        });

    }

    public void gotoPayCodeActivity(PayListModel trans) {

        Intent myIntent = new Intent(PayActivity.this, PayCodeActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, trans); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(POINTS_PAYED, payedPoints);
        startActivity(myIntent);
        this.finish();
    }

    public void GetPayInfo(PayListModel model) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        TransactionInterface providerApiInterface =
                retrofit.create(TransactionInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<PayResponseModel> call =
                providerApiInterface.GetPayInfo("Bearer " + authToken, model.id);
        call.enqueue(new Callback<PayResponseModel>() {
            @Override
            public void onResponse(Call<PayResponseModel> call, Response<PayResponseModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayResponseModel bankModel = response.body();
                    gotoBankPage(bankModel);
                    //gotoPayCodeActivity(trans);
                }
            }

            @Override
            public void onFailure(Call<PayResponseModel> call, Throwable t) {
                hideProgreeBar();
            }
        });

    }

    private void gotoBankPage(PayResponseModel bankModel) {
        automaticPayFlag=true;
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savedPayListModel);
        prefsEditor.putString(PAY_LIST_ITEM, json);
        prefsEditor.apply();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bankModel.bankUrl));
        startActivity(browserIntent);
    }

    public Profile getUserProfile() {
            Gson gson = new Gson();
            final SharedPreferences prefs =
                    this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
            String profileString = prefs.getString(Constants.GlobalConstants.PROFILE, null);
            if (profileString != null) {
                profile = (Profile) gson.fromJson(profileString, Profile.class);
                return profile;
            } else {
                return new Profile();
            }
    }
    public PayListModel getSavedPayList() {
        Gson gson = new Gson();
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String payListString = prefs.getString(PAY_LIST_ITEM, null);
        if (payListString != null) {
            savedPayListModel = (PayListModel) gson.fromJson(payListString, PayListModel.class);
            return savedPayListModel;
        } else {
            return new PayListModel();
        }
    }
}

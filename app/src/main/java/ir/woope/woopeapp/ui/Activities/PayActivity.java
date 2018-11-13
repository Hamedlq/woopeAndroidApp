package ir.woope.woopeapp.ui.Activities;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

import android.widget.RelativeLayout.LayoutParams;

public class PayActivity extends AppCompatActivity implements View.OnTouchListener {
    //@BindView(R.id.StoreName) TextView StoreName_tv;
    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.switch_credit)
    protected Switch switch_credit;
    @BindView(R.id.switch_woope)
    protected Switch switch_woope;
    @BindView(R.id.amount)
    protected EditText amount;
    @BindView(R.id.StoreName)
    protected TextView StoreName_tv;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

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
    Button btn;
    //ProgressBar progressBar;
    LinearLayout cash_layout;
    RadioButton cash_radio;
    LinearLayout credit_layout;
    RadioButton credit_radio;
    String profileString;
    boolean flag;
    //boolean saveTransactionFlag=true;
    PayListModel savedPayListModel;

    RadioGroup payType;

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
        amount.addTextChangedListener(onTextChangedListener());

        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            savedPayListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
            getStore(savedPayListModel.branchId);
            amount.setText(String.valueOf(savedPayListModel.totalPrice));
            StoreName_tv.setText(savedPayListModel.storeName);
            Picasso.with(PayActivity.this).load(Constants.GlobalConstants.LOGO_URL + savedPayListModel.logoSrc).into(backdrop);
        }else {
            savedPayListModel=new PayListModel();
        }

        if (profile != null) {
            woope_credit = (TextView) findViewById(R.id.woope_credit);
            woope_credit.setText(String.valueOf(profile.getWoopeCreditString()));

            toman_credit = (TextView) findViewById(R.id.toman_credit);
            toman_credit.setText(String.valueOf(profile.getCreditString()));
        }

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();

        btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(this);
//        cash_layout = (LinearLayout) findViewById(R.id.cash_layout);
//        cash_radio = (RadioButton) findViewById(R.id.cash_radio);
//        credit_layout = (LinearLayout) findViewById(R.id.credit_layout);
//        credit_radio = (RadioButton) findViewById(R.id.credit_radio);
//        cash_layout.setOnTouchListener(this);
//        cash_radio.setOnTouchListener(this);
//        credit_layout.setOnTouchListener(this);
//        credit_radio.setOnTouchListener(this);
//        cash_layout.setBackgroundColor(getResources().getColor(R.color.choice_selected));
//        cash_radio.setChecked(true);

        payType = findViewById(R.id.radioGroup_payType);



        cash_radio = findViewById(R.id.pay_cash_radio);
        cash_radio.setOnTouchListener(this);
        credit_radio = findViewById(R.id.pay_credit_radio);


//            cash_radio.setEnabled(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.pay_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            getProfileFromServer();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.pay_toolbar_items, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    public void gotoPayCash(PayListModel model) {
        Intent myIntent = new Intent(PayActivity.this, CashPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile); //Optional parameters
        this.startActivityForResult(myIntent,SHOULD_GET_PROFILE);
        this.finish();
    }

    public void setNext(PayListModel payListModel) {
        flag=true;
        float credit=profile.getTomanCredit()+(profile.getWoopeCredit()*1000);
        if(credit>=payListModel.totalPrice){
            ConfirmPayment(payListModel);
        }else {
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



        int selectedId = payType.getCheckedRadioButtonId();
        int pt = 0;
        //String payType = sp.getMode();
        if (selectedId == R.id.pay_cash_radio) {
            //go to cash pay
            //gotoPayCash();
            pt = 1;
        } else if (selectedId == R.id.pay_credit_radio) {
            //go to credit pay
            pt = 2;
        }

        SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<PayListModel> call =
                providerApiInterface.InsertTransaction("bearer "+authToken, savedPayListModel.id, savedPayListModel.branchId, amount.getText().toString(), pt,switch_credit.isChecked(),switch_woope.isChecked());


        call.enqueue(new Callback<PayListModel>() {
            @Override
            public void onResponse(Call<PayListModel> call, Response<PayListModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayListModel model = response.body();
                    //PayState sp = (PayState) spinner.getSelectedItem();
                    if (cash_radio.isChecked()) {
                        //go to cash pay
                        gotoPayCash(model);
                    } else if (credit_radio.isChecked()) {
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
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
//                case R.id.credit_layout:
//                case R.id.credit_radio:
//                    credit_radio.setChecked(true);
//                    cash_radio.setChecked(false);
//                    credit_layout.setBackgroundColor(getResources().getColor(R.color.choice_selected));
//                    cash_layout.setBackgroundColor(getResources().getColor(R.color.choice_not_selected));
//
//                    break;
//                case R.id.cash_layout:
//                case R.id.cash_radio:
//                    if(store.isCashPayAllowed) {
//                        cash_radio.setChecked(true);
//                        credit_radio.setChecked(false);
//                        cash_layout.setBackgroundColor(getResources().getColor(R.color.choice_selected));
//                        credit_layout.setBackgroundColor(getResources().getColor(R.color.choice_not_selected));
//                    }
//                    break;
                case R.id.button:
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!TextUtils.isEmpty(amount.getText())) {
                                saveTransaction();
                        } else {
                            showFillError();
                        }
                        return true;
                    }
                    break;

                case R.id.pay_cash_radio:
                        if (!cash_radio.isEnabled()) {

                            Toast.makeText(
                                    this
                                    , getResources().getString(R.string.askStoreForCashPay),
                                    Toast.LENGTH_LONG).show();
                        }
                    break;
            }
        }
        return false;
    }

    private void showFillError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("لطفا مبلغ خرید را وارد کنید").setPositiveButton("باشه", ConfirmDialogClickListener).show();
    }

    DialogInterface.OnClickListener ConfirmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        if (item.getItemId() == R.id.action_support) {

            Intent goto_verifphone = new Intent(this,
                    ContactUsActivity.class);
            startActivity(goto_verifphone);

        }

        return super.onOptionsItemSelected(item);
    }

    private TextWatcher onTextChangedListener() {
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
        };
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
                providerApiInterface.getStore("bearer " + authToken,storeId);


        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    store = response.body();
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });


    }

    public void ConfirmPayment(PayListModel payListModel){
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
                providerApiInterface.GetConfirmCode("Bearer "+authToken, payListModel.id,payListModel.pointPayString());
        call.enqueue(new Callback<PayListModel>() {
            @Override
            public void onResponse(Call<PayListModel> call, Response<PayListModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayListModel trans = response.body();
                    gotoPayCodeActivity(trans);
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
                    //setNext();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                hideProgreeBar();
            }
        });

    }

    public void gotoPayCodeActivity(PayListModel trans){

        Intent myIntent = new Intent(PayActivity.this, PayCodeActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, trans); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(POINTS_PAYED, payedPoints);
        startActivity(myIntent);
        this.finish();
    }
    public void GetPayInfo(PayListModel model){
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
                providerApiInterface.GetPayInfo("Bearer "+authToken, model.id);
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bankModel.bankUrl));
        startActivity(browserIntent);
    }
}

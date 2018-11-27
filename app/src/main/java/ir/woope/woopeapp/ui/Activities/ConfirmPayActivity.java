package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class ConfirmPayActivity extends AppCompatActivity {

    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.return_woope_credit)
    protected TextView return_woope_credit;
    @BindView(R.id.cancelBtn)
    protected Button cancelBtn;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    String profileString;
    String transactionString;
    //String payedPoints;
    Profile profile;
    String authToken;
    PayListModel payListModel;
    boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            payListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
            //payedPoints= getIntent().getStringExtra(POINTS_PAYED);
        }
        flag=false;
        TextView pointText = findViewById(R.id.pointText);
        return_woope_credit.setText(String.valueOf(payListModel.returnWoope));

        TextView payAmount = findViewById(R.id.payAmount);
        payAmount.setText(payListModel.pointPayString());
        hideProgreeBar();

        TextView total_price = findViewById(R.id.total_price);
        total_price.setText(payListModel.getWoopePriceString());

        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(payListModel.storeName);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    setNext();
                    //gotoBankActivity();
                }
                return false;
            }
        });

        cancelBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    finish();
                }
                return false;
            }
        });
        Picasso.with(ConfirmPayActivity.this).load(Constants.GlobalConstants.LOGO_URL + payListModel.logoSrc).into(backdrop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.confirmpay_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pay_toolbar_items, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        if(flag){
            getProfileFromServer();
        }

    }

    public void setNext() {
        flag=true;
        float credit=profile.getTomanCredit()+profile.getWoopeCredit();
        if(credit>=payListModel.totalPrice){
            ConfirmPayment();
        }else {
            GetPayInfo();
        }
   }

    public void ConfirmPayment(){
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

        Intent myIntent = new Intent(ConfirmPayActivity.this, PayCodeActivity.class);
        payListModel.confirmationCode=trans.confirmationCode;
        myIntent.putExtra(PAY_LIST_ITEM, payListModel); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(POINTS_PAYED, payedPoints);
        startActivity(myIntent);
        this.finish();
    }

    /*    public void gotoBankActivity(){
        //Intent myIntent = new Intent(ConfirmPayActivity.this, PayCodeActivity.class);
        Intent myIntent = new Intent(ConfirmPayActivity.this, BankActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, payListModel); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(POINTS_PAYED, payedPoints);
        startActivity(myIntent);
        this.finish();
    }*/

    public void GetPayInfo(){
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
                providerApiInterface.GetPayInfo("Bearer "+authToken, payListModel.id);
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
       /* WebView webview = new WebView(this);
        setContentView(webview);
        String url = "http://www.example.com";
        String postData = null;
        try {
            postData = "Token=" + URLEncoder.encode(bankModel.token, "UTF-8") + "&RedirectURL=" + URLEncoder.encode(bankModel.redirectURL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webview.postUrl(url,postData.getBytes());*/

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bankModel.bankUrl));
        startActivity(browserIntent);
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }


    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }
}

package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

public class CashPayActivity extends AppCompatActivity {

    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.store_name)
    protected TextView StoreName_tv;
    String authToken;
    String profileString;
    String transactionString;
    String payedPoints;
    Profile profile;
    PayListModel payListModel;

    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pay);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            payListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
            payedPoints = getIntent().getStringExtra(POINTS_PAYED);
        }
        //TextView StoreName=(TextView) findViewById(R.id.StoreName);
        StoreName_tv.setText(payListModel.storeName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();


        final EditText ConfirmCode = findViewById(R.id.ConfirmCode);
        Button btn = findViewById(R.id.button);

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    ConfirmPayment(ConfirmCode.getText().toString());
                }
                return false;
            }
        });
        Picasso.with(CashPayActivity.this).load(Constants.GlobalConstants.LOGO_URL + payListModel.logoSrc).transform(new CircleTransformation()).into(backdrop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.pay_toolbar_items, menu);
       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
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

    public void ConfirmPayment(String confirmCode) {
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
        Call<ApiResponse> call =
                providerApiInterface.SendConfirmCode("bearer " + authToken, payListModel.id, confirmCode);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    ApiResponse res = response.body();
                    String x = res.getMessage();
                    if (response.body().getStatus() == 101) {
                        Toast.makeText(CashPayActivity.this, x, Toast.LENGTH_LONG).show();
                        Intent goto_main = new Intent(CashPayActivity.this,
                                MainActivity.class);
                        goto_main.putExtra(GET_PROFILE_FROM_SERVER, true);
                        goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(goto_main);
                    } else {
                        Toast.makeText(CashPayActivity.this, x, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
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

}

package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

public class BankActivity extends AppCompatActivity {
    @BindView(R.id.button)
    protected Button btn;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    String profileString;
    String transactionString;
    //String payedPoints;
    Profile profile;
    String authToken;
    PayListModel payListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            payListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
            //payedPoints = getIntent().getStringExtra(POINTS_PAYED);
        }
        hideProgreeBar();

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    ConfirmPayment();
                }
                return false;
            }
        });


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



    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void gotoPayCodeActivity(PayListModel trans){

        Intent myIntent = new Intent(BankActivity.this, PayCodeActivity.class);
        payListModel.confirmationCode=trans.confirmationCode;
        myIntent.putExtra(PAY_LIST_ITEM, payListModel); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(POINTS_PAYED, payedPoints);
        startActivity(myIntent);
        this.finish();
    }
}

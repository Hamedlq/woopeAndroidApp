package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.PayArrayAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.PayState;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

public class CashPayActivity extends AppCompatActivity {

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

        Intent intent = getIntent();
        //storeName = intent.getStringExtra(STORE_NAME);
        profileString = intent.getStringExtra(PREF_PROFILE);
        transactionString = intent.getStringExtra(PAY_LIST_ITEM);
        payedPoints = intent.getStringExtra(POINTS_PAYED);

        Gson gson = new Gson();
        profile = (Profile) gson.fromJson(profileString, Profile.class);
        payListModel = (PayListModel) gson.fromJson(transactionString, PayListModel.class);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();


        final EditText ConfirmCode=findViewById(R.id.ConfirmCode);
        Button btn=findViewById(R.id.button);

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    ConfirmPayment(ConfirmCode.getText().toString());
                }
                return false;
            }
        });




        //Intent intent = getIntent();
        /*String storeName = intent.getStringExtra("StoreName");
        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(storeName);*/
        //Switch mSwitch=findViewById(R.id.switchBtn);
        /*mSwitch.setTrackDrawable(new SwitchDrawable(this,
                R.string.right_switch, R.string.left_switch));*/

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Spinner element
        //Spinner spinner = (Spinner) findViewById(R.id.spinnerBtn);

        //PayState[] states = new PayState[] { new PayState("اعتباری", "0"),  };
        /*List<PayState> states= new ArrayList<PayState>();
        states.add(new PayState("پرداخت اعتباری", "0"));
        states.add(new PayState("پرداخت نقدی","0") );
        ArrayAdapter adapter = new PayArrayAdapter(this, states);
        //adapter.setDropDownViewResource(R.layout.spinner_row);
       *//* String arr[] = { "1", "2", "3" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                PayActivity.this, R.layout.spinner_row, R.id.credit,arr);*//*
        spinner.setAdapter(adapter);

*/


    }

    public void ConfirmPayment(String confirmCode){
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
                providerApiInterface.SendConfirmCode(authToken, payListModel.id,confirmCode);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    ApiResponse res = response.body();
                    String x=res.getMessage();
                    Toast.makeText(CashPayActivity.this,x,Toast.LENGTH_LONG).show();
                    finish();
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

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

import com.google.gson.Gson;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.PayListModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

public class ConfirmPayActivity extends AppCompatActivity {


    //String storeName;
    String profileString;
    String transactionString;
    String payedPoints;
    Profile profile;
    String authToken;
    PayListModel payListModel;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay);

        Intent intent = getIntent();
        //storeName = intent.getStringExtra(STORE_NAME);
        profileString = intent.getStringExtra(PREF_PROFILE);
        transactionString = intent.getStringExtra(PAY_LIST_ITEM);
        payedPoints = intent.getStringExtra(POINTS_PAYED);

        Gson gson = new Gson();
        profile = (Profile) gson.fromJson(profileString, Profile.class);
        payListModel = (PayListModel) gson.fromJson(transactionString, PayListModel.class);

        TextView pointText = findViewById(R.id.pointText);
        TextView payAmount = findViewById(R.id.payAmount);

        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(payListModel.storeName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                providerApiInterface.GetConfirmCode(authToken, payListModel.id,payedPoints);
        call.enqueue(new Callback<PayListModel>() {
            @Override
            public void onResponse(Call<PayListModel> call, Response<PayListModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayListModel trans = response.body();
                    //payListModel =trans.getMessage();
                    Gson gson = new Gson();
                    String transModel = gson.toJson(trans);

                    //myIntent.putExtra(BUY_AMOUNT, String.valueOf(model.totalPrice)); //Optional parameters

                    Intent myIntent = new Intent(ConfirmPayActivity.this, PayCodeActivity.class);
                    myIntent.putExtra(PAY_LIST_ITEM, transModel); //Optional parameters
                    //myIntent.putExtra(STORE_NAME, storeName); //Optional parameters
                    //myIntent.putExtra(BUY_AMOUNT, amount.getText().toString()); //Optional parameters
                    myIntent.putExtra(PREF_PROFILE, profileString); //Optional parameters
                    startActivity(myIntent);
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

}

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

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.PayArrayAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.PayState;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOTAL_PRICE;

public class PayActivity extends AppCompatActivity {
    String storeName;
    long totalPrice;
    Spinner spinner;
    EditText amount;
    String authToken;
    Profile profile;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Intent intent = getIntent();
        storeName = intent.getStringExtra(STORE_NAME);
        totalPrice = intent.getLongExtra(TOTAL_PRICE,0);
        profile=(Profile) intent.getParcelableExtra(PREF_PROFILE);
        TextView StoreName_tv=findViewById(R.id.StoreName);
        amount=findViewById(R.id.amount);
        StoreName_tv.setText(storeName);
        if(totalPrice!=0){
            amount.setText(String.valueOf(totalPrice));
        }

        //Switch mSwitch=findViewById(R.id.switchBtn);
        /*mSwitch.setTrackDrawable(new SwitchDrawable(this,
                R.string.right_switch, R.string.left_switch));*/

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinnerBtn);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();
        //PayState[] states = new PayState[] { new PayState("اعتباری", "0"),  };
        List<PayState> states= new ArrayList<PayState>();
        states.add(new PayState("پرداخت اعتباری", "0"));
        states.add(new PayState("پرداخت نقدی","0") );
        ArrayAdapter adapter = new PayArrayAdapter(this, states);
        //adapter.setDropDownViewResource(R.layout.spinner_row);
       /* String arr[] = { "1", "2", "3" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                PayActivity.this, R.layout.spinner_row, R.id.credit,arr);*/
        spinner.setAdapter(adapter);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    saveTransaction();
                    return true;
                }
                return false;
            }
        });

    }

    public void gotoPayCash(){
        Intent myIntent = new Intent(PayActivity.this, CashPayActivity.class);
        myIntent.putExtra("StoreName", storeName); //Optional parameters
        this.startActivity(myIntent);
        this.finish();
    }

    public void gotoCreditCash(){
        Intent myIntent = new Intent(PayActivity.this, CreditPayActivity.class);
        myIntent.putExtra("StoreName", storeName); //Optional parameters
        this.startActivity(myIntent);
        this.finish();
    }


    private void saveTransaction() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        TransactionInterface providerApiInterface =
                retrofit.create(TransactionInterface.class);
        PayState sp=(PayState)spinner.getSelectedItem();
        int pt=0;
        String payType=sp.getMode();
        if(sp.getMode().equals("پرداخت نقدی")){
            //go to cash pay
            //gotoPayCash();
            pt=1;
        }else {
            //go to credit pay
            pt=2;
        }
        SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<ApiResponse> call =
                providerApiInterface.InsertTransaction(authToken,"1",amount.getText().toString(),pt);


        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayState sp=(PayState)spinner.getSelectedItem();
                    if(sp.getMode().equals("پرداخت نقدی")){
                        //go to cash pay
                        gotoPayCash();
                    }else {
                        //go to credit pay
                        gotoCreditCash();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
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


}

package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ir.woope.woopeapp.R;

public class ConfirmPayActivity extends AppCompatActivity {


    String storeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay);
        TextView pointText = findViewById(R.id.pointText);
        TextView payAmount = findViewById(R.id.payAmount);
        //pointText.setText(storeName);
        Intent intent = getIntent();
        String storeName = intent.getStringExtra("StoreName");
        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(storeName);


        Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gotoPayCash();
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


    public void gotoPayCash(){
        Intent myIntent = new Intent(ConfirmPayActivity.this, PayCodeActivity.class);
        myIntent.putExtra("StoreName", storeName); //Optional parameters
        this.startActivity(myIntent);
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
                    gotoPayCodeActivity(trans);
                }
            }

            @Override
            public void onFailure(Call<PayListModel> call, Throwable t) {
                hideProgreeBar();
            }
        });

    }

    public void gotoPayCodeActivity(PayListModel trans){
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
        this.finish();
    }

    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

}

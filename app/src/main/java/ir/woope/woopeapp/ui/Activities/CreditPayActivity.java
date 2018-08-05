package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Store;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class CreditPayActivity extends AppCompatActivity {

    String profileString;
    Profile profile;
    PayListModel payListModel;
    EditText pointText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_pay);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            payListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
        }

        pointText = findViewById(R.id.pointText);
        TextView payAmount = findViewById(R.id.payAmount);
        TextView toman_credit = findViewById(R.id.toman_credit);
        TextView woope_credit = findViewById(R.id.woope_credit);


        //buyAmount = intent.getStringExtra(BUY_AMOUNT);
        payAmount.setText(String.valueOf(payListModel.totalPrice));
        toman_credit.setText(profile.getCreditString());
        woope_credit.setText(profile.getWoopeCreditString());


        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(String.valueOf(payListModel.storeName));

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gotoConfirmCreditPay();
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

    public void gotoConfirmCreditPay(){
        Intent myIntent = new Intent(CreditPayActivity.this, ConfirmPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, payListModel); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        String st= pointText.getText().toString();
        myIntent.putExtra(POINTS_PAYED, st);
        //myIntent.putExtra("StoreName", storeName); //Optional parameters
        this.startActivity(myIntent);
        this.finish();
    }
}

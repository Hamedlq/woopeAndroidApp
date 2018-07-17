package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ir.woope.woopeapp.R;

public class PayCodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_pay);
        TextView pointText = findViewById(R.id.pointText);
        TextView payAmount = findViewById(R.id.payAmount);
        //pointText.setText(storeName);
        Intent intent = getIntent();
        String storeName = intent.getStringExtra("StoreName");
        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(storeName);




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
}

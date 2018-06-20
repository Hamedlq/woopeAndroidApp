package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.PayArrayAdapter;
import ir.woope.woopeapp.models.PayState;

public class PayActivity extends AppCompatActivity {
    String storeName;
    Spinner spinner;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Intent intent = getIntent();
        storeName = intent.getStringExtra("StoreName");
        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(storeName);
        //Switch mSwitch=findViewById(R.id.switchBtn);
        /*mSwitch.setTrackDrawable(new SwitchDrawable(this,
                R.string.right_switch, R.string.left_switch));*/

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinnerBtn);

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
                PayState sp=(PayState)spinner.getSelectedItem();
                if(sp.getMode().equals("پرداخت نقدی")){
                    //go to cash pay
                    gotoPayCash();
                }else {
                    //go to credit pay
                    gotoCreditCash();
                }
                return false;
            }
        });

    }

    public void gotoPayCash(){
        Intent myIntent = new Intent(PayActivity.this, CashPayActivity.class);
        myIntent.putExtra("StoreName", storeName); //Optional parameters
        this.startActivity(myIntent);
    }

    public void gotoCreditCash(){
        Intent myIntent = new Intent(PayActivity.this, CreditPayActivity.class);
        myIntent.putExtra("StoreName", storeName); //Optional parameters
        this.startActivity(myIntent);
    }
}

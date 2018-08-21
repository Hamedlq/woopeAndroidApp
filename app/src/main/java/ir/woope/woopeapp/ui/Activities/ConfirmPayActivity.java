package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class ConfirmPayActivity extends AppCompatActivity {

    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.return_woope_credit)
    protected TextView return_woope_credit;
    @BindView(R.id.cancelBtn)
    protected Button cancelBtn;
    String profileString;
    String transactionString;
    //String payedPoints;
    Profile profile;
    String authToken;
    PayListModel payListModel;

    ProgressBar progressBar;

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

        TextView pointText = findViewById(R.id.pointText);
        return_woope_credit.setText(String.valueOf(payListModel.returnWoope));

        TextView payAmount = findViewById(R.id.payAmount);
        payAmount.setText(payListModel.pointPayString());


        TextView total_price = findViewById(R.id.total_price);
        total_price.setText(payListModel.getWoopePriceString());

        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(payListModel.storeName);


        Button btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gotoBankActivity();
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

    }

    public void gotoBankActivity(){
        //Intent myIntent = new Intent(ConfirmPayActivity.this, PayCodeActivity.class);
        Intent myIntent = new Intent(ConfirmPayActivity.this, BankActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, payListModel); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(POINTS_PAYED, payedPoints);
        startActivity(myIntent);
        this.finish();
    }
}

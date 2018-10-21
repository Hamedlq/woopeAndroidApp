package ir.woope.woopeapp.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Store;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class CreditPayActivity extends AppCompatActivity {

    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.cancelBtn)
    protected Button cancelBtn;
   /* @BindView(R.id.progressBar)
    protected ProgressBar progressBar;*/
    String profileString;
    Profile profile;
    PayListModel payListModel;
    EditText pointText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_pay);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            payListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
        }
        pointText = findViewById(R.id.pointText);
        TextView payAmount = findViewById(R.id.payAmount);
        TextView toman_credit = findViewById(R.id.toman_credit);
        TextView woope_credit = findViewById(R.id.woope_credit);
        //pointText.setText(profile.getWoopeCreditString());

        //buyAmount = intent.getStringExtra(BUY_AMOUNT);
        payAmount.setText(String.valueOf(payListModel.totalPrice));
        toman_credit.setText(profile.getCreditString());
        woope_credit.setText(profile.getWoopeCreditString());


        TextView StoreName_tv = findViewById(R.id.StoreName);
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
        cancelBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent i = getIntent();
                    setResult(RESULT_OK, i);
                    finish();
                }
                return false;
            }
        });

        Picasso.with(CreditPayActivity.this).load(Constants.GlobalConstants.LOGO_URL + payListModel.logoSrc).into(backdrop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.creditpay_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pay_toolbar_items, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        if (item.getItemId() == R.id.action_support) {

        }

        return super.onOptionsItemSelected(item);
    }

    public void gotoConfirmCreditPay() {
        if (!TextUtils.isEmpty(pointText.getText())) {
            String value = pointText.getText().toString();
            int finalValue = Integer.parseInt(value);
            if (finalValue > profile.getWoopeCredit()) {
                showPointOverload();
                pointText.setText(profile.getWoopeCreditString());
            } else {
                payListModel.pointPay = finalValue;
                Intent myIntent = new Intent(CreditPayActivity.this, ConfirmPayActivity.class);
                myIntent.putExtra(PAY_LIST_ITEM, payListModel); //Optional parameters
                myIntent.putExtra(PREF_PROFILE, profile);
                //myIntent.putExtra(POINTS_PAYED, value);
                //myIntent.putExtra("StoreName", storeName); //Optional parameters
                this.startActivity(myIntent);
                Intent i = getIntent();
                setResult(RESULT_OK, i);
                this.finish();
            }
        } else {
            showFillError();
        }
    }

    private void showFillError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("لطفا تعداد ووپی که می‌خواهید برای این خرید استفاده کنید را وارد کنید").setPositiveButton("باشه", ConfirmDialogClickListener).show();
    }

    private void showPointOverload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("تعداد ووپ درخواستی نمی‌تواند بیشتر از اعتبار ووپ باشد").setPositiveButton("باشه", ConfirmDialogClickListener).show();
    }

    DialogInterface.OnClickListener ConfirmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    break;
/*                case DialogInterface.BUTTON_NEGATIVE:د
                    goToMainActivity();
                    dialog.dismiss();
                    break;*/
            }
        }
    };
}

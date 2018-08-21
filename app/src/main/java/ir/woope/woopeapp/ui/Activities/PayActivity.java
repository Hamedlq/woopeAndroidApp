package ir.woope.woopeapp.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;

public class PayActivity extends AppCompatActivity implements View.OnTouchListener {
    //@BindView(R.id.StoreName) TextView StoreName_tv;
    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    Store store;
    long totalPrice;
    //Spinner spinner;
    EditText amount;
    TextView woope_credit;
    TextView toman_credit;
    String authToken;
    Profile profile;
    String profileJson;
    Button btn;
    ProgressBar progressBar;
    LinearLayout cash_layout;
    RadioButton cash_radio;
    LinearLayout credit_layout;
    RadioButton credit_radio;
    String profileString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            store = (Store) getIntent().getExtras().getSerializable(STORE);
        }
        TextView StoreName_tv = findViewById(R.id.StoreName);
        amount = findViewById(R.id.amount);
        StoreName_tv.setText(store.storeName);
        if (totalPrice != 0) {
            amount.setText(String.valueOf(totalPrice));
        }
        if (profile != null) {
            woope_credit = (TextView) findViewById(R.id.woope_credit);
            woope_credit.setText(String.valueOf(profile.getWoopeCreditString()));

            toman_credit = (TextView) findViewById(R.id.toman_credit);
            toman_credit.setText(String.valueOf(profile.getCreditString()));
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();

        btn = (Button) findViewById(R.id.button);
        btn.setOnTouchListener(this);
        cash_layout = (LinearLayout) findViewById(R.id.cash_layout);
        cash_radio = (RadioButton) findViewById(R.id.cash_radio);
        credit_layout = (LinearLayout) findViewById(R.id.credit_layout);
        credit_radio = (RadioButton) findViewById(R.id.credit_radio);
        cash_layout.setOnTouchListener(this);
        cash_radio.setOnTouchListener(this);
        credit_layout.setOnTouchListener(this);
        credit_radio.setOnTouchListener(this);
        cash_layout.setBackgroundColor(getResources().getColor(R.color.choice_selected));
        cash_radio.setChecked(true);
        Picasso.with(PayActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).into(backdrop);

    }

    public void gotoPayCash(PayListModel model) {
        Intent myIntent = new Intent(PayActivity.this, CashPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile); //Optional parameters
        this.startActivity(myIntent);
        this.finish();
    }

    public void gotoCreditPay(PayListModel model) {
        Gson gson = new Gson();
        String transModel = gson.toJson(model);

        //myIntent.putExtra(BUY_AMOUNT, String.valueOf(model.totalPrice)); //Optional parameters
        Intent myIntent = new Intent(PayActivity.this, CreditPayActivity.class);
        myIntent.putExtra(PAY_LIST_ITEM, model); //Optional parameters
        myIntent.putExtra(PREF_PROFILE, profile); //Optional parameters
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
        //PayState sp = (PayState) spinner.getSelectedItem();
        int pt = 0;
        //String payType = sp.getMode();
        if (cash_radio.isChecked()) {
            //go to cash pay
            //gotoPayCash();
            pt = 1;
        } else {
            //go to credit pay
            pt = 2;
        }
        SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<PayListModel> call =
                providerApiInterface.InsertTransaction("bearer "+authToken, store.storeId, amount.getText().toString(), pt);


        call.enqueue(new Callback<PayListModel>() {
            @Override
            public void onResponse(Call<PayListModel> call, Response<PayListModel> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    PayListModel model = response.body();
                    //PayState sp = (PayState) spinner.getSelectedItem();
                    if (cash_radio.isChecked()) {
                        //go to cash pay
                        gotoPayCash(model);
                    } else {
                        //go to credit pay
                        gotoCreditPay(model);
                    }
                }
            }

            @Override
            public void onFailure(Call<PayListModel> call, Throwable t) {
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


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.credit_layout:
                case R.id.credit_radio:
                    credit_radio.setChecked(true);
                    cash_radio.setChecked(false);
                    credit_layout.setBackgroundColor(getResources().getColor(R.color.choice_selected));
                    cash_layout.setBackgroundColor(getResources().getColor(R.color.choice_not_selected));

                    break;
                case R.id.cash_layout:
                case R.id.cash_radio:
                    if(store.isCashPayAllowed) {
                        cash_radio.setChecked(true);
                        credit_radio.setChecked(false);
                        cash_layout.setBackgroundColor(getResources().getColor(R.color.choice_selected));
                        credit_layout.setBackgroundColor(getResources().getColor(R.color.choice_not_selected));
                    }
                    break;
                case R.id.button:
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!TextUtils.isEmpty(amount.getText())) {
                            saveTransaction();
                        }else {
                            showFillError();
                        }
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    private void showFillError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("لطفا مبلغ خرید را وارد کنید").setPositiveButton("باشه", ConfirmDialogClickListener).show();
    }
    DialogInterface.OnClickListener ConfirmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };
}

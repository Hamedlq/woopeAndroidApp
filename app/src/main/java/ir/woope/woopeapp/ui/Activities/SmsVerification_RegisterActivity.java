package ir.woope.woopeapp.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.chaos.view.PinView;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.LoginInterface;
import ir.woope.woopeapp.interfaces.RegisterInterface;
import ir.woope.woopeapp.models.AccessToken;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.receivers.SMSBroadcastReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.OPEN_MAIN_ACTIVITY;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SmsVerification_RegisterActivity extends AppCompatActivity {

    Retrofit retrofit_sms_verif, retrofit_login;

    Toolbar toolbar;

    ProgressBar progress_bar;

    PinView code;

    TextView countdown_timer;

    MaterialRippleLayout accept;
    TextView resend;
    boolean openMainActivity = true;
    AVLoadingIndicatorView loading;

    View layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sms_validation_register);

        layout = findViewById(R.id.activity_smsValidation_register);
        if (getIntent() != null && getIntent().getExtras() != null) {
            openMainActivity = getIntent().getExtras().getBoolean(OPEN_MAIN_ACTIVITY);
        }
        toolbar = (Toolbar) findViewById(R.id.sms_validation_register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.activity_smsValidation_register);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        countdown_timer = (TextView) findViewById(R.id.txt_countdown_register);
        final String recieved_code = getIntent().getStringExtra("validation_code");
        code = findViewById(R.id.pinView_smsValidation_register);
        progress_bar = (ProgressBar) findViewById(R.id.sms_validation_register_progressBar);

        loading = findViewById(R.id.progressBar_sms_verification_register);

        retrofit_login = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final LoginInterface login = retrofit_login.create(LoginInterface.class);

        retrofit_sms_verif = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final RegisterInterface send = retrofit_sms_verif.create(RegisterInterface.class);

        accept = findViewById(R.id.btn_accept_code_register);
        resend = findViewById(R.id.btn_resendcode_register);

        timer();
        smsReceiver();
        resend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                accept.setVisibility(View.VISIBLE);
                resend.animate().alpha(0.0f);
                resend.setVisibility(View.INVISIBLE);
                timer();

                send.send_code(getIntent().getExtras().getString("phone_number")).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

//                            progress.setVisibility(View.GONE);

//                            Toast.makeText(
//                                    SmsVerification_RegisterActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                        } else {

//                            progress.setVisibility(View.GONE);

//                            Toast.makeText(
//                                    SmsVerification_RegisterActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

//                        progress.setVisibility(View.GONE);

//                        Toast.makeText(
//
//                                SmsVerification_RegisterActivity.this
//                                , "خطای اتصال",
//                                Toast.LENGTH_SHORT).show();

                        Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_SHORT);
                    }
                });
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                loading.smoothToShow();

                send.send_verif_code(getIntent().getExtras().getString("phone_number"), Utility.arabicToDecimal(code.getText().toString())).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

                            loading.smoothToHide();

//                            Toast.makeText(
//                                    SmsVerification_RegisterActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                            login.send_info(getIntent().getStringExtra("phone_number"), getIntent().getStringExtra("password"), "password").enqueue(new Callback<AccessToken>() {
                                @Override
                                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                                    if (response.body().getAccessToken()!=null && response.body().getAccessToken()!="") {

                                        SharedPreferences settings = getApplicationContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString(TOKEN, response.body().getAccessToken()).apply();

//                                        Toast.makeText(
//                                                SmsVerification_RegisterActivity.this
//                                                , "ورود موفق!",
//                                                Toast.LENGTH_SHORT).show();

                                        Utility.showSnackbar(layout, R.string.success_login, Snackbar.LENGTH_SHORT);

                                        Intent goto_mainpage = new Intent(SmsVerification_RegisterActivity.this,
                                                MainActivity.class);
                                        goto_mainpage.putExtra(GET_PROFILE_FROM_SERVER, true);
                                        goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        finish();
                                        if (openMainActivity) {
                                            startActivity(goto_mainpage);
                                        }
                                    } else {

//                                        Toast.makeText(
//                                                SmsVerification_RegisterActivity.this
//                                                , "نام کاربری یا رمز عبور نامعتبر!",
//                                                Toast.LENGTH_SHORT).show();

                                        Utility.showSnackbar(layout, "خطای ورود به حساب کاربری", Snackbar.LENGTH_SHORT);
                                    }


                                }

                                @Override
                                public void onFailure(Call<AccessToken> call, Throwable t) {
                                    // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
//                                    Toast.makeText(
//                                            SmsVerification_RegisterActivity.this
//                                            , "خطا!",
//                                            Toast.LENGTH_SHORT).show();

                                    Utility.showSnackbar(layout, R.string.error, Snackbar.LENGTH_SHORT);

                                }
                            });

                        } else {

                            loading.smoothToHide();
//                            Toast.makeText(
//                                    SmsVerification_RegisterActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);
                        }

//                        Intent code_transfer = new Intent(getBaseContext(), sms_validation.class);
//                        code_transfer.putExtra("validation_code", code);
//                        startActivity(code_transfer);

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        loading.smoothToHide();

//                        Toast.makeText(
//                                SmsVerification_RegisterActivity.this
//                                , "خطای اتصال",
//                                Toast.LENGTH_SHORT).show();

                        Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_SHORT);

                    }
                });

            }
        });

    }

    public void timer() {

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //this will be done every 1000 milliseconds ( 1 seconds )

                countdown_timer.setText("00:" + millisUntilFinished / 1000);

                long progress = (60000 - millisUntilFinished) / 1000;
                progress_bar.setProgress((int) progress);
            }

            @Override
            public void onFinish() {
                //the progressBar will be invisible after 60 000 miliseconds ( 1 minute)

                countdown_timer.setText("00:00");

                progress_bar.setProgress(60);

                resend.animate().alpha(1.0f);
                resend.setVisibility(View.VISIBLE);

            }

        }.start();

    }

    private SMSBroadcastReceiver smsBroadcastReceiver;

    public void smsReceiver() {

        Log.d("smsReceiver","start smsReceiver");
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent

                //Utility.showSnackbar(layout, R.string.error, Snackbar.LENGTH_SHORT);


                // Add this inside your class
                SMSBroadcastReceiver broadcastReceiver = new SMSBroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("smsReceiver","start SMSBroadcastReceiver");
                        Bundle b = intent.getExtras();

                        String message = b.getString("message");
                        Log.d("smsReceiver",message);
                        Utility.showSnackbar(layout, message, Snackbar.LENGTH_SHORT);
                    }
                };
                registerReceiver(broadcastReceiver, new IntentFilter("SMSBroadcastReceiver"));

            }

        }) ;

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("smsReceiver",e.getMessage());
                // Failed to start retriever, inspect Exception for more details
                // ...
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
    }
}

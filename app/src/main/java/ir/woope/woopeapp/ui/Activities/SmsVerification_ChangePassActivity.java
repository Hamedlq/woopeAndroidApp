package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.chaos.view.PinView;
import com.wang.avi.AVLoadingIndicatorView;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.LoginInterface;
import ir.woope.woopeapp.interfaces.RegisterInterface;
import ir.woope.woopeapp.models.AccessToken;
import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SmsVerification_ChangePassActivity extends AppCompatActivity {



    Retrofit retrofit_sms_verif, retrofit_login;

    Toolbar toolbar;

    ProgressBar progress_bar;

    PinView code;

    TextView countdown_timer;

    MaterialRippleLayout accept;
    TextView resend;

    AVLoadingIndicatorView loading;

    String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sms_validation_changepass);

        toolbar = (Toolbar) findViewById(R.id.sms_validation_changepass_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countdown_timer = (TextView) findViewById(R.id.txt_countdown_changepass);
        final String recieved_code = getIntent().getStringExtra("validation_code");
        phoneNumber=getIntent().getExtras().getString("phonenumber");
        code = findViewById(R.id.pinView_smsValidation_changepass);
        progress_bar = (ProgressBar) findViewById(R.id.sms_validation_changepass_progressBar);

        loading = findViewById(R.id.progressBar_sms_validation_changepass);

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

        accept = findViewById(R.id.btn_accept_code_changepass);
        resend = findViewById(R.id.btn_resendcode_changepass);

        timer();

        resend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {



                send.send_code(phoneNumber).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

//                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    SmsVerification_ChangePassActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            accept.setVisibility(View.VISIBLE);
                            resend.animate().alpha(0.0f);
                            resend.setVisibility(View.INVISIBLE);
                            timer();

                        } else {

//                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    SmsVerification_ChangePassActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

//                        progress.setVisibility(View.GONE);

                        Toast.makeText(

                                SmsVerification_ChangePassActivity.this
                                , "خطای اتصال",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        accept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent goto_newpass = new Intent(SmsVerification_ChangePassActivity.this,
                                               NewPass_ChangePassActivity.class);

                goto_newpass.putExtra("confirmcode", code.getText().toString());
                goto_newpass.putExtra("phonenumber",phoneNumber );

                startActivity(goto_newpass);


//                send.send_verif_code(getIntent().getExtras().getString("phone_number"),Utility.arabicToDecimal(code.getText().toString())).enqueue(new Callback<ApiResponse>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//
//                        if (response.body().getStatus() == 101) {
//
//                            loading.smoothToHide();
//
//                            Toast.makeText(
//                                    SmsVerification_ChangePassActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//
//                            login.send_info(getIntent().getStringExtra("phone_number"), getIntent().getStringExtra("password"), "password").enqueue(new Callback<AccessToken>() {
//                                @Override
//                                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
//
//                                    if (response.message().toString().equals("OK")) {
//
//                                        SharedPreferences settings = getApplicationContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
//                                        SharedPreferences.Editor editor = settings.edit();
//                                        editor.putString(TOKEN, response.body().getAccessToken()).apply();
//
//                                        Toast.makeText(
//                                                SmsVerification_ChangePassActivity.this
//                                                , "ورود موفق!",
//                                                Toast.LENGTH_SHORT).show();
//                                        Intent goto_mainpage = new Intent(SmsVerification_ChangePassActivity.this,
//                                                MainActivity.class);
//                                        goto_mainpage.putExtra(GET_PROFILE_FROM_SERVER, true);
//                                        goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        finish();
//                                        startActivity(goto_mainpage);
//                                    } else {
//                                        Toast.makeText(
//                                                SmsVerification_ChangePassActivity.this
//                                                , "نام کاربری یا رمز عبور نامعتبر!",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//
//
//                                }
//
//                                @Override
//                                public void onFailure(Call<AccessToken> call, Throwable t) {
//                                    // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
//                                    Toast.makeText(
//                                            SmsVerification_ChangePassActivity.this
//                                            , "خطا!",
//                                            Toast.LENGTH_SHORT).show();
//
//
//                                }
//                            });
//
//                        } else {
//
//                            loading.smoothToHide();
//                            Toast.makeText(
//                                    SmsVerification_ChangePassActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
////                        Intent code_transfer = new Intent(getBaseContext(), sms_validation.class);
////                        code_transfer.putExtra("validation_code", code);
////                        startActivity(code_transfer);
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse> call, Throwable t) {
//
//                        loading.smoothToHide();
//
//                        Toast.makeText(
//                                SmsVerification_ChangePassActivity.this
//                                , "خطای اتصال",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });

            }
        });

    }

    public void timer(){

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //this will be done every 1000 milliseconds ( 1 seconds )

                countdown_timer.setText("00:"+millisUntilFinished / 1000);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            Intent goto_select = new Intent(SmsVerification_ChangePassActivity.this,
                    SendNumber_ChangePassActivity.class);
            {
                startActivity(goto_select);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}

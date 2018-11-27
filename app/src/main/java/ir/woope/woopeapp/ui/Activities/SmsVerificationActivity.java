package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SmsVerificationActivity extends AppCompatActivity {

    Retrofit retrofit_sms_verif, retrofit_login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sms_validation_register);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
//        }

        final TextView countdown_timer = (TextView) findViewById(R.id.txt_countdown_changepass);
        final String recieved_code = getIntent().getStringExtra("validation_code");
        final EditText code = (EditText) findViewById(R.id.txtbx_confirm_code_register);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_sms_verif_register);

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

        final Button accept = (Button) findViewById(R.id.btn_accept_code_register);
        final Button resend = (Button) findViewById(R.id.btn_resendcode_register);

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                countdown_timer.setText(getText(R.string.remaining_time) + " : " + millisUntilFinished / 1000);

            }

            @Override
            public void onFinish() {
                countdown_timer.setText("");
                if (code.equals("")) {

                    countdown_timer.setText("زمان شما به پایان رسید");

//                    Toast.makeText(
//                            sms_validation.this
//                            , "زمان شما به پایان رسید!",
//                            Toast.LENGTH_SHORT).show();

                    finish();
                }
                accept.setVisibility(View.GONE);
                resend.setVisibility(View.VISIBLE);
            }
        }.start();

        resend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                accept.setVisibility(View.VISIBLE);
                resend.setVisibility(View.GONE);

                send.send_code(getIntent().getExtras().getString("phone_number")).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    SmsVerificationActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    SmsVerificationActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        progress.setVisibility(View.GONE);

                        Toast.makeText(

                                SmsVerificationActivity.this
                                , "خطای اتصال",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                progress.setVisibility(View.VISIBLE);

                send.send_verif_code(getIntent().getExtras().getString("phone_number"),Utility.arabicToDecimal(code.getText().toString())).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    SmsVerificationActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            login.send_info(getIntent().getStringExtra("phone_number"), getIntent().getStringExtra("password"), "password").enqueue(new Callback<AccessToken>() {
                                @Override
                                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                                    if (response.message().toString().equals("OK")) {

                                        SharedPreferences settings = getApplicationContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString(TOKEN, response.body().getAccessToken()).apply();

                                        Toast.makeText(
                                                SmsVerificationActivity.this
                                                , "ورود موفق!",
                                                Toast.LENGTH_SHORT).show();
                                        Intent goto_mainpage = new Intent(SmsVerificationActivity.this,
                                                MainActivity.class);
                                        goto_mainpage.putExtra(GET_PROFILE_FROM_SERVER, true);
                                        goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                        startActivity(goto_mainpage);
                                    } else {
                                        Toast.makeText(
                                                SmsVerificationActivity.this
                                                , "نام کاربری یا رمز عبور نامعتبر!",
                                                Toast.LENGTH_SHORT).show();
                                    }


                                }

                                @Override
                                public void onFailure(Call<AccessToken> call, Throwable t) {
                                    // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
                                    Toast.makeText(
                                            SmsVerificationActivity.this
                                            , "خطا!",
                                            Toast.LENGTH_SHORT).show();


                                }
                            });

                        } else {

                            progress.setVisibility(View.GONE);
                            Toast.makeText(
                                    SmsVerificationActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

//                        Intent code_transfer = new Intent(getBaseContext(), sms_validation.class);
//                        code_transfer.putExtra("validation_code", code);
//                        startActivity(code_transfer);

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        progress.setVisibility(View.GONE);

                        Toast.makeText(
                                SmsVerificationActivity.this
                                , "خطای اتصال",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


}

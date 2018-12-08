package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.LoginInterface;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.RegisterInterface;
import ir.woope.woopeapp.models.AccessToken;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class VerifyPhoneActivity extends AppCompatActivity {

    Retrofit retrofit_getProfile, retrofit_sendConfirmCode;

    String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sms_validation_register);

        Intent intent = getIntent();
        final String token = intent.getStringExtra(TOKEN);

        final TextView countdown_timer = (TextView) findViewById(R.id.txt_countdown_changepass);
//        final EditText code = (EditText) findViewById(R.id.txtbx_confirm_code_register);
//        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_sms_verif_register);

        retrofit_getProfile = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ProfileInterface getInfo = retrofit_getProfile.create(ProfileInterface.class);

        retrofit_sendConfirmCode = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final RegisterInterface send = retrofit_sendConfirmCode.create(RegisterInterface.class);

        final Button accept = (Button) findViewById(R.id.btn_accept_code_register);
//        final Button resend = (Button) findViewById(R.id.btn_resendcode_register);

//        final CountDownTimer ch = new CountDownTimer(60000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                countdown_timer.setText(getText(R.string.remaining_time) + " : " + millisUntilFinished / 1000);
//
//            }
//
//            @Override
//            public void onFinish() {
//                countdown_timer.setText("");
//                if (code.equals("")) {
//
//                    countdown_timer.setText("زمان شما به پایان رسید");
//
////                    Toast.makeText(
////                            sms_validation.this
////                            , "زمان شما به پایان رسید!",
////                            Toast.LENGTH_SHORT).show();
//
//                    finish();
//                }
//                accept.setVisibility(View.GONE);
//                resend.setVisibility(View.VISIBLE);
//            }
//        }.start();
//
//        getInfo.getProfileFromServer("bearer " + token).enqueue(new Callback<Profile>() {
//            @Override
//            public void onResponse(Call<Profile> call, Response<Profile> response) {
//
//                if (response.code() == 200) {
//
//                    phoneNumber = response.body().getMobile();
//
//                    send.send_code(phoneNumber).enqueue(new Callback<ApiResponse>() {
//                        @Override
//                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//
//                            if (response.body().getStatus() == 101) {
//
//
//                                Toast.makeText(
//                                        VerifyPhoneActivity.this
//                                        , response.body().getMessage(),
//                                        Toast.LENGTH_SHORT).show();
//
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<ApiResponse> call, Throwable t) {
//
//                            Toast.makeText(
//                                    VerifyPhoneActivity.this
//                                    , "خطای اتصال",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                } else {
//                    Toast.makeText(
//
//                            VerifyPhoneActivity.this
//                            , "خطا",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Profile> call, Throwable t) {
//
//                progress.setVisibility(View.GONE);
//
//                Toast.makeText(
//
//                        VerifyPhoneActivity.this
//                        , "خطای اتصال",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//        resend.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View arg0) {
//
//                resend.setVisibility(View.GONE);
//                progress.setVisibility(View.VISIBLE);
//
//                send.send_code(phoneNumber).enqueue(new Callback<ApiResponse>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//
//                        if (response.body().getStatus() == 101) {
//
//                            ch.start();
//
//                            progress.setVisibility(View.GONE);
//                            resend.setVisibility(View.GONE);
//                            accept.setVisibility(View.VISIBLE);
//                            Toast.makeText(
//                                    VerifyPhoneActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//
//
//                        } else {
//
//                            resend.setVisibility(View.VISIBLE);
//                            progress.setVisibility(View.GONE);
//                            Toast.makeText(
//                                    VerifyPhoneActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse> call, Throwable t) {
//
//                        resend.setVisibility(View.VISIBLE);
//                        progress.setVisibility(View.GONE);
//
//                        Toast.makeText(
//                                VerifyPhoneActivity.this
//                                , "خطای اتصال",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        });
//
//
//        accept.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View arg0) {
//
//                progress.setVisibility(View.VISIBLE);
//                accept.setVisibility(View.GONE);
//
//                send.send_verif_code(phoneNumber, Utility.arabicToDecimal(code.getText().toString())).enqueue(new Callback<ApiResponse>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//
//                        if (response.body().getStatus() == 101) {
//
//                            SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putString(TOKEN, token);
//                            editor.apply();
//
//                            progress.setVisibility(View.GONE);
//
//                            Toast.makeText(
//                                    VerifyPhoneActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//
//                            Intent goto_mainpage = new Intent(VerifyPhoneActivity.this,
//                                    MainActivity.class);
//                            goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            finish();
//                            startActivity(goto_mainpage);
//
//
//                        } else {
//
//                            accept.setVisibility(View.VISIBLE);
//                            progress.setVisibility(View.GONE);
//
//                            Toast.makeText(
//                                    VerifyPhoneActivity.this
//                                    , response.body().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse> call, Throwable t) {
//                        // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
//
//                        accept.setVisibility(View.VISIBLE);
//                        progress.setVisibility(View.GONE);
//
//                        Toast.makeText(
//                                VerifyPhoneActivity.this
//                                , "خطا اتصال!",
//                                Toast.LENGTH_SHORT).show();
//
//
//                    }
//
//                });
//
//            }
//
//        });

    }

}

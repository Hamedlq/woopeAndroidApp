package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.LoginInterface;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.models.AccessToken;
import ir.woope.woopeapp.models.Profile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class LoginActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    Animation rotateonceAnim;
    ImageView wplogologin;

    private void rotationAnimation() {

        rotateonceAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_once);
        wplogologin.startAnimation(rotateonceAnim);

    }

    Retrofit retrofit_login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_login);


        final EditText username = (EditText) findViewById(R.id.txtbx_userphone_login);
        final EditText password = (EditText) findViewById(R.id.txtbx_password_login);

        View usr= findViewById(R.id.txtbx_userphone_login);
        View pass = findViewById(R.id.txtbx_password_login);



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
//        }

        //sets the statusbar and navbar colors

        wplogologin = findViewById(R.id.imgbx_logo_login);
        /*final MediaPlayer mp = new MediaPlayer();
        wplogologin.setSoundEffectsEnabled(false);
        wplogologin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                rotationAnimation();

                if(mp.isPlaying())
                {
                    mp.stop();
                }

                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = getAssets().openFd("coins.wav");
                    mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });*/

        TextView forgetpass = (TextView) findViewById(R.id.txt_forget_pass);

        forgetpass.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_forgetpass = new Intent(LoginActivity.this,
                        SendNumber_ChangePassActivity.class);
                startActivity(goto_forgetpass);
            }
        });

        Button register = (Button) findViewById(R.id.btn_register);

        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_userinfo = new Intent(LoginActivity.this,
                        UserRegisterActivity.class);
                {
                    startActivity(goto_userinfo);
                }
            }
        });

        final Button enter = (Button) findViewById(R.id.btn_enter_login);

        retrofit_login = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofit_getProf = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final LoginInterface login = retrofit_login.create(LoginInterface.class);
        final ProfileInterface getProfile = retrofit_getProf.create(ProfileInterface.class);
        final ProgressBar enterprogress = (ProgressBar) findViewById(R.id.enter_progressbar_login);

        enter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                enter.setVisibility(View.GONE);
                enterprogress.setVisibility(View.VISIBLE);

                login.send_info(username.getText().toString(), Utility.arabicToDecimal(password.getText().toString()), "password").enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                        if (response.code() == 200) {

                            enterprogress.setVisibility(View.GONE);
                            enter.setVisibility(View.VISIBLE);

                            final String tk = response.body().getAccessToken();

                            getProfile.getProfileFromServer("bearer " + tk).enqueue(new Callback<Profile>() {
                                @Override
                                public void onResponse(Call<Profile> call, Response<Profile> response) {

                                    if (response.code() == 200) {

                                        if (response.body().getConfirmed() == false) {

                                            Toast.makeText(
                                                    LoginActivity.this
                                                    , "شماره موبایل خود را تایید کنید",
                                                    Toast.LENGTH_SHORT).show();

                                            Intent goto_verifphone = new Intent(LoginActivity.this,
                                                    VerifyPhoneActivity.class);
                                            goto_verifphone.putExtra(TOKEN,tk);
                                            startActivity(goto_verifphone);

                                        } else {

                                            SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString(TOKEN, tk);
                                            editor.apply();

                                            Toast.makeText(
                                                    LoginActivity.this
                                                    , "ورود موفق!",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent goto_mainpage = new Intent(LoginActivity.this,
                                                    MainActivity.class);
                                            goto_mainpage.putExtra(GET_PROFILE_FROM_SERVER, true);
                                            goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            finish();
                                            startActivity(goto_mainpage);

                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<Profile> call, Throwable t) {

                                    Toast.makeText(
                                            LoginActivity.this
                                            , "خطای اتصال!",
                                            Toast.LENGTH_SHORT).show();

                                    enter.setVisibility(View.VISIBLE);
                                    enterprogress.setVisibility(View.GONE);
                                }
                            });


                        } else {

                            enterprogress.setVisibility(View.GONE);
                            enter.setVisibility(View.VISIBLE);

                            Toast.makeText(
                                    LoginActivity.this
                                    , "نام کاربری یا رمز عبور نامعتبر!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
                        Toast.makeText(
                                LoginActivity.this
                                , "خطا!",
                                Toast.LENGTH_SHORT).show();

                        enter.setVisibility(View.VISIBLE);
                        enterprogress.setVisibility(View.GONE);
                    }
                });


            }


        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج کلید بازگشت را فشار دهید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}

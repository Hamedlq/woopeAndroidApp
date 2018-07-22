package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoginActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;

    Animation rotateonceAnim;
    ImageView wplogologin;



    private void rotationAnimation() {

        rotateonceAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_once);
        wplogologin.startAnimation(rotateonceAnim);

    }

    public interface login_api {

        @POST("/connect/token")
        @FormUrlEncoded
        Call<AccessToken> send_info(@Field("username") String userphone, @Field("password") String password, @Field("grant_type") String grant_type);
        // Call<result> token(@Field("access_token") String token);

    }

    Retrofit retrofit_login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_login);

        final EditText username = (EditText) findViewById(R.id.txtbx_userphone_login);
        final EditText password = (EditText) findViewById(R.id.txtbx_password_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
        }

        //sets the statusbar and navbar colors

        wplogologin = findViewById(R.id.imgbx_logo_login);
        final MediaPlayer mp = new MediaPlayer();
        wplogologin.setSoundEffectsEnabled(false);
        wplogologin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                rotationAnimation();
                //arg0.playSoundEffect(android.view.SoundEffectConstants.CLICK);

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
        });

        TextView forgetpass = (TextView) findViewById(R.id.txt_forget_pass);

        forgetpass.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_forgetpass = new Intent(LoginActivity.this,
                        ForgotPasswordActivity.class);
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

        Button enter = (Button) findViewById(R.id.btn_enter_login);

        retrofit_login = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final login_api login = retrofit_login.create(login_api.class);

        enter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                login.send_info(username.getText().toString(),password.getText().toString(), "password").enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                        if(response.message().toString().equals("OK"))
                        {

                            response.body(); // have your all data
                            String tk =response.body().getAccessToken();

                            SharedPreferences settings = getApplicationContext().getSharedPreferences("token", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("token", tk);
                            // Apply the edits!
                            editor.apply();

                            Toast.makeText(
                                    LoginActivity.this
                                    , "ورود موفق!",
                                    Toast.LENGTH_SHORT).show();
                            Intent goto_mainpage = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(goto_mainpage);
                            {

                                goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(goto_mainpage);

                            }
                        }
                        else{
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}

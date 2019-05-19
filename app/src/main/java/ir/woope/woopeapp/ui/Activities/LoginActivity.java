package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wang.avi.AVLoadingIndicatorView;

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

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.OPEN_MAIN_ACTIVITY;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class LoginActivity extends AppCompatActivity {

    ImageView wplogologin;
    Toolbar toolbar;

    Retrofit retrofit_login;

    MaterialRippleLayout enter;
    Boolean openMainActivity=true;
    View layout;
    AVLoadingIndicatorView enterprogress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml

        setContentView(R.layout.activity_login);

        if (getIntent() != null && getIntent().getExtras() != null) {
            openMainActivity =  getIntent().getExtras().getBoolean(OPEN_MAIN_ACTIVITY);
        }
        layout = findViewById(R.id.activity_login);

        final EditText username = (EditText) findViewById(R.id.txtbx_userphone_login);
        final EditText password = (EditText) findViewById(R.id.txtbx_password_login);

        View usr = findViewById(R.id.txtbx_userphone_login);
        View pass = findViewById(R.id.txtbx_password_login);

        wplogologin = findViewById(R.id.imgbx_logo_login);

        TextView forgetpass = (TextView) findViewById(R.id.txt_forget_pass);

        forgetpass.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_forgetpass = new Intent(LoginActivity.this,
                        SendNumber_ChangePassActivity.class);
                startActivity(goto_forgetpass);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        enter = findViewById(R.id.btn_enter_login);

        retrofit_login = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        final LoginInterface login = retrofit_login.create(LoginInterface.class);
        enterprogress = findViewById(R.id.progressBar_login);

        enter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                enter.setVisibility(View.GONE);
                enterprogress.smoothToShow();

                login.send_info(username.getText().toString(), Utility.arabicToDecimal(password.getText().toString()), "password").enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                        if (response.code() == 200) {

                            enterprogress.smoothToHide();
                            enter.setVisibility(View.VISIBLE);

                            final String tk = response.body().getAccessToken();

                            getProfile(tk);


                                        if (response.body().getConfirmed() == false) {

//                                            Toast.makeText(
//                                                    LoginActivity.this
//                                                    , getResources().getString(R.string.confirmPhoneNumber),
//                                                    Toast.LENGTH_SHORT).show();

                                            Utility.showSnackbar(layout, R.string.confirmPhoneNumber, Snackbar.LENGTH_LONG);

                                            Intent goto_verifphone = new Intent(LoginActivity.this,
                                                    VerifyPhoneActivity.class);
                                            goto_verifphone.putExtra(TOKEN, tk);
                                            startActivity(goto_verifphone);

                                        } else {
                                            SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString(TOKEN, tk);
                                            editor.apply();
//                                            Toast.makeText(
//                                                    LoginActivity.this
//                                                    , "ورود موفق!",
//                                                    Toast.LENGTH_SHORT).show();
                                            Intent goto_mainpage = new Intent(LoginActivity.this,
                                                    MainActivity.class);
                                            goto_mainpage.putExtra(GET_PROFILE_FROM_SERVER, true);
                                            goto_mainpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                            if(openMainActivity){
                                                startActivity(goto_mainpage);
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<Profile> call, Throwable t) {

//                                    Toast.makeText(
//                                            LoginActivity.this
//                                            , "خطای اتصال!",
//                                            Toast.LENGTH_SHORT).show();

                                    Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                                    enter.setVisibility(View.VISIBLE);
                                    enterprogress.smoothToHide();
                                }
                            });


                        } else {

                            enterprogress.smoothToHide();
                            enter.setVisibility(View.VISIBLE);

//                            Toast.makeText(
//                                    LoginActivity.this
//                                    , "نام کاربری یا رمز عبور نامعتبر!",
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, R.string.invalidUsernameOrPassword, Snackbar.LENGTH_LONG);
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
//                        Toast.makeText(
//                                LoginActivity.this
//                                , "خطا!",
//                                Toast.LENGTH_SHORT).show();
                        Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                        enter.setVisibility(View.VISIBLE);
                        enterprogress.smoothToHide();
                    }
                });


            }


        });
    }

    private void getProfile(final String token){
        Retrofit retrofit_getProf = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProfileInterface getProfile = retrofit_getProf.create(ProfileInterface.class);
        getProfile.getProfileFromServer("bearer " + token).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                if (response.code() == 200) {

                    if (response.body().getConfirmed() == false) {

//                                            Toast.makeText(
//                                                    LoginActivity.this
//                                                    , getResources().getString(R.string.confirmPhoneNumber),
//                                                    Toast.LENGTH_SHORT).show();

                        Utility.showSnackbar(layout, R.string.confirmPhoneNumber, Snackbar.LENGTH_LONG);

                        Intent goto_verifphone = new Intent(LoginActivity.this,
                                VerifyPhoneActivity.class);
                        goto_verifphone.putExtra(TOKEN, token);
                        startActivity(goto_verifphone);

                    } else {

                        SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(TOKEN, token);
                        editor.apply();

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

//                                    Toast.makeText(
//                                            LoginActivity.this
//                                            , "خطای اتصال!",
//                                            Toast.LENGTH_SHORT).show();

                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                enter.setVisibility(View.VISIBLE);
                enterprogress.smoothToHide();
            }
        });
    }

    @Override
    public void onBackPressed() {
        /*Intent goto_select = new Intent(LoginActivity.this,
                SplashSelectActivity.class);
        {
            startActivity(goto_select);
            finish();
        }*/
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            /*Intent goto_select = new Intent(LoginActivity.this,
                    SplashSelectActivity.class);
            {
                startActivity(goto_select);
                finish();
            }*/
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

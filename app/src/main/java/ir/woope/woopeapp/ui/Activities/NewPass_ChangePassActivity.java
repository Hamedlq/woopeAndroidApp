package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wang.avi.AVLoadingIndicatorView;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ChangePassInterface;
import ir.woope.woopeapp.interfaces.LoginInterface;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.models.AccessToken;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class NewPass_ChangePassActivity extends AppCompatActivity {

    MaterialRippleLayout accept;
    AVLoadingIndicatorView progress;
    EditText newpass;
    String code, phonenumber;

    Retrofit retrofit_changepass;

    Toolbar toolbar;
    View layout;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_newpass_changepass);

        layout = findViewById(R.id.activity_newpass_changepass);

        toolbar = (Toolbar) findViewById(R.id.newpass_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        newpass = findViewById(R.id.txtbx_newpass_changepass);
        code = getIntent().getExtras().getString("confirmcode");
        phonenumber = getIntent().getExtras().getString("phonenumber");
        progress = findViewById(R.id.progressBar_newpass_changepass);
        accept = findViewById(R.id.btn_accept_newpass_changepass);

        retrofit_changepass = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ChangePassInterface changepass = retrofit_changepass.create(ChangePassInterface.class);

        accept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                if (newpass.getText().length() >= 4)

                {

                    progress.smoothToShow();
                    accept.setVisibility(View.GONE);

                    changepass.change_pass(phonenumber, newpass.getText().toString(), code).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            if (response.body().getStatus() == 101) {

                                progress.smoothToHide();

//                                Toast.makeText(
//                                        NewPass_ChangePassActivity.this
//                                        , response.body().getMessage(),
//                                        Toast.LENGTH_SHORT).show();

                                Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                                login(phonenumber, newpass.getText().toString());

                            } else {

                                progress.smoothToHide();
                                accept.setVisibility(View.VISIBLE);

//                                Toast.makeText(
//                                        NewPass_ChangePassActivity.this
//                                        , response.body().getMessage(),
//                                        Toast.LENGTH_SHORT).show();

                                Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                                finish();

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            progress.smoothToHide();
                            accept.setVisibility(View.VISIBLE);

//                            Toast.makeText(
//                                    NewPass_ChangePassActivity.this
//                                    , "خطای اتصال",
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_SHORT);
                        }
                    });
                } else if (newpass.getText().length() < 4)
//                    Toast.makeText(
//                            NewPass_ChangePassActivity.this
//                            , "رمز عبور باید 4 رقم یا بیشتر باشد",
//                            Toast.LENGTH_SHORT).show();
                    Utility.showSnackbar(layout, R.string.passwordLenghtNotEnough, Snackbar.LENGTH_SHORT);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            Intent goto_select = new Intent(NewPass_ChangePassActivity.this,
                    SmsVerification_ChangePassActivity.class);
            {
                startActivity(goto_select);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void login(String username, String password)

    {

        Retrofit retrofit_login;

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

        login.send_info(username, Utility.arabicToDecimal(password), "password").enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                if (response.code() == 200) {


                    final String tk = response.body().getAccessToken();

                    getProfile.getProfileFromServer("bearer " + tk).enqueue(new Callback<Profile>() {
                        @Override
                        public void onResponse(Call<Profile> call, Response<Profile> response) {

                            if (response.code() == 200) {

                                if (response.body().getConfirmed() == false) {

//                                    Toast.makeText(
//                                            NewPass_ChangePassActivity.this
//                                            , "شماره موبایل خود را تایید کنید",
//                                            Toast.LENGTH_SHORT).show();

                                    Utility.showSnackbar(layout, R.string.confirmPhoneNumber, Snackbar.LENGTH_SHORT);

                                    Intent goto_verifphone = new Intent(NewPass_ChangePassActivity.this,
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
                                    Intent goto_mainpage = new Intent(NewPass_ChangePassActivity.this,
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

//                            Toast.makeText(
//                                    NewPass_ChangePassActivity.this
//                                    , "خطای اتصال!",
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_SHORT);

                        }
                    });


                } else {


//                    Toast.makeText(
//                            NewPass_ChangePassActivity.this
//                            , "نام کاربری یا رمز عبور نامعتبر!",
//                            Toast.LENGTH_SHORT).show();

                    Utility.showSnackbar(layout, R.string.invalidUsernameOrPassword, Snackbar.LENGTH_SHORT);

                }

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

//                Toast.makeText(
//                        NewPass_ChangePassActivity.this
//                        , "خطا!",
//                        Toast.LENGTH_SHORT).show();

                Utility.showSnackbar(layout, R.string.error, Snackbar.LENGTH_SHORT);

            }
        });

    }

}

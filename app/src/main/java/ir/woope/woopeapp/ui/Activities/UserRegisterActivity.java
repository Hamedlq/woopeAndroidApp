package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wang.avi.AVLoadingIndicatorView;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.RegisterInterface;
import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRegisterActivity extends AppCompatActivity {

    EditText username, password, phonenumber;
    TextInputLayout usernamelayout, passwordlayout, phonenumberlayout;
    AVLoadingIndicatorView progress;
    MaterialRippleLayout register;
    Retrofit retrofit_userregister;
    RegisterInterface reg;
    TextView txt_rules;
    Toolbar toolbar;

    View layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_user_register);

        layout = findViewById(R.id.activity_userRegister);

        toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = (EditText) findViewById(R.id.txtbx_username_register);
        password = (EditText) findViewById(R.id.txtbx_password_register);
        phonenumber = (EditText) findViewById(R.id.txtbx_phonenumber_register);
        txt_rules = (TextView) findViewById(R.id.txt_rules);

        progress = findViewById(R.id.progressBar_user_register);

        register = findViewById(R.id.btn_accept_register);

        retrofit_userregister = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reg = retrofit_userregister.create(RegisterInterface.class);

        txt_rules.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent goto_sms_validation = new Intent(UserRegisterActivity.this,
                        RulesActivity.class);
                startActivity(goto_sms_validation);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                if (!isUserNameValid(username.getText().toString())) {

//                    Toast.makeText(
//                            UserRegisterActivity.this
//                            , "نام کاربری معتبر نیست",
//                            Toast.LENGTH_SHORT).show();

                    Utility.showSnackbar(layout, R.string.invalidUsername, Snackbar.LENGTH_SHORT);

                } else {
                    progress.smoothToShow();
                    register.setVisibility(View.GONE);

                    reg.send_info(username.getText().toString(), phonenumber.getText().toString(), Utility.arabicToDecimal(password.getText().toString())).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            if (response.body().getStatus() == 101) {

                                progress.smoothToHide();

//                                Toast.makeText(
//                                        UserRegisterActivity.this
//                                        , response.body().getMessage(),
//                                        Toast.LENGTH_SHORT).show();

                                Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);


                                final Intent goto_sms_validation = new Intent(UserRegisterActivity.this,
                                        SmsVerification_RegisterActivity.class);

                                goto_sms_validation.putExtra("phone_number", phonenumber.getText().toString());
                                goto_sms_validation.putExtra("username", username.getText().toString());
                                goto_sms_validation.putExtra("password", password.getText().toString());

                                reg.send_code(phonenumber.getText().toString()).enqueue(new Callback<ApiResponse>() {
                                    @Override
                                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                                        if (response.body().status==101) {

                                            progress.smoothToHide();

//                                            Toast.makeText(
//                                                    UserRegisterActivity.this
//                                                    , response.body().getMessage(),
//                                                    Toast.LENGTH_SHORT).show();

                                            Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                                            startActivity(goto_sms_validation);

                                        } else {

                                            progress.smoothToHide();
                                            register.setVisibility(View.VISIBLE);

//                                            Toast.makeText(
//                                                    UserRegisterActivity.this
//                                                    , response.body().getMessage(),
//                                                    Toast.LENGTH_SHORT).show();

                                            Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);

                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                                        progress.smoothToHide();
                                        register.setVisibility(View.VISIBLE);
//                                        Toast.makeText(
//                                                UserRegisterActivity.this
//                                                , "خطای اتصال",
//                                                Toast.LENGTH_SHORT).show();

                                        Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_SHORT);

                                    }
                                });


                            } else {

                                progress.smoothToHide();
                                register.setVisibility(View.VISIBLE);

                                if (username.getText().toString() == "") {
                                    usernamelayout.setError("نام کاربری را وارد کنید");
                                }
                                if (phonenumber.getText().toString() == "") {
                                    phonenumberlayout.setError("شماره موبایل را وارد کنید");
                                }
                                if (password.getText().toString() == "") {
                                    passwordlayout.setError("رمز عبور را وارد کنید");
                                }


//                                Toast.makeText(
//                                        UserRegisterActivity.this
//                                        , response.body().getMessage(),
//                                        Toast.LENGTH_SHORT).show();

                                Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_SHORT);


                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            progress.smoothToHide();
                            register.setVisibility(View.VISIBLE);

//                            Toast.makeText(
//                                    UserRegisterActivity.this
//                                    , "خطای اتصال",
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_SHORT);

                        }
                    });


                }
            }

        });


    }

    @Override
    public void onBackPressed() {
        Intent goto_select = new Intent(UserRegisterActivity.this,
                SplashSelectActivity.class);
        {
            startActivity(goto_select);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent goto_select = new Intent(UserRegisterActivity.this,
                    SplashSelectActivity.class);
            {
                startActivity(goto_select);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isUserNameValid(String text) {

        return text.matches("^([A-Za-z0-9]+)([A-Za-z]+)*?$");
    }

}

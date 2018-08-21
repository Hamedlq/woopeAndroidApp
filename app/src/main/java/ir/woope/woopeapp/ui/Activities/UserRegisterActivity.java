package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.w3c.dom.Text;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
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
    ProgressBar progress;
    Button register;
    Retrofit retrofit_userregister;
    RegisterInterface reg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_user_register);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
//
//        }

        username = (EditText) findViewById(R.id.txtbx_username_register);
        password = (EditText) findViewById(R.id.txtbx_password_register);
        phonenumber = (EditText) findViewById(R.id.txtbx_phonenumber_register);

        usernamelayout = (TextInputLayout) findViewById(R.id.usernamelayout_reg);
        passwordlayout = (TextInputLayout) findViewById(R.id.passwordlayout_reg);
        phonenumberlayout = (TextInputLayout) findViewById(R.id.phonenumberlayout_reg);

        progress = (ProgressBar) findViewById(R.id.progressBar_userinfo);

        register = (Button) findViewById(R.id.btn_accept_register);

        retrofit_userregister = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        reg = retrofit_userregister.create(RegisterInterface.class);

        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                progress.setVisibility(View.VISIBLE);
                register.setVisibility(View.GONE);

                reg.send_info(username.getText().toString(), phonenumber.getText().toString(), password.getText().toString()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    UserRegisterActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            final Intent goto_sms_validation = new Intent(UserRegisterActivity.this,
                                    SmsVerificationActivity.class);

                            goto_sms_validation.putExtra("phone_number", phonenumber.getText().toString());
                            goto_sms_validation.putExtra("username", username.getText().toString());
                            goto_sms_validation.putExtra("password", password.getText().toString());

                            reg.send_code(phonenumber.getText().toString()).enqueue(new Callback<ApiResponse>() {
                                @Override
                                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                                    if (response.message().toString().equals("OK")) {

                                        progress.setVisibility(View.GONE);

                                        Toast.makeText(
                                                UserRegisterActivity.this
                                                , response.body().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(goto_sms_validation);

                                    } else {

                                        progress.setVisibility(View.GONE);
                                        register.setVisibility(View.VISIBLE);

                                        Toast.makeText(
                                                UserRegisterActivity.this
                                                , response.body().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }

                                @Override
                                public void onFailure(Call<ApiResponse> call, Throwable t) {
                                    progress.setVisibility(View.GONE);
                                    register.setVisibility(View.VISIBLE);
                                    Toast.makeText(
                                            UserRegisterActivity.this
                                            , "خطای اتصال",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {

                            progress.setVisibility(View.GONE);
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


                            Toast.makeText(
                                    UserRegisterActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

//                        Intent code_transfer = new Intent(getBaseContext(), sms_validation.class);
//                        code_transfer.putExtra("validation_code", code);
//                        startActivity(code_transfer);


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                        progress.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);

                        Toast.makeText(
                                UserRegisterActivity.this
                                , "خطای اتصال",
                                Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });


    }


}

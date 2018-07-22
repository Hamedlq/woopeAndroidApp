package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;

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

    Retrofit retrofit_userregister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_user_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));

        }

        username = (EditText) findViewById(R.id.txtbx_username_register);
        password = (EditText) findViewById(R.id.txtbx_password_register);
        phonenumber = (EditText) findViewById(R.id.txtbx_phonenumber_register);

        Button register = (Button) findViewById(R.id.btn_accept_register);

        retrofit_userregister = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final RegisterInterface reg = retrofit_userregister.create(RegisterInterface.class);

        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                reg.send_info(username.getText().toString(), phonenumber.getText().toString(), password.getText().toString()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {
                            Toast.makeText(
                                    UserRegisterActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            Intent goto_sms_validation = new Intent(UserRegisterActivity.this,
                                    SmsVerificationActivity.class);
                            startActivity(goto_sms_validation);


                        } else {
                            for (String item : response.body().getErrors()) {

                                Toast.makeText(
                                        UserRegisterActivity.this
                                        , item,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

//                        Intent code_transfer = new Intent(getBaseContext(), sms_validation.class);
//                        code_transfer.putExtra("validation_code", code);
//                        startActivity(code_transfer);

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

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

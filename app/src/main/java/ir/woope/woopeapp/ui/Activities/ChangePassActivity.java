package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ChangePassInterface;
import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePassActivity extends AppCompatActivity {

    Retrofit retrofit_changepass;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_changepass);

        final EditText newpass = (EditText) findViewById(R.id.txtbx_newpass_changepass);
        final EditText code = (EditText) findViewById(R.id.txtbx_confirm_code_changepass);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_changepass);
        final Button accept = (Button) findViewById(R.id.btn_save_changepass);
        final TextView ct = (TextView) findViewById(R.id.txt_countdown_changepass);

        retrofit_changepass = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ChangePassInterface changepass = retrofit_changepass.create(ChangePassInterface.class);

        accept.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                progress.setVisibility(View.VISIBLE);
                accept.setVisibility(View.GONE);

                changepass.change_pass(getIntent().getExtras().getString("phonenumber"), newpass.getText().toString(), code.getText().toString()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    ChangePassActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            Intent goto_login = new Intent(ChangePassActivity.this,
                                    LoginActivity.class);
                            startActivity(goto_login);
                            goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(goto_login);

                        } else {

                            progress.setVisibility(View.GONE);
                            accept.setVisibility(View.VISIBLE);

                            Toast.makeText(
                                    ChangePassActivity.this
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
                        accept.setVisibility(View.VISIBLE);

                        Toast.makeText(
                                ChangePassActivity.this
                                , "خطای اتصال",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                ct.setText(getText(R.string.remaining_time) + " : " + millisUntilFinished / 1000);

            }

            @Override
            public void onFinish() {
                ct.setText("");
                if (code.equals("")) {

                    ct.setText("زمان شما به پایان رسید");

//                    Toast.makeText(
//                            sms_validation.this
//                            , "زمان شما به پایان رسید!",
//                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        }.start();

    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

}

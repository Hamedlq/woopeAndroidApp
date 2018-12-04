package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import ir.woope.woopeapp.interfaces.ChangePassInterface;
import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewPass_ChangePassActivity extends AppCompatActivity {

    MaterialRippleLayout accept;
    AVLoadingIndicatorView progress;
    EditText newpass;
    String code,phonenumber;

    Retrofit retrofit_changepass;

    Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_newpass_changepass);

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

                if(newpass.getText().length()>=4)

                {

                    progress.smoothToShow();
                    accept.setVisibility(View.GONE);

                    changepass.change_pass(phonenumber, newpass.getText().toString(), code).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            if (response.body().getStatus() == 101) {

                                progress.smoothToHide();

                                Toast.makeText(
                                        NewPass_ChangePassActivity.this
                                        , response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                Intent goto_login = new Intent(NewPass_ChangePassActivity.this,
                                        MainActivity.class);
                                goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finishAffinity();
                                startActivity(goto_login);

                            } else {

                                progress.smoothToHide();
                                accept.setVisibility(View.VISIBLE);

                                Toast.makeText(
                                        NewPass_ChangePassActivity.this
                                        , response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                finish();

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            progress.smoothToHide();
                            accept.setVisibility(View.VISIBLE);

                            Toast.makeText(
                                    NewPass_ChangePassActivity.this
                                    , "خطای اتصال",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else if(newpass.getText().length()<4)
                    Toast.makeText(
                            NewPass_ChangePassActivity.this
                            , "رمز عبور باید 4 رقم یا بیشتر باشد",
                            Toast.LENGTH_SHORT).show();
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


}

package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class SendNumber_ChangePassActivity extends AppCompatActivity {

    Retrofit retrofit_changepass;

    Toolbar toolbar;

    MaterialRippleLayout send;

    AVLoadingIndicatorView progress;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sendnumber_changepass);

        toolbar = (Toolbar) findViewById(R.id.sendnumber_changepass_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText number = (EditText) findViewById(R.id.txtbx_sendnumber_changepass);
        progress = findViewById(R.id.progressBar_sendnumber_changepass);
        send = findViewById(R.id.btn_accept_sendnumber_changepass);

        retrofit_changepass = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ChangePassInterface changepass = retrofit_changepass.create(ChangePassInterface.class);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                if (TextUtils.isEmpty(number.getText()))

                {
                    Toast.makeText(
                            SendNumber_ChangePassActivity.this
                            , "شماره موبایل خود را وارد کنید",
                            Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(number.getText()))  {

                    progress.smoothToShow();
                    send.setVisibility(View.GONE);

                    changepass.send_mobile(number.getText().toString()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            if (response.body().getStatus() == 101) {

                                progress.smoothToHide();

                                Toast.makeText(
                                        SendNumber_ChangePassActivity.this
                                        , response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                Intent goto_changepass = new Intent(SendNumber_ChangePassActivity.this,
                                        SmsVerification_ChangePassActivity.class);
                                goto_changepass.putExtra("phonenumber", number.getText().toString());
                                startActivity(goto_changepass);

                            } else {

                                progress.smoothToHide();
                                send.setVisibility(View.VISIBLE);

                                Toast.makeText(
                                        SendNumber_ChangePassActivity.this
                                        , response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            progress.smoothToHide();
                            send.setVisibility(View.VISIBLE);

                            Toast.makeText(
                                    SendNumber_ChangePassActivity.this
                                    , "خطای اتصال",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent goto_select = new Intent(SendNumber_ChangePassActivity.this,
                LoginActivity.class);
        {
            startActivity(goto_select);
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent goto_select = new Intent(SendNumber_ChangePassActivity.this,
                    LoginActivity.class);
            {
                startActivity(goto_select);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

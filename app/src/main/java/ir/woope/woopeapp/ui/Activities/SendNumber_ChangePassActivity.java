package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class SendNumber_ChangePassActivity extends AppCompatActivity {

    Retrofit retrofit_changepass;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sendnumber_changepass);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
//        }

        final EditText number = (EditText) findViewById(R.id.txtbx_sendnumber_changepass);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_sendnumber_changepass);
        final Button send = (Button) findViewById(R.id.btn_sendnumber_changepass);

        retrofit_changepass = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ChangePassInterface changepass = retrofit_changepass.create(ChangePassInterface.class);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                progress.setVisibility(View.VISIBLE);

                changepass.send_mobile(number.getText().toString()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if (response.body().getStatus() == 101) {

                            progress.setVisibility(View.GONE);

                            Toast.makeText(
                                    SendNumber_ChangePassActivity.this
                                    , response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            Intent goto_changepass = new Intent(SendNumber_ChangePassActivity.this,
                                    ChangePassActivity.class);
                            goto_changepass.putExtra("phonenumber", number.getText().toString());
                            startActivity(goto_changepass);

                        } else {

                            progress.setVisibility(View.GONE);

                                Toast.makeText(
                                        SendNumber_ChangePassActivity.this
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

                        Toast.makeText(
                                SendNumber_ChangePassActivity.this
                                , "خطای اتصال",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

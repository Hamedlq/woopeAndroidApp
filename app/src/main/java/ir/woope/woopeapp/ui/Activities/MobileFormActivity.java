package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import ir.woope.woopeapp.R;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MobileFormActivity extends AppCompatActivity {




    EditText edt;
    Retrofit retrofit;
    Random rand = new Random();
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_form);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
        }

        Button accept_phone_number = (Button) findViewById(R.id.btn_accept_number);

        accept_phone_number.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent goto_sms_validation = new Intent(MobileFormActivity.this,
                        SmsVerificationActivity.class);
                {
                    startActivity(goto_sms_validation);
                    goto_sms_validation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                }
            }});

//        retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.kavenegar.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        final MyApi myApi = retrofit.create(MyApi.class);
//
//        edt = (EditText) findViewById(R.id.txtbx_phone_number);
//        code = Integer.toString(rand.nextInt(1000) + 9000);

//        Button accept_phone_number = (Button) findViewById(R.id.btn_accept_number);
//
//        accept_phone_number.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//
//
//                if (edt.length() == 11) {
//
//                   // receptor = edt.getText().toString();
//
//                 //   code = rand.nextInt(1000) + 9999;
//
//                    myApi.savePost(edt.getText().toString(),code, "verify").enqueue(new Callback<Post>() {
//                        @Override
//                        public void onResponse(Call<Post> call, Response<Post> response) {
//
//                            Intent goto_sms_validation = new Intent(MobileForm.this,
//                                    sms_validation.class);
//                            startActivity(goto_sms_validation);
//                            Intent code_transfer = new Intent(getBaseContext(), sms_validation.class);
//                            code_transfer.putExtra("validation_code", code);
//                            startActivity(code_transfer);
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<Post> call, Throwable t) {
//                            // این متود هم فقط زمانی فرخوانی می‌شه که به هر دلیلی کانکشن ما با مشکل روبرو بشه
//                            Toast.makeText(
//                                    MobileForm.this
//                                    , "خطا!",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//
//                } else {
//                    Toast.makeText(
//                            MobileForm.this
//                            , "شماره موبایل نامعتبر!",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }


}

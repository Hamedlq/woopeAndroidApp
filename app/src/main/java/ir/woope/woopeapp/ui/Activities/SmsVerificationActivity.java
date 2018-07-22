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
import android.widget.TextView;

import com.chaos.view.PinView;

import ir.woope.woopeapp.R;

public class SmsVerificationActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_sms_validation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
        }

        final TextView countdown_timer = (TextView) findViewById(R.id.txt_countdown);
        final PinView user_verif_code = (PinView) findViewById(R.id.verification_code);
        Button save = (Button) findViewById(R.id.btn_save_verif_code);
        final String recieved_code = getIntent().getStringExtra("validation_code");

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent goto_mainpage_sms = new Intent(SmsVerificationActivity.this,
                        MainActivity.class);
                {

                    startActivity(goto_mainpage_sms);
                    goto_mainpage_sms.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();

                }
            }});

//                String user_code = user_verif_code.getText().toString();
//
//                if (user_verif_code.getText().toString().equals(recieved_code)) {
//                    Toast.makeText(
//                            sms_validation.this
//                            , getText(R.string.code_accepted),
//                            Toast.LENGTH_SHORT).show();
//                    Intent goto_user_info = new Intent(sms_validation.this,
//                            user_info.class);
//                    startActivity(goto_user_info);
//
//                } else {
//                    Toast.makeText(
//                            sms_validation.this
//                            , getText(R.string.wrong_code),
//                            Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });


        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                countdown_timer.setText(getText(R.string.remaining_time) + " : " + millisUntilFinished / 1000);

            }

            @Override
            public void onFinish() {
                countdown_timer.setText("");
                if (user_verif_code.getText().toString().matches("")) {

                    countdown_timer.setText("زمان شما به پایان رسید");

//                    Toast.makeText(
//                            sms_validation.this
//                            , "زمان شما به پایان رسید!",
//                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        }.start();

    }
}

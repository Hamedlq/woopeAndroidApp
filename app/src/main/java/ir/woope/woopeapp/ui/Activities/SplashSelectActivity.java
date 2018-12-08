package ir.woope.woopeapp.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.balysv.materialripple.MaterialRippleLayout;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SplashSelectActivity extends AppCompatActivity {

    @BindView(R.id.btn_enter_login)
    protected MaterialRippleLayout btn_enter_login;
    @BindView(R.id.btn_register)
    protected MaterialRippleLayout btn_register;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_select_splash);
        ButterKnife.bind(this);

        btn_register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_register = new Intent(SplashSelectActivity.this,
                        UserRegisterActivity.class);
                {
                    startActivity(goto_register);
                    finish();
                }
            }
        });

        btn_enter_login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_login = new Intent(SplashSelectActivity.this,
                        LoginActivity.class);
                {
                    startActivity(goto_login);
                    finish();
                }
            }
        });

//        btn_register.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
//
//            @Override
//            public void onComplete(RippleView rippleView) {
//                Intent goto_register = new Intent(SplashSelectActivity.this,
//                        UserRegisterActivity.class);
//                {
//                    startActivity(goto_register);
//                    finish();
//                }
//            }
//
//        });
//
//        btn_enter_login.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
//
//            @Override
//            public void onComplete(RippleView rippleView) {
//                Intent goto_login = new Intent(SplashSelectActivity.this,
//                        LoginActivity.class);
//                {
//                    startActivity(goto_login);
//                    finish();
//                }
//            }
//
//        });

    }
}

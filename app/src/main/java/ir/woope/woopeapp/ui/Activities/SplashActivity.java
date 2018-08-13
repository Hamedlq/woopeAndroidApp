package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Splash;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SplashActivity extends AppCompatActivity {

    Retrofit retrofit_splash;
    Button retry;
    TextView err;
    ProgressBar progress;
    ImageView wpelogo;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_splash);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
        }
        */

        retry = (Button) findViewById(R.id.btn_retry);
        err = (TextView) findViewById(R.id.txt_errorconnection);
        progress = (ProgressBar) findViewById(R.id.progressBar_splash);
        wpelogo = (ImageView) findViewById(R.id.img_wplogo);

        View splashLayout = findViewById(R.id.activity_splash);
        GetProfileFromServer();


        retry.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                retry.setVisibility(View.GONE);
                err.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                GetProfileFromServer();

            }
        });

    }

    public void GetProfileFromServer() {
        retrofit_splash = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        final SplashInterface splash = retrofit_splash.create(SplashInterface.class);

        final SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);

        splash.check_connection("bearer " + token).enqueue(new Callback<Profile>() {

            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                if (response.code() == 200) {
                    Profile user = response.body();
                    //profile=user.getMessage();
                    final SharedPreferences.Editor prefsEditor = settings.edit();
                    Gson gson = new Gson();

                    String json = gson.toJson(user);
                    prefsEditor.putString(PROFILE, json);
                    prefsEditor.apply();

                    Intent goto_main = new Intent(SplashActivity.this,
                            MainActivity.class);
                    goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(goto_main);


                } else if (response.code() == 401) {

                    Intent goto_login = new Intent(SplashActivity.this,
                            LoginActivity.class);
                    goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(goto_login);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

                retry.setVisibility(View.VISIBLE);
                err.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                Toast.makeText(
                        SplashActivity.this
                        , t.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });

    }

}

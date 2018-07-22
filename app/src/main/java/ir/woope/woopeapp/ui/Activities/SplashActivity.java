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

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.Splash;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class SplashActivity extends AppCompatActivity {

    Retrofit retrofit_splash;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.wpp));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.wpp));
        }

        final Button retry = (Button) findViewById(R.id.btn_retry);
        final TextView err = (TextView) findViewById(R.id.txt_errorconnection);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_splash);
        final ImageView wpelogo = (ImageView) findViewById(R.id.img_wplogo);

        View splashLayout = findViewById(R.id.activity_splash);

        retrofit_splash = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        final SplashInterface splash = retrofit_splash.create(SplashInterface.class);

        SharedPreferences settings = getApplicationContext().getSharedPreferences("token", 0);
        final String token = settings.getString("token", null);

        splash.check_connection("bearer "+token).enqueue(new Callback<Splash>() {

            @Override
            public void onResponse(Call<Splash> call, Response<Splash> response) {

                if (response.code() == 200) {

                    Intent goto_main = new Intent(SplashActivity.this,
                            MainActivity.class);
                    startActivity(goto_main);
                    {

                        goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(goto_main);

                    }

                } else if (response.code() == 401) {

                    Intent goto_login = new Intent(SplashActivity.this,
                            LoginActivity.class);
                    startActivity(goto_login);
                    {

                        goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(goto_login);

                    }

                }

            }

            @Override
            public void onFailure(Call<Splash> call, Throwable t) {

                retry.setVisibility(View.VISIBLE);
                err.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

            }
        });



//        else if(!(token==null||token=="")){
//
//            splash.check_connection(token).enqueue(new Callback<post_splash>() {
//
//                @Override
//                public void onResponse(Call<post_splash> call, Response<post_splash> response) {
//
//                    if (response.code() == 200) {
//
//                        Intent goto_main = new Intent(splash_screen.this,
//                                main_page.class);
//                        startActivity(goto_main);
//                        {
//
//                            goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            finish();
//                            startActivity(goto_main);
//
//                        }
//
//                    } else if (response.code() == 401) {
//
//                        Intent goto_login = new Intent(splash_screen.this,
//                                login.class);
//                        startActivity(goto_login);
//                        {
//
//                            goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            finish();
//                            startActivity(goto_login);
//
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<post_splash> call, Throwable t) {
//
//                    retry.setVisibility(View.VISIBLE);
//                    err.setVisibility(View.VISIBLE);
//                    progress.setVisibility(View.GONE);
//
//                }
//            });
//
//        }
        retry.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                retry.setVisibility(View.GONE);
                err.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                splash.check_connection(token).enqueue(new Callback<Splash>() {

                    @Override
                    public void onResponse(Call<Splash> call, Response<Splash> response) {

                        Intent goto_login = new Intent(SplashActivity.this,
                                LoginActivity.class);

                        startActivity(goto_login);
                        {

                            goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(goto_login);

                        }

                    }

                    @Override
                    public void onFailure(Call<Splash> call, Throwable t) {

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
        });

    }

}

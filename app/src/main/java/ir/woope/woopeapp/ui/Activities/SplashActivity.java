package ir.woope.woopeapp.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Splash;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SplashActivity extends AppCompatActivity {

    Retrofit retrofit_splash;
    Button retry;
    TextView err;
    AVLoadingIndicatorView progress;
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
        progress = (AVLoadingIndicatorView) findViewById(R.id.progressBar_splash);
        wpelogo = (ImageView) findViewById(R.id.img_wplogo);

        View splashLayout = findViewById(R.id.activity_splash);
        checkVersion();
        //GetProfileFromServer();

        retry.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                retry.setVisibility(View.GONE);
                err.setVisibility(View.GONE);
                progress.smoothToShow();
                //GetProfileFromServer();
                checkVersion();

            }
        });

    }

    public int getVersion() {
        int v = 1000;
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return v;
    }

    private void checkVersion() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        ProfileInterface profileInterface =
                retrofit.create(ProfileInterface.class);
        Call<ApiResponse> call =
                profileInterface.GetVersion("7");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int code = response.code();
                if (code == 200) {
                    ApiResponse res= response.body();
                    int appVersion = Integer.valueOf(res.getMessage());
                    if (appVersion > getVersion()) {
                        showUpdateDialog();
                    }else {
                        GetProfileFromServer();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                retry.setVisibility(View.VISIBLE);
                err.setVisibility(View.VISIBLE);
                progress.smoothToHide();

                Toast.makeText(
                        SplashActivity.this
                        , t.getMessage()+"checkVersion",
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showUpdateDialog() {
        retry.setVisibility(View.VISIBLE);
        err.setVisibility(View.VISIBLE);
        progress.smoothToHide();
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setMessage("لطفا نسخه جدید را دانلود کنید").setPositiveButton("دانلود", dialogClickListener)
                .setNegativeButton("بستن برنامه", dialogClickListener);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        this.setFinishOnTouchOutside(false);
        dialog.show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://woope.ir/app/app-release.apk"));
                    startActivity(browserIntent);

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    finishIt();
                    break;
            }
        }
    };

    private void finishIt() {
        finish();
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
                    goto_main.putExtra(GET_PROFILE_FROM_SERVER, false);
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
                progress.smoothToHide();
                Toast.makeText(
                        SplashActivity.this
                        , t.getMessage()+"getProfile",
                        Toast.LENGTH_LONG).show();

            }
        });

    }

}

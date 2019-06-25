package ir.woope.woopeapp.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.metrix.sdk.Metrix;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.METRIX_APP_ID;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.btn_retry)
    Button retry;
    @BindView(R.id.txt_errorconnection)
    TextView err;
    @BindView(R.id.progressBar_splash)
    AVLoadingIndicatorView progress;
    @BindView(R.id.splash_connecting)
    TextView connecting;

    Retrofit retrofit_splash;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_splash);

        Metrix.initialize(this, METRIX_APP_ID);
        Metrix.getInstance().enableLogging(true);
        Metrix.getInstance().setLogLevel(Log.DEBUG);
        Metrix.getInstance().setScreenFlowsAutoFill(true);
        Metrix.getInstance().setFlushEventsOnClose(true);

        ButterKnife.bind(this);

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

        YoYo.with(Techniques.FadeIn)
                .duration(1500)
                .repeat(9999999)
                .playOn(connecting);

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

                    ApiResponse res = response.body();
                    int appVersion = Integer.valueOf(res.getMessage());
                    if (appVersion > getVersion()) {
                        showUpdateDialog();
                    } else {
                        GetProfileFromServer();
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                hideLoading();
                Utility.showSnackbar(findViewById(R.id.activity_splash), R.string.network_error, Snackbar.LENGTH_LONG);

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
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://woope.ir/app/woope.apk"));
//                    startActivity(browserIntent);
                    shareViaApp("com.farsitel.bazaar","https://cafebazaar.ir/app/ir.woope.woopeapp/");

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    finishIt();
                    break;
            }
        }
    };

    private void shareViaApp(String appPackageName, String url) {
        boolean found = false;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(appPackageName) ||
                        info.activityInfo.name.toLowerCase().contains(appPackageName) ) {

                    intent.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;

            startActivity(Intent.createChooser(intent, "Select"));
        }
    }

    private void showLoading(){
        retry.setVisibility(View.GONE);
        err.setVisibility(View.GONE);
        progress.smoothToShow();
        connecting.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        progress.smoothToHide();
        connecting.setVisibility(View.GONE);
        retry.setVisibility(View.VISIBLE);
        err.setVisibility(View.VISIBLE);
    }

    private void finishIt() {
        finish();
    }

    public void GetProfileFromServer() {

        showLoading();

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


                }
                else if (response.code() == 401) {

                    final boolean tutorialIsShown = settings.getBoolean("tutorialIsShown", false);

                    if (tutorialIsShown == false) {

                        Intent goto_login = new Intent(SplashActivity.this,
                                SliderActivity.class);
                        goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(goto_login);

                    } else if (tutorialIsShown == true) {

                        /*Intent goto_login = new Intent(SplashActivity.this,
                                SplashSelectActivity.class);*/
                        Intent goto_login = new Intent(SplashActivity.this,
                                MainActivity.class);
                        goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(goto_login);

                    }
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

                hideLoading();

//                Toast.makeText(
//                        SplashActivity.this
//                        , R.string.network_error,
//                        Toast.LENGTH_LONG).show();

                Utility.showSnackbar(findViewById(R.id.activity_splash), R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });

    }

}

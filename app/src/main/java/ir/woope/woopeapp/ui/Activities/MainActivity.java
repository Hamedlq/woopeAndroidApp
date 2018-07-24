package ir.woope.woopeapp.ui.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.home_fragment;
import ir.woope.woopeapp.ui.Fragments.profile_fragment;
import ir.woope.woopeapp.ui.Fragments.search_fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;

public class MainActivity extends AppCompatActivity {


    boolean doubleBackToExitPressedOnce = false;
    //private TextView mTextMessage;
    String HOME_FRAGMENT = "HomeFragment";
    String SEARCH_FRAGMENT = "SearchFragment";
    String PROFILE_FRAGMENT = "ProfileFragment";
    String authToken = null;
    Profile profile = null;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, new home_fragment(), HOME_FRAGMENT)
                            .commit();
                    return true;
                case R.id.navigation_‌search:
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, new search_fragment(), SEARCH_FRAGMENT)
                            .commit();
                    return true;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.title_notifications);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, new profile_fragment(), PROFILE_FRAGMENT)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        sendRegistrationToServer();
        //getProfileFromServer();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new home_fragment(), HOME_FRAGMENT)
                .commit();

    }

    public Profile getUserProfile() {
        Gson gson = new Gson();
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String profileString= prefs.getString(Constants.GlobalConstants.PROFILE, null);
        if(profileString != null){
            profile = (Profile) gson.fromJson(profileString, Profile.class);
            return  profile;
        }else {
            getProfileFromServer();
        }
        return  null;
    }

    private void getProfileFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        ProfileInterface providerApiInterface =
                retrofit.create(ProfileInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");
        Call<Profile> call =
                providerApiInterface.getProfileFromServer(authToken);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                int code = response.code();
                if (code == 200) {
                    Profile user = response.body();
                    //profile=user.getMessage();
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(profile);
                    prefsEditor.putString(PROFILE, json);
                    prefsEditor.apply();
                    Fragment fragment = fragmentManager.findFragmentByTag(PROFILE_FRAGMENT);
                    if(fragment !=null){
                        ((profile_fragment) fragment).setValues(user);
                    }

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

            }
        });

    }

    private void sendRegistrationToServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        ProfileInterface profileInterface =
                retrofit.create(ProfileInterface.class);

        SharedPreferences prefs = getSharedPreferences(
                Constants.GlobalConstants.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "");

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String oneSignalToken=status.getSubscriptionStatus().getUserId();

        Call<ApiResponse> call =
                profileInterface.sendOneSignalToken(authToken, oneSignalToken);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                //Toast.makeText(MainActivity.this, "do", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(1);
            return;
        }
        Toast.makeText(MainActivity.this, R.string.press_again_to_exit , Toast.LENGTH_LONG).show();
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 4000);
    }
}

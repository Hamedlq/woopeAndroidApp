package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.ui.Fragments.profile_fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class GiftActivity extends AppCompatActivity {
    Profile profile;
    @BindView(R.id.txtbx_gift_code)
    protected EditText txtbx_gift_code;
    @BindView(R.id.gift_result)
    protected TextView gift_result;
    @BindView(R.id.button)
    protected Button button;
    @BindView(R.id.gift_response)
    protected LinearLayout gift_response;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R.id.ok_result)
    protected ImageView ok_result;
    @BindView(R.id.nok_result)
    protected ImageView nok_result;
    @BindView(R.id.ok_res)
    protected ImageView ok_res;
    @BindView(R.id.nok_res)
    protected ImageView nok_res;
    @BindView(R.id.gift_view)
    protected ScrollView gift_view;
    @BindView(R.id.vUserStats)
    protected LinearLayout loginPanel;
    @BindView(R.id.btn_enter_login)
    protected MaterialRippleLayout btn_enter_login;
    @BindView(R.id.btn_register)
    protected MaterialRippleLayout btn_register;


    String authToken;

    FragmentManager fragmentManager = getSupportFragmentManager();
    String PROFILE_FRAGMENT = "ProfileFragment";

    //Store store;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        ButterKnife.bind(this);
        hideProgreeBar();
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            //store = (Store) getIntent().getExtras().getSerializable(STORE);
        }

        if(IsLogedIn()) {
            loginPanel.setVisibility(View.GONE);
            gift_view.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }else{
            loginPanel.setVisibility(View.VISIBLE);
            gift_view.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }

        btn_register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_register = new Intent(GiftActivity.this,
                        UserRegisterActivity.class);
                {
                    startActivity(goto_register);
                    //getActivity().finish();
                }
            }
        });

        btn_enter_login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_login = new Intent(GiftActivity.this,
                        LoginActivity.class);
                {
                    startActivity(goto_login);
                    //getActivity().finish();
                }
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    SendGiftCode();
                }
                return false;
            }
        });

    }

    private boolean IsLogedIn() {
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String tokenString = prefs.getString(TOKEN, null);
        if(tokenString==null){
            return false;
        }else {
            return true;
        }
    }

    private void SendGiftCode() {

        gift_response.setVisibility(View.INVISIBLE);
        ok_res.setVisibility(View.INVISIBLE);
        ok_result.setVisibility(View.INVISIBLE);
        nok_res.setVisibility(View.INVISIBLE);
        nok_result.setVisibility(View.INVISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        ProfileInterface providerApiInterface =
                retrofit.create(ProfileInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(TOKEN, "null");

        showProgreeBar();
        Call<ApiResponse> call =
                providerApiInterface.sendGiftCode("Bearer " + authToken, txtbx_gift_code.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    if (response.body().getStatus() == 202) {
                        gift_response.setVisibility(View.VISIBLE);
                        ok_res.setVisibility(View.GONE);
                        ok_result.setVisibility(View.GONE);
                        nok_res.setVisibility(View.VISIBLE);
                        nok_result.setVisibility(View.VISIBLE);

                        gift_result.setText(response.body().getMessage());

/*
                        Toast.makeText(
                                GiftActivity.this
                                , response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
*/
                    }
                    if (response.body().getStatus() == 101) {
                        gift_response.setVisibility(View.VISIBLE);
                        nok_res.setVisibility(View.GONE);
                        nok_result.setVisibility(View.GONE);
                        ok_res.setVisibility(View.VISIBLE);
                        ok_result.setVisibility(View.VISIBLE);

                        gift_result.setText("کد هدیه اعمال شد");

                        /*Toast.makeText(
                                GiftActivity.this
                                , response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();*/
                        /*Intent i=getIntent();
                        setResult(RESULT_OK, i);
                        finish();*/
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideProgreeBar();
                gift_response.setVisibility(View.VISIBLE);
                nok_res.setVisibility(View.VISIBLE);
                nok_result.setVisibility(View.VISIBLE);
                ok_res.setVisibility(View.GONE);
                ok_result.setVisibility(View.GONE);
                gift_result.setText("خطا در ارتباط");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public Profile getProfile() {
        return profile;
    }

    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
        button.setEnabled(true);
    }



}

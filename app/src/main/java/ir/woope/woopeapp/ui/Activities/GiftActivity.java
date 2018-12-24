package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

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

    String authToken;

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

    private void SendGiftCode() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        ProfileInterface providerApiInterface =
                retrofit.create(ProfileInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

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
                gift_result.setText("خطا در ارتباط");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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

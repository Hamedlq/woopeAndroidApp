package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unstoppable.submitbuttonview.SubmitButton;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.EditProfileInterface;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.RegisterInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.ui.Fragments.profile_fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;


public class EditProfileActivity extends AppCompatActivity {

    Retrofit retrofit_splash;

    EditText name, family, age, phonenumber, email, bio;
    ProgressBar editprogress;
    Button sendedit;
    Retrofit retrofit_editprofile;
    private String gender;
    Profile profile = null;

    public TextView userNameFamily;
    public TextView username;
    public TextView userBio;
    public TextView cashCredit;
    public TextView woopeCredit;
    public TextView useNumber;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_editprofile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = findViewById(R.id.txtbx_name_editprofile);
        family = findViewById(R.id.txtbx_family_editprofile);
        age = findViewById(R.id.txtbx_age_editprofile);
        email = findViewById(R.id.txtbx_email_editprofile);
        bio = findViewById(R.id.txtbx_userbio_editprofile);
        editprogress = findViewById(R.id.progressBar_editprofile);
        sendedit = findViewById(R.id.btn_send_editprofile);

        final RadioRealButtonGroup group = findViewById(R.id.realradiogroup_gender_editprofile);

        // onClickButton listener detects any click performed on buttons by touch
        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0)
                    gender = "1";
                else if (position == 1)
                    gender = "2";
            }
        });

        final SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);

        retrofit_editprofile = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final EditProfileInterface edit = retrofit_editprofile.create(EditProfileInterface.class);

        edit.getProfileFromServer("bearer " + token).enqueue(new Callback<Profile>() {

            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                if (response.code() == 200) {
                    name.setText(response.body().getName());
                    family.setText(response.body().getFamily());
                    age.setText(response.body().getAge());
                    email.setText(response.body().getEmail());
                    bio.setText(response.body().getUserBio());
                    group.setPosition(response.body().getGender());
                    if (response.body().getGender() == 1)
                        group.setPosition(0);
                    else if (response.body().getGender() == 2)
                        group.setPosition(1);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {


            }
        });

        sendedit.setOnClickListener(new View.OnClickListener()

        {

            public void onClick(View arg0) {

                if (isValidEmail(email.getText())||TextUtils.isEmpty(email.getText())) {

                    editprogress.setVisibility(View.VISIBLE);
                    sendedit.setVisibility(View.GONE);

                    edit.send_edit("bearer " + token, name.getText().toString(), family.getText().toString(), bio.getText().toString(), email.getText().toString(), gender, age.getText().toString()).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            if (response.body().getStatus() == 101) {

                                editprogress.setVisibility(View.GONE);

                                Toast.makeText(
                                        EditProfileActivity.this
                                        , response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                finish();

                            } else {

                                editprogress.setVisibility(View.GONE);
                                sendedit.setVisibility(View.VISIBLE);

                                Toast.makeText(
                                        EditProfileActivity.this
                                        , response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            editprogress.setVisibility(View.GONE);
                            sendedit.setVisibility(View.VISIBLE);

                            Toast.makeText(

                                    EditProfileActivity.this
                                    , "خطای اتصال",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(

                            EditProfileActivity.this
                            , getResources().getString(R.string.invalid_email),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    public Profile getUserProfile() {
        if (profile == null) {
            Gson gson = new Gson();
            final SharedPreferences prefs =
                    this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
            String profileString = prefs.getString(Constants.GlobalConstants.PROFILE, null);
            if (profileString != null) {
                profile = (Profile) gson.fromJson(profileString, Profile.class);
                return profile;
            } else {
                return new Profile();
            }
        } else {
            return profile;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



}

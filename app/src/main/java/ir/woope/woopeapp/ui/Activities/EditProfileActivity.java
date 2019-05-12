package ir.woope.woopeapp.ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;


public class EditProfileActivity extends AppCompatActivity {

    Retrofit retrofit_splash;

    EditText name, family, phonenumber, email, bio;
    ProgressBar editprogress;
    Button sendedit;
    Retrofit retrofit_editprofile;
    private String gender;
    Profile profile;

    public TextView userNameFamily;
    public TextView username;
    public TextView userBio;
    public TextView cashCredit;
    public TextView woopeCredit;
    public TextView useNumber;

    RadioGroup genderGroup;
    RadioButton male, female;

    View layout;

    CardView birthDateCard;
    TextView birthDateText;

    int year,month,day;

    PersianDatePickerDialog picker;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_editprofile);
        layout = findViewById(R.id.activity_editprofile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = findViewById(R.id.txtbx_name_editprofile);
        family = findViewById(R.id.txtbx_family_editprofile);
        email = findViewById(R.id.txtbx_email_editprofile);
        bio = findViewById(R.id.txtbx_userbio_editprofile);
        editprogress = findViewById(R.id.progressBar);
        sendedit = findViewById(R.id.button);

        genderGroup = findViewById(R.id.radiogroup_gender_editprofile);
        male = findViewById(R.id.radiobutton_male_editprofile);
        female = findViewById(R.id.radiobutton_female_editprofile);

        birthDateCard = findViewById(R.id.birthDateCard);
        birthDateText = findViewById(R.id.birthDateText);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                View radioButton = genderGroup.findViewById(checkedId);

                switch (radioButton.getId()) {
                    case R.id.radiobutton_male_editprofile: // first button
                        gender = "1";
                        break;
                    case R.id.radiobutton_female_editprofile: // secondbutton
                        gender = "2";
                        break;
                }

            }
        });

        final SharedPreferences settings = getApplicationContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);

        retrofit_editprofile = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final EditProfileInterface edit = retrofit_editprofile.create(EditProfileInterface.class);
        showProgreeBar();
        edit.getProfileFromServer("bearer " + token).enqueue(new Callback<Profile>() {

            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                hideProgreeBar();

                if (response.code() == 200) {

                    name.setText(response.body().getName());

                    family.setText(response.body().getFamily());

                    if (response.body().getAgeYear()==0) {
                        birthDateText.setText(getResources().getString(R.string.choose));
                    } else
                        birthDateText.setText(response.body().getAgeYear() + "/" + response.body().getAgeMonth() + "/" + response.body().getAgeDay());

                    email.setText(response.body().getEmail());

                    bio.setText(response.body().getUserBio());

//                    genderGroup.setPosition(response.body().getGender());

                    if (response.body().getGender() == 1)
                        male.setChecked(true);
                    else if (response.body().getGender() == 2)
                        female.setChecked(true);

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

                hideProgreeBar();
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });

        sendedit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                if (isValidEmail(email.getText()) || TextUtils.isEmpty(email.getText())) {

                    editprogress.setVisibility(View.VISIBLE);
                    sendedit.setVisibility(View.GONE);

                    edit.send_edit("bearer " + token, name.getText().toString(), family.getText().toString(), bio.getText().toString(), email.getText().toString(), gender, "25",year,month,day).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                            if (response.body().getStatus() == 101) {

                                editprogress.setVisibility(View.GONE);

//                                Toast.makeText(
//                                        EditProfileActivity.this
//                                        , response.body().getMessage(),
//                                        Toast.LENGTH_SHORT).show();

                                Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_LONG);

                                Intent i = getIntent();
                                setResult(RESULT_OK, i);
                                finish();

                            } else {

                                editprogress.setVisibility(View.GONE);
                                sendedit.setVisibility(View.VISIBLE);

//                                Toast.makeText(
////                                        EditProfileActivity.this
////                                        , response.body().getMessage(),
////                                        Toast.LENGTH_SHORT).show();

                                Utility.showSnackbar(layout, response.body().getMessage(), Snackbar.LENGTH_LONG);

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {

                            editprogress.setVisibility(View.GONE);
                            sendedit.setVisibility(View.VISIBLE);

//                            Toast.makeText(
//                                    EditProfileActivity.this
//                                    , "خطای اتصال",
//                                    Toast.LENGTH_SHORT).show();

                            Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                        }
                    });
                } else {

                    Utility.showSnackbar(layout, R.string.invalid_email, Snackbar.LENGTH_LONG);

                }
            }
        });

        birthDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // item clicked
                showDatePicker();
            }
        });

    }

    public Profile getUserProfile() {
        if (profile == null) {
            Gson gson = new Gson();
            final SharedPreferences prefs =
                    this.getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            String msg = "آیا می‌خواهید از حساب کاربری خود خارج شوید؟";
            builder.setMessage(msg).setPositiveButton("بله", dialogClickListener).setNegativeButton("نه", dialogClickListener).show();
        }


        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    SharedPreferences settings = getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(TOKEN, null);
                    editor.apply();

                    Intent goto_splash = new Intent(EditProfileActivity.this,
                            SplashActivity.class);
                    goto_splash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  MainActivity.main.finish();
                    finish();
                    startActivity(goto_splash);
                    break;
            }
        }
    };

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void showProgreeBar() {
        editprogress.setVisibility(View.VISIBLE);
        sendedit.setEnabled(false);
    }

    public void hideProgreeBar() {
        editprogress.setVisibility(View.GONE);
        sendedit.setEnabled(true);
    }

    private void showFillError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("آیا می‌خواهید از حساب کاربری خود خارج شوید؟").setPositiveButton("بله", ConfirmDialogClickListener).setNegativeButton("نه فعلا", ConfirmDialogClickListener).show();
    }

    DialogInterface.OnClickListener ConfirmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:

                    dialog.dismiss();
                    break;
            }
        }
    };

    private void showDatePicker()
    {

        PersianCalendar initDate = new PersianCalendar();
        initDate.setPersianDate(year, month, day);



        picker = new PersianDatePickerDialog(this)
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButtonVisible(false)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setMinYear(1300)
                .setActionTextColor(Color.GRAY)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                        birthDateText.setText(persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay());
                        day=persianCalendar.getPersianDay();
                        month=persianCalendar.getPersianMonth();
                        year=persianCalendar.getPersianYear();
                    }

                    @Override
                    public void onDismissed() {

                    }
                });

        picker.setInitDate(initDate);

        picker.show();

    }

}

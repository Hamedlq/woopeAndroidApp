package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.unstoppable.submitbuttonview.SubmitButton;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.EditProfileInterface;
import ir.woope.woopeapp.interfaces.RegisterInterface;
import ir.woope.woopeapp.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;


public class EditProfileActivity extends AppCompatActivity {

    EditText name, family, age, phonenumber, email, bio;
    ProgressBar editprogress;
    Button sendedit;
    Retrofit retrofit_editprofile;
    private String gender;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_editprofile);

        name = findViewById(R.id.txtbx_name_editprofile);
        family = findViewById(R.id.txtbx_family_editprofile);
        age = findViewById(R.id.txtbx_age_editprofile);
        phonenumber = findViewById(R.id.txtbx_phonenumber_editprofile);
        email = findViewById(R.id.txtbx_email_editprofile);
        bio = findViewById(R.id.txtbx_userbio_editprofile);
        editprogress = findViewById(R.id.progressBar_editprofile);
        sendedit = findViewById(R.id.btn_send_editprofile);

        RadioRealButtonGroup group = findViewById(R.id.realradiogroup_gender_editprofile);

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


        retrofit_editprofile = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final EditProfileInterface edit = retrofit_editprofile.create(EditProfileInterface.class);

        final SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);

        sendedit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                editprogress.setVisibility(View.VISIBLE);
                sendedit.setVisibility(View.GONE);

                edit.send_edit("bearer " + token,name.getText().toString(),family.getText().toString(),bio.getText().toString(),email.getText().toString(),gender,age.getText().toString()).enqueue(new Callback<ApiResponse>() {
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
            }
        });

    }
}

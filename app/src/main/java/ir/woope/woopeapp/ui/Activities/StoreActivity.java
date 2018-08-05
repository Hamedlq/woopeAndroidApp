package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;

public class StoreActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    String STORE_FRAGMENT = "StoreFragment";
    String authToken = null;
    Profile profile = null;
    Store store;
    String profileString;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            store = (Store) getIntent().getExtras().getSerializable(STORE);
        }


        Button payBtn = (Button) findViewById(R.id.payBtn);
        payBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    goToPaying();
                }
                return false;
            }
        });

        
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        hideProgreeBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void goToPaying() {
        /*final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(PROFILE, "");

        Profile obj = gson.fromJson(json, Profile.class);*/
        Intent myIntent = new Intent(this, PayActivity.class);
        myIntent.putExtra(PREF_PROFILE, profile);
        myIntent.putExtra(STORE, store); //Optional parameters
        this.startActivity(myIntent);
        this.finish();
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

}

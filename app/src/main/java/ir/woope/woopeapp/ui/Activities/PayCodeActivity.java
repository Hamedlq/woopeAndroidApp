package ir.woope.woopeapp.ui.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class PayCodeActivity extends AppCompatActivity {
    @BindView(R.id.backdrop)
    protected ImageView backdrop;
    @BindView(R.id.button)
    protected Button btn;
    @BindView(R.id.store_name)
    protected TextView StoreName_tv;
    String profileString;
    String transactionString;
    String payedPoints;
    Profile profile;
    String authToken;
    PayListModel payListModel;

    Toolbar toolbar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_pay);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            payListModel = (PayListModel) getIntent().getExtras().getSerializable(PAY_LIST_ITEM);
            //payedPoints = getIntent().getStringExtra(POINTS_PAYED);
        }

        TextView ConfirmCode = findViewById(R.id.ConfirmCode);

        //TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(payListModel.storeName);

        ConfirmCode.setText(payListModel.confirmationCode);

        Picasso.with(PayCodeActivity.this).load(Constants.GlobalConstants.LOGO_URL + payListModel.logoSrc).into(backdrop);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Intent goto_main = new Intent(PayCodeActivity.this,
                            MainActivity.class);
                    goto_main.putExtra(GET_PROFILE_FROM_SERVER, true);
                    goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(goto_main);
                }
                return false;
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pay_toolbar_items, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        if (item.getItemId() == R.id.action_support) {

            Intent goto_verifphone = new Intent(this,
                    ContactUsActivity.class);
            startActivity(goto_verifphone);

        }

        return super.onOptionsItemSelected(item);
    }

//    public void showhint() {
//
//        final TapTargetSequence sequence = new TapTargetSequence(this)
//                .targets(
//                        // Likewise, this tap target will target the search button
//                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_support, "This is a search icon", "As you can see, it has gotten pretty dark around here...")
//                                .dimColor(android.R.color.black)
//                                .outerCircleColor(R.color.colorAccent)
//                                .targetCircleColor(android.R.color.black)
//                                .transparentTarget(true)
//                                .textColor(android.R.color.black)
//                                .id(2)
//                )
//                .listener(new TapTargetSequence.Listener() {
//                    // This listener will tell us when interesting(tm) events happen in regards
//                    // to the sequence
//                    @Override
//                    public void onSequenceFinish() {
//                        SharedPreferences prefs =
//                                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
//
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean("PAYCODEACTIVITYFIRSTRUN", false);
//                        editor.commit();
//                    }
//
//                    @Override
//                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//
//                    }
//
//                    @Override
//                    public void onSequenceCanceled(TapTarget lastTarget) {
//
//                    }
//                });
//
//        sequence.start();
//
//    }


}

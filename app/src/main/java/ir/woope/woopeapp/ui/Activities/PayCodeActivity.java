package ir.woope.woopeapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.POINTS_PAYED;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

public class PayCodeActivity extends AppCompatActivity {

    String profileString;
    String transactionString;
    String payedPoints;
    Profile profile;
    String authToken;
    PayListModel payListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_pay);
        Intent intent = getIntent();
        //storeName = intent.getStringExtra(STORE_NAME);
        profileString = intent.getStringExtra(PREF_PROFILE);
        transactionString = intent.getStringExtra(PAY_LIST_ITEM);
        payedPoints = intent.getStringExtra(POINTS_PAYED);

        Gson gson = new Gson();
        profile = (Profile) gson.fromJson(profileString, Profile.class);
        payListModel = (PayListModel) gson.fromJson(transactionString, PayListModel.class);


        //StoreName

        TextView ConfirmCode = findViewById(R.id.ConfirmCode);

        //pointText.setText(storeName);

        TextView StoreName_tv=findViewById(R.id.StoreName);
        StoreName_tv.setText(payListModel.storeName);

        ConfirmCode.setText(payListModel.confirmationCode);

    }
}

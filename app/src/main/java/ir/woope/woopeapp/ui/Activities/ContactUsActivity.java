package ir.woope.woopeapp.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import ir.woope.woopeapp.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_contact_us);

        LinearLayout callLayout = findViewById(R.id.call_woope_support);
        LinearLayout telegramLayout = findViewById(R.id.telegram_woope_support);

        callLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String number = getResources().getString(R.string.support_phoneNumber);
                String uri = "tel:" + number.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);

            }
        });

        telegramLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/Hamedlq"));
                startActivity(telegram);

            }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactUs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

        return super.onOptionsItemSelected(item);
    }
}

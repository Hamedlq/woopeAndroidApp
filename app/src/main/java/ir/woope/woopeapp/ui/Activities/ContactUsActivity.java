package ir.woope.woopeapp.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import ir.woope.woopeapp.R;

public class ContactUsActivity extends AppCompatActivity {

    AVLoadingIndicatorView loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_contact_us);

        loading = findViewById(R.id.loading_contactUs);

        LinearLayout callLayout = findViewById(R.id.phone_woope_support);
        LinearLayout telegramLayout = findViewById(R.id.telegram_woope_support);
        LinearLayout websiteLayout = findViewById(R.id.website_woope_support);
        LinearLayout instaLayout = findViewById(R.id.instagram_woope_support);
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

                Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/woope"));
                startActivity(telegram);

            }

        });

        websiteLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://woope.ir"));
                startActivity(telegram);

            }

        });

        instaLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://instagram.com/_u/woope.ir");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(getBaseContext(), insta)){
                    startActivity(insta);
                } else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/woope.ir")));
                }

            }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactUs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);

//        WebView myWebView = (WebView) findViewById(R.id.contactUs_webView);
//        myWebView.getSettings().setJavaScriptEnabled(true);
//        myWebView.loadUrl("https://woope.ir/contactus");
//        showProgreeBar();
//
//        myWebView.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//
//                hideProgreeBar();
//
//            }
//
//        });

    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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

    private void hideProgreeBar() {
        loading.smoothToHide();
    }

    private void showProgreeBar() {
        loading.smoothToShow();
    }

}

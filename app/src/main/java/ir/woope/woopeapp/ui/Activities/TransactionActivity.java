package ir.woope.woopeapp.ui.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.ui.Fragments.TransListFragment;

public class TransactionActivity extends AppCompatActivity {

    String LIST_FRAGMENT = "HomeFragment";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.frame_layout, new TransListFragment(), LIST_FRAGMENT)
                .commit();

    }

}

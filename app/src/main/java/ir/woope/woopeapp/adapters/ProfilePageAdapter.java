package ir.woope.woopeapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.ui.Fragments.ProfileTransactionListFragment;
import ir.woope.woopeapp.ui.Fragments.profileBookmarkFragment;

/**
 * Created by alireza on 3/30/18.
 */

public class ProfilePageAdapter  extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public ProfilePageAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                ProfileTransactionListFragment tab1 = new ProfileTransactionListFragment();
                return tab1;
            case 1:
                profileBookmarkFragment tab2 = new profileBookmarkFragment();
                return tab2;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}

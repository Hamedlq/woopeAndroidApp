package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.ui.Fragments.storeGalleryFragment;
import ir.woope.woopeapp.ui.Fragments.storeInfoFragment;


public class StoreViewPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;

    public StoreViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new storeInfoFragment();
        } else if (position == 1) {
            return new storeGalleryFragment();
        } else {
            return null;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.info);
            case 1:
                return mContext.getString(R.string.gallery);
            default:
                return null;
        }
    }

}


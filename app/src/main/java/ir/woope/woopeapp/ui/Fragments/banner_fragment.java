package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.FilterAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import ir.woope.woopeapp.ui.Activities.StoreListActivity;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_MODEL;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_TITLE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_LIST;

public class banner_fragment extends Fragment {

    @BindView(R.id.thumbnail)
    ImageView banner;

    private List<Store> banners;
    filter_fragment.ItemTouchListener itemTouchListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            banners = (List<Store>) bundle.getSerializable(STORE_LIST);
        }
    }

    private View mRecycler;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.adv_card, null);
        ButterKnife.bind(this, mRecycler);
        Picasso.with(getContext()).load(Constants.GlobalConstants.LOGO_URL + banners.get(0).logoSrc).into(banner);

        banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banners.get(0).website));
                    startActivity(browserIntent);
                }
                return true;
            }
        });

        return mRecycler;
    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

    }

}

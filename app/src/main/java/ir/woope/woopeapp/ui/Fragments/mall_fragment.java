package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.MallAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.MallModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import ir.woope.woopeapp.ui.Activities.StoreListActivity;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_MODEL;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_TITLE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MALL_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MALL_MODEL;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_LIST;

public class mall_fragment extends Fragment {

    @BindView(R.id.filter_recycler)
    RecyclerView filter_recycler;
    @BindView(R.id.title)
    TextView title;

    private View mRecycler;
    private List<MallModel> malls;
    private String listTitle;
    private MainListModel model;

    private MallAdapter filter_adapter;

    mall_fragment.ItemTouchListener itemTouchListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            malls = (List<MallModel>) bundle.getSerializable(MALL_LIST);
            listTitle = bundle.getString(LIST_TITLE);
            model = (MainListModel) bundle.getSerializable(LIST_MODEL);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_mall, null);
        ButterKnife.bind(this, mRecycler);
        title.setText(listTitle);
        List<MallModel> albumList = new ArrayList<>();
        MallAdapter adapter = new MallAdapter(getActivity(), albumList, itemTouchListener);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        filter_recycler.setLayoutManager(layoutManager);

        filter_recycler.setItemAnimator(new DefaultItemAnimator());
        filter_recycler.setAdapter(adapter);


        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onCardViewTap(View view, int position) {

                MallModel s = malls.get(position);
                final SharedPreferences prefs =
                        getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString(PROFILE, "");
                Profile obj = gson.fromJson(json, Profile.class);
                Intent myIntent = new Intent(getActivity(), StoreListActivity.class);
                myIntent.putExtra(LIST_MODEL, model);
                myIntent.putExtra(MALL_MODEL, s);
                getActivity().startActivityForResult(myIntent, RELOAD_LIST);

            }
        };
        adapter = new MallAdapter(getActivity(), malls, itemTouchListener);
        filter_recycler.setAdapter(adapter);

        return mRecycler;
    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

    }
}

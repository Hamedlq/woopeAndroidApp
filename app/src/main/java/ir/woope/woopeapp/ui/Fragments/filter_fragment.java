package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import ir.woope.woopeapp.adapters.FilterAdapter;
import ir.woope.woopeapp.adapters.ProfileBookmarkAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.ListPaddingDecoration;
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

public class filter_fragment extends Fragment {



    @BindView(R.id.filter_recycler)
    RecyclerView filter_recycler;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.more_store)
    TextView more_store;

    private View mRecycler;
    private List<Store> stores;
    private String listTitle;
    private MainListModel model;

    private FilterAdapter filter_adapter;

    filter_fragment.ItemTouchListener itemTouchListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            stores = (List<Store>) bundle.getSerializable(STORE_LIST);
            listTitle = bundle.getString(LIST_TITLE);
            model = (MainListModel) bundle.getSerializable(LIST_MODEL);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_filter, null);
        ButterKnife.bind(this, mRecycler);
        title.setText(listTitle);
        List<Store> albumList = new ArrayList<>();
        FilterAdapter adapter = new FilterAdapter(getActivity(), albumList, itemTouchListener);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        filter_recycler.setLayoutManager(layoutManager);

        filter_recycler.setItemAnimator(new DefaultItemAnimator());
        filter_recycler.setAdapter(adapter);
        more_store.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent store_list = new Intent(getActivity(),
                            StoreListActivity.class);
                    store_list.putExtra(LIST_MODEL, model);

                    startActivity(store_list);
                }
                return true;
            }
        });

        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onCardViewTap(View view, int position) {

                Store s = stores.get(position);
                final SharedPreferences prefs =
                        getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString(PROFILE, "");
                Profile obj = gson.fromJson(json, Profile.class);
                Intent myIntent = new Intent(getActivity(), StoreActivity.class);
                myIntent.putExtra(PREF_PROFILE, obj);
                myIntent.putExtra(STORE, s); //Optional parameters
                getActivity().startActivityForResult(myIntent, RELOAD_LIST);

            }
        };
        adapter = new FilterAdapter(getActivity(), stores, itemTouchListener);
        filter_recycler.setAdapter(adapter);

        return mRecycler;
    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

    }
}

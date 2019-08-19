package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import ir.woope.woopeapp.ui.Activities.StoreListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_BRANCHES;
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
    private Boolean hasMoreBranches;
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
            hasMoreBranches = bundle.getBoolean(LIST_BRANCHES);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_filter, null);
        ButterKnife.bind(this, mRecycler);
        title.setText(listTitle);
        List<Store> albumList = new ArrayList<>();
        FilterAdapter adapter = new FilterAdapter(getContext(), albumList, itemTouchListener);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        filter_recycler.setLayoutManager(layoutManager);

        filter_recycler.setItemAnimator(new DefaultItemAnimator());
        filter_recycler.setAdapter(adapter);

        if (hasMoreBranches)
            more_store.setVisibility(View.VISIBLE);
        else if (!hasMoreBranches || hasMoreBranches == null)
            more_store.setVisibility(View.GONE);

        more_store.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent store_list = new Intent(getContext(),
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
//                final SharedPreferences prefs =
//                        getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
//                Gson gson = new Gson();
//                String json = prefs.getString(PROFILE, "");
//                Profile obj = gson.fromJson(json, Profile.class);
//                Intent myIntent = new Intent(getContext(), StoreActivity.class);
//                myIntent.putExtra(PREF_PROFILE, obj);
//                myIntent.putExtra(STORE, s); //Optional parameters
//                getActivity().startActivityForResult(myIntent, RELOAD_LIST);
                shareStore(s.storeId);

            }
        };
        adapter = new FilterAdapter(getContext(), stores, itemTouchListener);
        filter_recycler.setAdapter(adapter);

        return mRecycler;
    }

    private void shareStore(long branchId) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<ApiResponse> call =
                providerApiInterface.shareStore("bearer " + authToken, branchId);

//        showShareProgreeBar();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//                hideShareProgreeBar();
                int code = response.body().status;
                if (code == 101)
                    shareText(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
//                hideShareProgreeBar();
                Utility.showSnackbar(getView().findViewById(R.id.main_content), R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });
    }

    private void shareText(String text) {

        if (((MainActivity) getActivity()) != null)
            ((MainActivity) getActivity()).shareText(text);

    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

    }
}

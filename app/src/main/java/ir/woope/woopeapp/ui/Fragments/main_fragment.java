package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.AttributeSet;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.CategoryAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Category;
import ir.woope.woopeapp.models.ListTypes;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.MallModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.GiftActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.StoreListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_MODEL;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_TITLE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MALL_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

/**
 * Created by Hamed on 6/11/2018.
 */

public class main_fragment extends Fragment {

    /*@BindView(R.id.category_recycler)
    RecyclerView category_recycler;*/
    @BindView(R.id.container_layout)
    LinearLayout container_layout;
    @BindView(R.id.all_button)
    TextView all_button;

    String MOST_WOOPE_FILTER = "MOST_WOOPE";
    String MOST_PURCHASE_FILTER = "MOST_PURCHASE";

    private View mRecycler;
    private List<Category> categoryList;
    private List<MainListModel> mainFilterList;
    String ALBUM_FRAGMENT = "AlbumFragment";
    String authToken;
    private boolean isVisible = true;
    private CategoryAdapter category_adapter;

    Toolbar toolbar;

    List<Store> stores;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        stores = new ArrayList<>();
        getActivity().setTitle("");
        mRecycler = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.bind(this, mRecycler);
        setHasOptionsMenu(true);
       /* categoryList = new ArrayList<>();
        categoryList.add(new Category("همه", 5));
        categoryList.add(new Category("dolam", 5));*/
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //category_recycler.setLayoutManager(layoutManager);
        //category_adapter = new CategoryAdapter(getActivity(), categoryList);
        //category_recycler.setAdapter(category_adapter);


        toolbar = (Toolbar) mRecycler.findViewById(R.id.home_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        all_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    MainListModel ml = new MainListModel();
                    ml.listOrder = 81;
                    Intent store_list = new Intent(getActivity(),
                            StoreListActivity.class);
                    store_list.putExtra(LIST_MODEL, ml);

                    startActivity(store_list);

                }
                return true;
            }
        });

        getListItems();
        //getStoresByPage(MOST_WOOPE_FILTER);
        //getStoresByPage(MOST_PURCHASE_FILTER);
        return mRecycler;
    }

    //private void getStoresByPage(final String filter) {
    private void getStores(final FrameLayout childLayout, final MainListModel ml) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Call<List<Store>> call =
                providerApiInterface.GetStoresFilter("bearer " + authToken, 0,
                        ml.listOrder, ml.numberPerList, null);

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                int code = response.code();
                if (code == 200) {
                    if (isVisible) {
                        if (response.body().size() > 1) {
                            Bundle arguments = new Bundle();
                            arguments.putSerializable(STORE_LIST, (ArrayList<Store>) response.body());
                            arguments.putSerializable(LIST_MODEL, ml);
                            arguments.putString(LIST_TITLE, ml.listTitle);
                            Fragment woopeFilter = new filter_fragment();
                            woopeFilter.setArguments(arguments);
                            fragmentManager.beginTransaction()
                                    .replace(ml.listOrder, woopeFilter, String.valueOf(ml.listOrder))
                                    .commit();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {

            }
        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
        } else {
            isVisible = false;
        }
    }

    private void getMalls(final FrameLayout childLayout, final MainListModel ml) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Call<List<MallModel>> call =
                providerApiInterface.GetMallList("bearer " + authToken, 0, ml.numberPerList);

        call.enqueue(new Callback<List<MallModel>>() {
            @Override
            public void onResponse(Call<List<MallModel>> call, Response<List<MallModel>> response) {
                int code = response.code();
                if (code == 200) {
                    if (isVisible) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(MALL_LIST, (ArrayList<MallModel>) response.body());
                        arguments.putSerializable(LIST_MODEL, ml);
                        arguments.putString(LIST_TITLE, ml.listTitle);
                        Fragment mallFilter = new mall_fragment();
                        mallFilter.setArguments(arguments);
                        fragmentManager.beginTransaction()
                                .replace(ml.listOrder, mallFilter, String.valueOf(ml.listOrder))
                                .commit();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MallModel>> call, Throwable t) {

            }
        });


    }

    private void getListItems() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Call<List<MainListModel>> call =
                providerApiInterface.getMainLists("bearer " + authToken);

        call.enqueue(new Callback<List<MainListModel>>() {
            @Override
            public void onResponse(Call<List<MainListModel>> call, Response<List<MainListModel>> response) {
                int code = response.code();
                if (code == 200) {
                    mainFilterList = (List<MainListModel>) response.body();
                    showLists();
                }
            }

            @Override
            public void onFailure(Call<List<MainListModel>> call, Throwable t) {

            }
        });


    }

    private void showLists() {
        for (MainListModel ml : mainFilterList) {
            if (ml.listType == ListTypes.MallList) {
                FrameLayout childLayout = new FrameLayout(getActivity());
                childLayout.setId(ml.listOrder);
                FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                container_layout.addView(childLayout, parentParams);
                getMalls(childLayout, ml);
            } else if (ml.listType == ListTypes.StoreList) {
                FrameLayout childLayout = new FrameLayout(getActivity());
                childLayout.setId(ml.listOrder);
                FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                container_layout.addView(childLayout, parentParams);
                getStores(childLayout, ml);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_gift, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_store:
                Profile userobj = ((MainActivity) getActivity()).getUserProfile();
                Intent giftIntent = new Intent(getActivity(), GiftActivity.class);
                giftIntent.putExtra(PREF_PROFILE, userobj);
                getActivity().startActivityForResult(giftIntent, SHOULD_GET_PROFILE);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                break;
            default:
                break;
        }
        return true;
    }
}

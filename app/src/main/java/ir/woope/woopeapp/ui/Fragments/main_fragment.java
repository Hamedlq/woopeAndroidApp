package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.CategoryAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.ListTypes;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.MallModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.ContactUsActivity;
import ir.woope.woopeapp.ui.Activities.GiftActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_BRANCHES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_MODEL;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.LIST_TITLE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MALL_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_LIST;

/**
 * Created by Hamed on 6/11/2018.
 */

public class main_fragment extends Fragment {

    @BindView(R.id.categoryRecycler)
    RecyclerView category_recycler;
    @BindView(R.id.container_layout)
    LinearLayout container_layout;

//    TabLayout categoryTab;
//
//    @BindView(R.id.all_button)
//    RelativeLayout all_button;

    String MOST_WOOPE_FILTER = "MOST_WOOPE";
    String MOST_PURCHASE_FILTER = "MOST_PURCHASE";

    private View mRecycler;
    private List<CategoryModel> categoryList;
    CategoryAdapter categoryAdapter;
    private List<MainListModel> mainFilterList;
    String ALBUM_FRAGMENT = "AlbumFragment";
    String authToken;
    private boolean isVisible = true;

    Toolbar toolbar;

    List<Store> stores;

    public CategoryTouchListener categoryTouchListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        getListItems();
        getCategories();
        super.onResume();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        stores = new ArrayList<>();
        getActivity().setTitle("");
        mRecycler = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.bind(this, mRecycler);
        this.setHasOptionsMenu(true);

//        categoryTab =(TabLayout) mRecycler.findViewById(R.id.categoryTabLayout);

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList,categoryTouchListener);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        category_recycler.setLayoutManager(layoutManager);
        category_recycler.setItemAnimator(new DefaultItemAnimator());
        category_recycler.setAdapter(categoryAdapter);

        toolbar = (Toolbar) mRecycler.findViewById(R.id.home_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

//        all_button.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    MainListModel ml = new MainListModel();
//                    ml.listOrder = 81;
//                    Intent store_list = new Intent(getContext(),
//                            StoreListActivity.class);
//                    store_list.putExtra(LIST_MODEL, ml);
//
//                    startActivity(store_list);
//
//                }
//                return true;
//            }
//        });

//        setupTabView(categoryNames,categoryIcons);

        getListItems();
        getCategories();
        //getStoresByPage(MOST_WOOPE_FILTER);
        //getStoresByPage(MOST_PURCHASE_FILTER);

        categoryTouchListener = new main_fragment.CategoryTouchListener() {
            @Override
            public void onCategoryTap(View v, int position) {
                search_fragment.getInstance().categoryId = categoryList.get(position).id;
                search_fragment.getInstance().adapter.emptyList();
                search_fragment.getInstance().findStoresByPage("",0,null,categoryList.get(position).id,null);
                search_fragment.getInstance().categoryButton.setText(categoryList.get(position).name);
                search_fragment.getInstance().categoryId = categoryList.get(position).id;
                search_fragment.getInstance().categoryAdapter.deselectItem(search_fragment.getInstance().selectedCategory);
                search_fragment.getInstance().categoryAdapter.selectItem(position);
                search_fragment.getInstance().selectedCategory = position;
//                ((MainActivity)getActivity()).switchPage(R.id.navigation_‌search);
                ((MainActivity)getActivity()).switchToSearch();
            }
        };

        return mRecycler;
    }

//    public void setupTabView(String[] categoryNames,int[] categoryIcons){
//        for (int i = 0; i < categoryTab.getTabCount(); i++) {
//            categoryTab.getTabAt(i).setCustomView(R.layout.category_item);
//            TextView categoryName =  categoryTab.getTabAt(i).getCustomView().findViewById(R.id.category_label);
//            categoryName.setText(categoryNames[i]);
//            ImageView CategoryIcon =  categoryTab.getTabAt(i).getCustomView().findViewById(R.id.category_icon);
//            Picasso.with(getContext()).load(categoryIcons[i]).into(CategoryIcon);
//        }
//    }

    //private void getStoresByPage(final String filter) {
    private void getStores(final FrameLayout childLayout, final MainListModel ml) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

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
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(STORE_LIST, (ArrayList<Store>) response.body());
                        arguments.putSerializable(LIST_MODEL, ml);
                        arguments.putString(LIST_TITLE, ml.listTitle);
                        arguments.putBoolean(LIST_BRANCHES, ml.hasMoreBranches);
                        Fragment woopeFilter = new filter_fragment();
                        woopeFilter.setArguments(arguments);
                        fragmentManager.beginTransaction()
                                .replace(ml.listOrder, woopeFilter, String.valueOf(ml.listOrder))
                                .commitAllowingStateLoss();
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
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

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
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

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
//            if (ml.listType == ListTypes.MallList) {
//                FrameLayout childLayout = new FrameLayout(getContext());
//                childLayout.setId(ml.listOrder);
//                FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//                container_layout.addView(childLayout, parentParams);
//                getMalls(childLayout, ml);
//            }
            if (ml.listType == ListTypes.StoreList) {
                FrameLayout childLayout = new FrameLayout(getContext());
                childLayout.setId(ml.listOrder);
                FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                container_layout.addView(childLayout, parentParams);
                getStores(childLayout, ml);
            } else if (ml.listType == ListTypes.BannerList) {

                FrameLayout childLayout = new FrameLayout(getContext());
                childLayout.setId(ml.listOrder);
                FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                container_layout.addView(childLayout, parentParams);
                getBanners(childLayout, ml);

            }
        }
    }

    private CardView createAdCard(String imageSrc, final String adLink) {

        CardView cardview;
        ViewGroup.LayoutParams layoutparams;
        ViewGroup.LayoutParams layoutparams2;
        ImageView imageview;
        RelativeLayout relativeLayout;

        cardview = new CardView(getContext());
        relativeLayout = new RelativeLayout(getContext());

        layoutparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
        );
        layoutparams2 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
        );

        relativeLayout.setLayoutParams(layoutparams);

        cardview.setLayoutParams(layoutparams);

        cardview.setRadius(10);

        cardview.setPadding(25, 25, 25, 25);

        cardview.setMaxCardElevation(30);

        cardview.setMaxCardElevation(6);

        imageview = new ImageView(getContext());

        imageview.setLayoutParams(layoutparams2);

        Picasso.with(getContext()).load(Constants.GlobalConstants.LOGO_URL + imageSrc).into(imageview);

        imageview.setPadding(25, 25, 25, 25);

        relativeLayout.addView(imageview);

        cardview.addView(relativeLayout);

        imageview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adLink));
                startActivity(browserIntent);
            }
        });

        return cardview;

    }

    private void getBanners(final FrameLayout childLayout, final MainListModel ml) {

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
//        showProgreeBar();

        Call<List<Store>> call =
                providerApiInterface.getBanners("bearer " + authToken, 1);

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {

                Bundle arguments = new Bundle();
                arguments.putSerializable(STORE_LIST, (ArrayList<Store>) response.body());
                Fragment banner = new banner_fragment();
                banner.setArguments(arguments);
                fragmentManager.beginTransaction()
                        .replace(ml.listOrder, banner, String.valueOf(ml.listOrder))
                        .commit();

            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {

//                String s = t.getMessage();
//                hideProgreeBar();
//                snack.dismiss();
//                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });

    }

    private void getCategories() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<List<CategoryModel>> call =
                providerApiInterface.getCategories("bearer " + authToken);

        call.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                int code = response.code();
                if (code == 200) {
                    categoryList =  response.body();
                    categoryAdapter = new CategoryAdapter(getContext(), categoryList,categoryTouchListener);
                    category_recycler.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {

            }
        });

    }

    public interface CategoryTouchListener {
        public void onCategoryTap(View v, int position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_gift, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_store:
                Profile userobj = ((MainActivity) getActivity()).getUserProfile();
                Intent giftIntent = new Intent(getActivity(), GiftActivity.class);
                giftIntent.putExtra(PREF_PROFILE, userobj);
                getActivity().startActivityForResult(giftIntent, SHOULD_GET_PROFILE);
                getActivity().overridePendingTransition(R.anim.slide_down, R.anim.no_change);
                break;

            case R.id.nav_help:
                Intent intentContactUs = new Intent(getActivity(), ContactUsActivity.class);
                getActivity().startActivityForResult(intentContactUs, SHOULD_GET_PROFILE);
                getActivity().overridePendingTransition(R.anim.slide_down, R.anim.no_change);
                break;

            default:
                break;
        }
        return true;
    }
}

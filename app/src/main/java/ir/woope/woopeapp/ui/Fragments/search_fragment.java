package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.CategoryAdapter;
import ir.woope.woopeapp.adapters.SortAdapter;
import ir.woope.woopeapp.adapters.StoreSearchAdapter_v2;
import ir.woope.woopeapp.adapters.ZoneAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.SortType;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.ZoneModel;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

/**
 * Created by Hamed on 6/11/2018.
 */

public class search_fragment extends Fragment {

    private View mRecycler;

    private static search_fragment instance = null;
    String ALBUM_FRAGMENT = "AlbumFragment";
    String authToken;

//    private FloatingSearchView floatingSearchView;
//    FloatingActionButton fab;

    ProgressBar progressBar;

    View layout;

    @BindView(R.id.zoneButton)
    AppCompatButton zoneButton;
    @BindView(R.id.acceptZoneCardBtn)
    AppCompatButton closeZoneCardButton;
    @BindView(R.id.zoneCard)
    CardView zoneCard;
    //    @BindView(R.id.zoneRecycler)
    RecyclerView zoneRecycler;
    @BindView(R.id.zoneSearchEditText)
    EditText ZoneSearch;
    @BindView(R.id.categoryButton)
    AppCompatButton categoryButton;
    @BindView(R.id.categoryCard)
    CardView categoryCard;
    //    @BindView(R.id.categoryRecycler)
    RecyclerView categoryRecycler;
    RecyclerView sortRecycler;

    @BindView(R.id.searchEditText)
    EditText searchText;

    //SEARCH
    private List<Store> albumList;
    private RecyclerView recyclerView;
    public StoreSearchAdapter_v2 adapter;
    ItemTouchListener itemTouchListener;
    String query;
    boolean searchInProgress = false;
    private boolean itShouldLoadMore = true;
    int PageNumber = 0;
    String newquery;

    //ZONE
    ZoneAdapter zoneAdapter;
    private List<ZoneModel> zoneList;
    List<Long> searchableZoneList = new ArrayList<>();
    ZoneTouchListener zoneTouchListener;

    //CATEGORY
    CategoryAdapter categoryAdapter;
    public main_fragment.CategoryTouchListener categoryTouchListener;
    int selectedCategory;
    public Long categoryId = null;
    private List<CategoryModel> categoryList = new ArrayList<>();

    //SORT
    public SortAdapter sortAdapter;
    List<SortType> sortTypesList = new ArrayList<>();
    List<ZoneModel> sortedZoneList;
    public Short sortId = null;
    int selectedSort;
    Boolean isSorted = true;
    public SortTouchListener sortTouchListener;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = getView().findViewById(R.id.activity_search_fragment);

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

    }

    public static search_fragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_search, null);

        ButterKnife.bind(this, mRecycler);

        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onCardViewTap(View view, int position) {
                Store s = albumList.get(position);
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

            @Override
            public void onFollowTap(int position) {
                Store s = albumList.get(position);
                followStore(s);
            }
        };

        zoneTouchListener = new ZoneTouchListener() {
            @Override
            public void onZoneTap(View v, int position) {

                isSorted = false;

                if (((CustomCheckBox) v).isChecked()) {
                    for (int i = 0; i <= zoneList.size(); i++)
                        if (zoneAdapter.getList().get(position).id == zoneList.get(i).id) {
                            zoneList.get(i).isChecked = true;
                            break;
                        }
                } else if (!((CustomCheckBox) v).isChecked())
                    for (int i = 0; i <= zoneList.size(); i++)
                        if (zoneAdapter.getList().get(position).id == zoneList.get(i).id) {
                            zoneList.get(i).isChecked = false;
                            break;
                        }

            }
        };

        sortTouchListener = new SortTouchListener() {
            @Override
            public void onSortTap(View v, int position) {

                if (sortId == null) {
                    sortId = sortTypesList.get(position).id;
                    PageNumber = 0;
                    adapter.emptyList();
                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                    sortAdapter.selectItem(position);
                    selectedSort = position;
                } else if (position == selectedSort) {
                    sortId = null;
                    PageNumber = 0;
                    adapter.emptyList();
                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                    sortAdapter.deselectItem(position);
                } else if (sortId != null) {
                    sortId = sortTypesList.get(position).id;
                    PageNumber = 0;
                    adapter.emptyList();
                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                    sortAdapter.selectItem(position);
                    sortAdapter.deselectItem(selectedSort);
                    selectedSort = position;
                }
            }
        };

        zoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (zoneCard.getVisibility() == View.GONE) {
//                    if (!isSorted) {
//                        updateZoneList();
//                        isSorted = true;
//                    }
                    showZoneCard();
                } else if (zoneCard.getVisibility() == View.VISIBLE) {
//                    if (!isSorted) {
////                        updateZoneList();
//                        isSorted = true;
//                    }
                    hideZoneCard();
                }

            }
        });

        closeZoneCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateZoneList();
                hideZoneCard();
                searchableZoneList.clear();

                for (ZoneModel zone : sortedZoneList) {
                    if (zone.isChecked) {
                        searchableZoneList.add(zone.id);
                    }
                }

                PageNumber = 0;
                adapter.emptyList();
                findStoresByPage(query, PageNumber, searchableZoneList, categoryId, sortId);

                if (searchableZoneList.size() == 0)
                    zoneButton.setText("انتخاب محله");
                else if (searchableZoneList.size() == 1) {
                    for (ZoneModel zone : sortedZoneList)
                        if (zone.isChecked)
                            zoneButton.setText(zone.name);
                } else if (searchableZoneList.size() > 1) {
                    String zones = "";
                    int count = 1;
                    for (ZoneModel zone : sortedZoneList)
                        if (zone.isChecked) {
                            if (count == 1) {
                                zones = zones + zone.name;
                                count++;
                            } else if (count == 2) {
                                zones = zones + "," + zone.name;
                                count++;
                            } else if (count == 3)
                                zones = zones + ",...";
                            else
                                break;
                        }
                    zoneButton.setText(zones);
                }

            }
        });

        progressBar = (ProgressBar) mRecycler.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) mRecycler.findViewById(R.id.recycler_view);
        zoneRecycler = (RecyclerView) mRecycler.findViewById(R.id.zoneRecycler);
        categoryRecycler = (RecyclerView) mRecycler.findViewById(R.id.categoryRecyclerSearch);
        sortRecycler = (RecyclerView) mRecycler.findViewById(R.id.sortRecycler);

        Toolbar toolbar = (Toolbar) mRecycler.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        findStoresByPage("", 0, null, categoryId, sortId);

        //fab=(FloatingActionButton)mRecycler.findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), TransactionActivity.class);
                getActivity().startActivity(myIntent);
            }
        });*/
        //initCollapsingToolbar();

//        floatingSearchView = (FloatingSearchView) mRecycler.findViewById(R.id.floating_search_view);
//        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
//            @Override
//            public void onSearchTextChanged(String oldQuery, final String newQuery) {
//                //if(newQuery.length()>0){
//                //Toast.makeText(getActivity(), newQuery, Toast.LENGTH_LONG).show();
//
//                newquery = newQuery;
//                PageNumber = 0;
//                adapter.emptyList();
//                if (newQuery.length() >= 2) {
//                    findStoresByPage(newQuery, PageNumber);
//                } else if (newQuery.length() == 0) {
//                    findStoresByPage(newQuery, PageNumber);
//                }
//            }
//        });

        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                query = s.toString();
                newquery = query;
                PageNumber = 0;
                adapter.emptyList();
                if (query.length() >= 2) {
                    findStoresByPage(query, PageNumber, searchableZoneList, categoryId, sortId);
                } else if (query.length() == 0) {
                    findStoresByPage(query, PageNumber, searchableZoneList, categoryId, sortId);
                }

            }
        });

        albumList = new ArrayList<>();
        adapter = new StoreSearchAdapter_v2(getActivity(), albumList, itemTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new ListPaddingDecoration());
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            // for this tutorial, this is the ONLY method that we need, ignore the rest
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) {
                            findStoresByPage(newquery, PageNumber, searchableZoneList, categoryId, sortId);
                        }
                    }

                }
            }
        });

        zoneList = new ArrayList<>();
        zoneAdapter = new ZoneAdapter(getContext(), zoneList, zoneTouchListener);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        zoneRecycler.setLayoutManager(layoutManager);
        zoneRecycler.setItemAnimator(new DefaultItemAnimator());
        zoneRecycler.setAdapter(zoneAdapter);

        sortTypesList = new ArrayList<>();
        sortAdapter = new SortAdapter(getContext(), sortTypesList, sortTouchListener);
        LinearLayoutManager layoutManagerSort
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        sortRecycler.setLayoutManager(layoutManagerSort);
        sortRecycler.setItemAnimator(new DefaultItemAnimator());
        sortRecycler.setAdapter(sortAdapter);

        getZones();
        getCategories();
        getSortTypes();

        ZoneSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to 9
                List<ZoneModel> list = zoneList;
//                if (sortedZoneList.size() != 0) {
//                    list.clear();
//                    list = sortedZoneList;
//                }
                List<ZoneModel> searchedList = searchZoneList(list, s.toString());
                zoneAdapter.updateList(searchedList);
                zoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, categoryTouchListener);
        LinearLayoutManager layoutManagerCategory
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecycler.setLayoutManager(layoutManagerCategory);
        categoryRecycler.setItemAnimator(new DefaultItemAnimator());
        categoryRecycler.setAdapter(categoryAdapter);

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (categoryCard.getVisibility() == View.GONE) {
//                    if (!isSorted) {
//                        updateZoneList();
//                        isSorted = true;
//                    }
                    showCategoryCard();
                } else if (categoryCard.getVisibility() == View.VISIBLE) {
//                    if (!isSorted) {
////                        updateZoneList();
//                        isSorted = true;
//                    }
                    hideCategoryCard();
                }

            }
        });

        categoryTouchListener = new main_fragment.CategoryTouchListener() {
            @Override
            public void onCategoryTap(View v, int position) {
//                categoryId = categoryList.get(position).id;
//                PageNumber = 0;
//                adapter.emptyList();
//                findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
//                categoryButton.setText(categoryList.get(position).name);
//                categoryCard.setVisibility(View.GONE);

                if (categoryId == null) {
                    categoryId = categoryList.get(position).id;
                    PageNumber = 0;
                    adapter.emptyList();
                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                    categoryAdapter.selectItem(position);
                    categoryButton.setText(categoryList.get(position).name);
                    selectedCategory = position;
                } else if (position == selectedCategory) {
                    categoryId = null;
                    PageNumber = 0;
                    adapter.emptyList();
                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                    categoryAdapter.deselectItem(position);
                    categoryButton.setText("انتخاب دسته بندی");
                } else if (categoryId != null) {
                    categoryId = categoryList.get(position).id;
                    PageNumber = 0;
                    adapter.emptyList();
                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                    categoryAdapter.selectItem(position);
                    categoryAdapter.deselectItem(selectedCategory);
                    categoryButton.setText(categoryList.get(position).name);
                    selectedCategory = position;
                }

                hideCategoryCard();

            }
        };

        //prepareAlbums();
        findStoresByPage("", 0, searchableZoneList, categoryId, sortId);

        return mRecycler;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void followStore(Store s) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        final SharedPreferences settings = getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = settings.getString(TOKEN, null);

        Call<ApiResponse> call =
                providerApiInterface.followStore("bearer " + authToken, s.storeId);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int code = response.code();
                if (code == 200) {
                    ApiResponse ar = response.body();
                    Utility.showSnackbar(layout, ar.getMessage(), Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    private void getOrderListFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<List<Store>> call =
                providerApiInterface.getStoreFromServer("bearer " + authToken);


        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    albumList = response.body();
                    //adapter.notifyDataSetChanged();

                    adapter = new StoreSearchAdapter_v2(getActivity(), albumList, itemTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                    recyclerView.setAdapter(adapter);


                    //prepareAlbums();

                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                hideProgreeBar();
            }
        });

    }

//    private void findStores(String storeQuery) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(Constants.HTTP.BASE_URL)
//                .build();
//        StoreInterface providerApiInterface =
//                retrofit.create(StoreInterface.class);
//
//        SharedPreferences prefs =
//                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
//        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");
//
//        showProgreeBar();
//        if (!searchInProgress) {
//            searchInProgress = true;
//            Call<List<Store>> call =
//                    providerApiInterface.FindStore("bearer " + authToken, storeQuery);
//
//            call.enqueue(new Callback<List<Store>>() {
//                @Override
//                public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
//                    hideProgreeBar();
//                    searchInProgress = false;
//                    int code = response.code();
//                    if (code == 200) {
//                        albumList = response.body();
//                        //adapter.notifyDataSetChanged();
//
//                        adapter = new StoreSearchAdapter_v2(getActivity(), albumList, itemTouchListener);
//                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//                    ordersList.setLayoutManager(mLayoutManager);*/
//                        recyclerView.setAdapter(adapter);
//
//
//                        //prepareAlbums();
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Store>> call, Throwable t) {
//                    searchInProgress = false;
//                    //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
//                    hideProgreeBar();
//                    Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);
//
//                }
//            });
//        }
//    }

    Boolean isFirst=true;

    public void findStoresByPage(String storeQuery, int pageNumber, @Nullable List<Long> zones, @Nullable Long categoryId, @Nullable Short sortType) {

        showProgreeBar();

        itShouldLoadMore = false;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        if (!searchInProgress) {
            searchInProgress = true;
            Call<List<Store>> call =
                    providerApiInterface.FindStoreByPage("bearer " + authToken, storeQuery, pageNumber, zones, categoryId, sortType,false);

            call.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                    hideProgreeBar();
                    searchInProgress = false;
                    int code = response.code();
                    if (code == 200) {

                        hideProgreeBar();

                        itShouldLoadMore = true;

                        if (response.body().size()<=9) {
                            adapter.addItem(makeSpace());
                            isFirst=false;
                        }
                        adapter.addList(response.body());

                        PageNumber++;

//                        albumList = response.body();
//                        //adapter.notifyDataSetChanged();
//
//                        adapter = new StoreSearchAdapter(getActivity(), albumList, itemTouchListener);
//                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//                    ordersList.setLayoutManager(mLayoutManager);*/
//                        recyclerView.setAdapter(adapter);


                        //prepareAlbums();

                    }
                }

                @Override
                public void onFailure(Call<List<Store>> call, Throwable t) {
                    searchInProgress = false;
                    //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                    hideProgreeBar();
                    Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                    itShouldLoadMore = true;

                }
            });
        }
    }

    public Store makeSpace() {
        Store s = new Store();
        s.isSpace = true;
        return s;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

        public void onFollowTap(int position);
    }

    public interface ZoneTouchListener {
        public void onZoneTap(View v, int position);
    }

    public interface SortTouchListener {
        public void onSortTap(View v, int position);
    }

    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void getZones() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<List<ZoneModel>> call =
                providerApiInterface.getAllActiveZones("bearer " + authToken, 1);

        call.enqueue(new Callback<List<ZoneModel>>() {
            @Override
            public void onResponse(Call<List<ZoneModel>> call, Response<List<ZoneModel>> response) {
                int code = response.code();
                if (code == 200) {
                    zoneAdapter = new ZoneAdapter(getContext(), response.body(), zoneTouchListener);
                    zoneList = response.body();
                    zoneRecycler.setAdapter(zoneAdapter);
                    sortedZoneList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<ZoneModel>> call, Throwable t) {

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
                    categoryList = response.body();
                    categoryAdapter = new CategoryAdapter(getContext(), categoryList, categoryTouchListener);
                    categoryRecycler.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {

            }
        });


    }

    private void getSortTypes() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<List<SortType>> call =
                providerApiInterface.getSortItems("bearer " + authToken);

        call.enqueue(new Callback<List<SortType>>() {
            @Override
            public void onResponse(Call<List<SortType>> call, Response<List<SortType>> response) {
                int code = response.code();
                if (code == 200) {
                    sortAdapter = new SortAdapter(getContext(), response.body(), sortTouchListener);
                    sortTypesList = response.body();
                    sortRecycler.setAdapter(sortAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<SortType>> call, Throwable t) {

            }
        });

    }

    private void showZoneCard() {
//        hideSortTypes();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                categoryCard.setVisibility(View.GONE);
                zoneCard.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(600)
                        .playOn(zoneCard);
            }
        }, 300);

    }

    private void hideZoneCard() {
//        showSortTypes();
        YoYo.with(Techniques.SlideOutUp)
                .duration(600)
                .playOn(zoneCard);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                zoneCard.setVisibility(View.GONE);
            }
        }, 300);
    }

    private void showCategoryCard() {
//        hideSortTypes();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                zoneCard.setVisibility(View.GONE);
                categoryCard.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(600)
                        .playOn(categoryCard);
            }
        }, 200);


    }

    private void hideCategoryCard() {
//        showSortTypes();
        YoYo.with(Techniques.SlideOutUp)
                .duration(600)
                .playOn(categoryCard);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                categoryCard.setVisibility(View.GONE);
            }
        }, 300);
    }

    private void showSortTypes() {
        sortRecycler.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(600)
                .playOn(sortRecycler);
    }

    private void hideSortTypes() {
        YoYo.with(Techniques.SlideOutDown)
                .duration(600)
                .playOn(sortRecycler);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                sortRecycler.setVisibility(View.GONE);
            }
        }, 300);
    }

    private void updateZoneList() {
        zoneAdapter.updateList(sortZoneList(zoneList));
    }

    private List<ZoneModel> sortZoneList(List<ZoneModel> list) {

        List<ZoneModel> oldList = list;
        List<ZoneModel> checkedList = new ArrayList<>();
        List<ZoneModel> nonCheckedList = new ArrayList<>();

        for (ZoneModel zone : oldList) {
            if (zone.isChecked)
                checkedList.add(zone);
            else if (!zone.isChecked)
                nonCheckedList.add(zone);
        }

        List<ZoneModel> sortedList = new ArrayList<>();
        sortedList.addAll(checkedList);
        sortedList.addAll(nonCheckedList);
//        sortedList.addAll(extrasList);

        isSorted = true;

        sortedZoneList = sortedList;
        return sortedList;
    }

    private List<ZoneModel> searchZoneList(List<ZoneModel> list, String query) {

        List<ZoneModel> rawList = list;
        List<ZoneModel> searchedList = new ArrayList<>();

        if (!query.matches(""))
            for (ZoneModel zone : rawList) {
                if (zone.name.startsWith(query) || zone.name.endsWith(query) || zone.name.matches(query))
                    searchedList.add(zone);
            }
        else
            searchedList = rawList;

        return searchedList;
    }

}

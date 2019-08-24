package ir.woope.woopeapp.ui.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.CategoryAdapter;
import ir.woope.woopeapp.adapters.EarnMoneyAdapter;
import ir.woope.woopeapp.adapters.SortAdapter;
import ir.woope.woopeapp.adapters.StoreSearchAdapter_v2;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.SortType;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.search_fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.OPEN_MAIN_ACTIVITY;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class EarnMoneyActivity extends AppCompatActivity {

    private List<Store> albumList;
    private RecyclerView recyclerView;
    public EarnMoneyAdapter adapter;
    search_fragment.ItemTouchListener itemTouchListener;
    String query;
    boolean searchInProgress = false;
    private boolean itShouldLoadMore = true;
    int PageNumber = 0;
    String newquery;

    @BindView(R.id.progressBar_earnMoney)
    RelativeLayout progressBar;

    @BindView(R.id.searchEditText)
    EditText searchText;
    @BindView(R.id.searchIcon)
    ImageView searchIcon;
    @BindView(R.id.earnMoneyTitle)
    TextView title;
    @BindView(R.id.sort_layout)
    LinearLayout sortLayout;
    @BindView(R.id.filter_layout)
    LinearLayout filterLayout;
    @BindView(R.id.selected_cat)
    TextView selectedCat;
    @BindView(R.id.selected_sort)
    TextView selectedSort;
    @BindView(R.id.backBtn)
    ImageView back;
    @BindView(R.id.sort_text)
    TextView sortText;
    @BindView(R.id.filterText)
    TextView filterText;
    List<Long> searchableZoneList = new ArrayList<>();
    public Long categoryId = null;
    public Short sortId = null;

    Context context;

    AlertDialog.Builder categoryDialog;
    AlertDialog.Builder sortDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_money);

        sortDialog = new AlertDialog.Builder(this);
        categoryDialog = new AlertDialog.Builder(this);

        context = this;

        ButterKnife.bind(this);

        hideSearch();
        showTitle();

        recyclerView = (RecyclerView) findViewById(R.id.earnMoney_recycler_view);

        findStoresByPage("", 0, null, categoryId, sortId);

        searchIcon.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                if (searchText.getVisibility() == View.GONE)
                    showSearch();
                else
                    showTitle();

            }
        });
        sortLayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                sortDialog.show();

            }
        });
        filterLayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                categoryDialog.show();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                finish();

            }
        });
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

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (IsLogedIn()) {
//                    if (albumList.get().describeCountDiscountCode == null)
//                        new GuideView.Builder(context)
//                                .setTitle(" ")
//                                .setContentText("برای این فروشگاه طرح اشتراک گذاری فعال نگردیده است")
//                                .setGravity(Gravity.auto) //optional
//                                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
//                                .setTargetView(view)
//                                .setContentTextSize(12)//optional
//                                .setTitleTextSize(14)//optional
//                                .build()
//                                .show();
//                    else
//                        shareStore(store.storeId);
//
//                } else {
//                    Intent goto_login = new Intent(EarnMoneyActivity.this,
//                            SplashSelectActivity.class);
//                    goto_login.putExtra(OPEN_MAIN_ACTIVITY, false);
//                    goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    //finish();
//                    startActivity(goto_login);
//                }
//
//            }
//        });

        itemTouchListener = new search_fragment.ItemTouchListener() {

            @Override
            public void onCardViewTap(View view, int position) {
                getStore(albumList.get(position).storeId, view);
//                if (IsLogedIn()) {
//                    if (result == null)
//                        new GuideView.Builder(context)
//                                .setTitle(" ")
//                                .setContentText("برای این فروشگاه طرح اشتراک گذاری فعال نگردیده است")
//                                .setGravity(Gravity.auto) //optional
//                                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
//                                .setTargetView(view)
//                                .setContentTextSize(12)//optional
//                                .setTitleTextSize(14)//optional
//                                .build()
//                                .show();
//                    else
//                        shareStore(albumList.get(position).storeId);
//
//                } else {
//                    Intent goto_login = new Intent(EarnMoneyActivity.this,
//                            SplashSelectActivity.class);
//                    goto_login.putExtra(OPEN_MAIN_ACTIVITY, false);
//                    goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    //finish();
//                    startActivity(goto_login);
//                }
            }

            @Override
            public void onFollowTap(int position) {

            }
        };

        albumList = new ArrayList<>();
        adapter = new EarnMoneyAdapter(this, albumList, itemTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
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

        getCategories();
        getSortTypes();
        findStoresByPage("", 0, searchableZoneList, categoryId, sortId);

    }

    private boolean IsLogedIn() {
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String tokenString = prefs.getString(TOKEN, null);
        if (tokenString == null) {
            return false;
        } else {
            return true;
        }
    }

    private void shareStore(long branchId) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<ApiResponse> call =
                providerApiInterface.shareStore("bearer " + authToken, branchId);

        showProgreeBar();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                hideProgreeBar();
                if (response.body() != null) {
                    int code = response.body().status;
                    if (code == 101)
                        shareText(response.body().getMessage());
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });
    }

    private void shareText(String text) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");// Plain format text
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "به اشتراک گذاری با : "));

    }

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
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        if (!searchInProgress) {
            searchInProgress = true;
            Call<List<Store>> call =
                    providerApiInterface.FindStoreByPage("bearer " + authToken, storeQuery, pageNumber, zones, categoryId, sortType, true);

            call.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
//                    hideProgreeBar();
                    searchInProgress = false;
                    int code = response.code();
                    if (code == 200) {

                        hideProgreeBar();

                        itShouldLoadMore = true;

//                        if (response.body().size()<=9) {
//                            adapter.addItem(makeSpace());
//                            isFirst=false;
//                        }
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
//                    Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                    itShouldLoadMore = true;

                }
            });
        }
    }

    String result;

    private void getStore(long storeId, View v) {
        result = null;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<Store> call =
                providerApiInterface.getStore("bearer " + authToken, storeId);

        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    if (IsLogedIn()) {
                        if (response.body().describeCountDiscountCode == null)
                            new GuideView.Builder(context)
                                    .setTitle(" ")
                                    .setContentText("برای این فروشگاه طرح اشتراک گذاری فعال نگردیده است")
                                    .setGravity(Gravity.auto) //optional
                                    .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                                    .setTargetView(v)
                                    .setContentTextSize(12)//optional
                                    .setTitleTextSize(14)//optional
                                    .build()
                                    .show();
                        else
                            shareStore(response.body().storeId);

                    } else {
                        Intent goto_login = new Intent(EarnMoneyActivity.this,
                                SplashSelectActivity.class);
                        goto_login.putExtra(OPEN_MAIN_ACTIVITY, false);
                        goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //finish();
                        startActivity(goto_login);
                    }
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                result = null;
                hideProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });

    }

//    public interface ItemTouchListener {
//        public void onCardViewTap(View view, int position);
//
//        public void onFollowTap(int position);
//    }

    private void showTitle() {
        hideSearch();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
//                .setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(600)
                        .playOn(title);
            }
        }, 200);


    }

    private void hideTitle() {
//        showSortTypes();
        YoYo.with(Techniques.SlideOutUp)
                .duration(600)
                .playOn(title);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                title.setVisibility(View.GONE);
            }
        }, 300);
    }

    private void showSearch() {
        hideTitle();
        searchText.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(600)
                .playOn(searchText);
    }

    private void hideSearch() {
        YoYo.with(Techniques.SlideOutDown)
                .duration(600)
                .playOn(searchText);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                searchText.setVisibility(View.GONE);
            }
        }, 300);
    }

    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);

    }

    private List<CategoryModel> categoryList = new ArrayList<>();
    int catWhich = 0;
    Boolean CatFirst=true;
    private void getCategories() {
        showProgreeBar();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<List<CategoryModel>> call =
                providerApiInterface.getCategories("bearer " + authToken);

        call.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                int code = response.code();
                if (code == 200) {
                    hideProgreeBar();
                    categoryList = response.body();
                    if(CatFirst){
                    selectedCat.setText(categoryList.get(0).name);
                    CatFirst=false;}
                    categoryId = categoryList.get(0).id;
                    List<CategoryModel> list = response.body();
                    ArrayList<String> arrList = new ArrayList<>();
                    for (CategoryModel e : list) {
                        arrList.add(e.name);
                    }
                    String[] item = arrList.toArray(new String[arrList.size()]);

                    categoryDialog.setTitle("انتخاب دسته بندی")
                            .setSingleChoiceItems(new ArrayAdapter<String>(context,
                                            R.layout.rtl_list_item,
                                            R.id.text, item),
                                    catWhich,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            categoryId = null;
                                            int catId;
                                            catWhich = which;
                                            for (CategoryModel s : categoryList) {
                                                if (s.name.equals(item[which])) {
                                                    selectedCat.setText(item[which]);
                                                    categoryId = s.id;
                                                    adapter.emptyList();
                                                    PageNumber = 0;
                                                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                                                    dialog.dismiss();
                                                    getCategories();
                                                    break;
                                                }
                                            }
                                        }
                                    });

                    categoryDialog.create();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                hideProgreeBar();
            }
        });


    }

    private List<SortType> sortList = new ArrayList<>();
    int sortWhich = 0;
    Boolean SortFirst = true;

    private void getSortTypes() {
        showProgreeBar();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<List<SortType>> call =
                providerApiInterface.getSortItems("bearer " + authToken);

        call.enqueue(new Callback<List<SortType>>() {
            @Override
            public void onResponse(Call<List<SortType>> call, Response<List<SortType>> response) {
                int code = response.code();
                if (code == 200) {
                    hideProgreeBar();
                    sortList = response.body();
                    if (SortFirst) {
                        selectedSort.setText(sortList.get(0).name);
                        SortFirst = false;
                    }
                    sortId = sortList.get(0).id;
                    List<SortType> list = response.body();
                    ArrayList<String> arrList = new ArrayList<>();
                    for (SortType e : list) {
                        arrList.add(e.name);
                    }
                    String[] item = arrList.toArray(new String[arrList.size()]);
                    sortDialog.setTitle("مرتب سازی بر اساس")
                            .setSingleChoiceItems(new ArrayAdapter<String>(context,
                                            R.layout.rtl_list_item,
                                            R.id.text, item),
                                    sortWhich,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sortId = null;
                                            int catId;
                                            sortWhich = which;
                                            for (SortType s : sortList) {
                                                if (s.name.equals(item[which])) {
                                                    sortId = s.id;
                                                    adapter.emptyList();
                                                    PageNumber = 0;
                                                    findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                                                    selectedSort.setText(item[which]);
                                                    dialog.dismiss();
                                                    getSortTypes();
                                                    break;
                                                }
                                            }
                                        }
                                    });

                    sortDialog.create();
                }
            }

            @Override
            public void onFailure(Call<List<SortType>> call, Throwable t) {
                hideProgreeBar();
            }
        });

    }

    @Override
    public void onBackPressed() {

        finish();

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}

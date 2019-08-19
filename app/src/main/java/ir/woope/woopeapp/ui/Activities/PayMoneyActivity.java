package ir.woope.woopeapp.ui.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.PayMoneyAdapter;
import ir.woope.woopeapp.adapters.StoreSearchAdapter_v2;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PAY_LIST_ITEM;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class PayMoneyActivity extends AppCompatActivity {

    private List<Store> albumList;
    private RecyclerView recyclerView;
    public PayMoneyAdapter adapter;
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
        setContentView(R.layout.activity_pay_money);

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
                    isFirst=true;
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
        adapter = new PayMoneyAdapter(this, albumList, itemTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
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

    Boolean isFirst = true;

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
                int code = response.body().status;
                if (code == 101)
                    shareText(response.body().getMessage());

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
                    providerApiInterface.FindStoreByPage("bearer " + authToken, storeQuery, pageNumber, zones, categoryId, sortType, false);

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

                        if (isFirst) {
                            findStoresByPage("", PageNumber, searchableZoneList, categoryId, sortId);
                            isFirst = false;
                        }

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
                        store = response.body();
                        showPayDialog();
                    } else {
                        Intent goto_login = new Intent(PayMoneyActivity.this,
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

    ImageView close_btn;
    TextView confirm_pay;
    AVLoadingIndicatorView dialogProgressBar;
    EditText discountCode;
    TextView invalidDiscountCode;

    private void showPayDialog() {
        final Dialog dialog = new Dialog(PayMoneyActivity.this);
        dialog.setContentView(R.layout.pay_dialog);
        dialog.setTitle("");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        close_btn = (ImageView) dialog.findViewById(R.id.close_btn);
        confirm_pay = (TextView) dialog.findViewById(R.id.confirm_pay);
        dialogProgressBar = dialog.findViewById(R.id.progressBar_payDialog);
        discountCode = dialog.findViewById(R.id.discountCode);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        final EditText amount = (EditText) dialog.findViewById(R.id.amount);
        final TextView invalidPrice = (TextView) dialog.findViewById(R.id.pay_dialog_priceError);
        invalidDiscountCode = dialog.findViewById(R.id.pay_dialog_invalidDiscountCode);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalPrice > 0 && totalPrice <= 10000000) {
                    try {
                        if (discountCode.getText().toString().matches("")) {
                            goToPaying(null, null, 0);
                        } else if (!discountCode.getText().toString().matches("")) {
                            checkCode(discountCode.getText().toString(), store.storeId, totalPrice);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }
                } else if (totalPrice > 10000000) {
                    try {
                        invalidPrice.setText(R.string.overflowed_price);
                        invalidPrice.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }
                } else {
                    try {
                        invalidPrice.setText(R.string.invalid_price);
                        invalidPrice.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }

                }
            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amount.removeTextChangedListener(this);

                try {

                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(Utility.arabicToDecimal(originalString));
                    totalPrice = longval;
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                amount.addTextChangedListener(this);
            }
        });
        dialog.show();

    }

    Store store;
    Profile profile = null;
    long totalPrice = 0;

    public void getProfileFromServer() {

        showProgreeBar();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        ProfileInterface providerApiInterface =
                retrofit.create(ProfileInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");
        Call<Profile> call =
                providerApiInterface.getProfileFromServer("bearer " + authToken);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {

                    profile = response.body();
                    //profile=user.getMessage();
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(profile);
                    prefsEditor.putString(PROFILE, json);
                    prefsEditor.apply();

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                hideProgreeBar();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), "خطا در دریافت اطلاعات حساب کاربری", Snackbar.LENGTH_LONG);
            }
        });

    }

    private void checkCode(final String Code, long branchId, long amount) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");
        showDialogProgreeBar();
        Call<ApiResponse> call =
                providerApiInterface.checkDiscountCode("bearer " + authToken, Code, branchId, amount);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideDialogProgreeBar();
                int code = response.body().status;
                if (code == 101)
                    goToPaying(response.body().getMessage(), Code, Integer.valueOf(response.body().getMessage()));
                else if (code == 202)
                    invalidDiscountCode.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideDialogProgreeBar();
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                Utility.showSnackbar(findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });
    }

    public void showDialogProgreeBar() {
        dialogProgressBar.smoothToShow();
        confirm_pay.setText("");
    }

    public void hideDialogProgreeBar() {
        dialogProgressBar.hide();
        confirm_pay.setText(R.string.accept);
    }

    public void goToPaying(@org.jetbrains.annotations.Nullable String extraWoope, @org.jetbrains.annotations.Nullable String discountCode, @org.jetbrains.annotations.Nullable int giftPrice) {

        Intent myIntent = new Intent(this, PayActivity.class);
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(STORE, store);
        PayListModel model = new PayListModel();
        model.storeName = store.storeName;
        model.branchId = store.storeId;
        model.totalPrice = totalPrice;
        model.logoSrc = store.logoSrc;
        model.categoryId = store.categoryId;
        model.switchWoope = false;
        model.switchCredit = false;
        model.woopeThreshold = store.woopeThreshold;
        model.basePrice = store.basePrice;
        model.returnPoint = store.returnPoint;
        if (extraWoope == null)
            model.extraDiscountWoope = "0";
        else if (extraWoope.equals(""))
            model.extraDiscountWoope = "0";
        else
            model.extraDiscountWoope = extraWoope;
        model.discountCode = discountCode;
        myIntent.putExtra(PAY_LIST_ITEM, model);
        myIntent.putExtra("LogoUrl", store.logoSrc);
        myIntent.putExtra("giftPrice", giftPrice);
//        Map<String, String> attributes = new HashMap<>();
//        attributes.put("store_name", model.storeName);
//
//        Map<String, Double> metrics = new HashMap<>();
//        metrics.put("price", Double.valueOf(model.totalPrice));
//
//        Metrix.getInstance().newEvent("hogju", attributes, metrics);

        this.startActivity(myIntent);
        this.finish();
    }

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
                    selectedCat.setText(categoryList.get(0).name);
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
                    selectedSort.setText(sortList.get(0).name);
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
    public void onResume() {
        super.onResume();
        if (IsLogedIn() && getUserProfile() == null)
            getProfileFromServer();
    }

    public Profile getUserProfile() {
        Gson gson = new Gson();
        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String profileString = prefs.getString(Constants.GlobalConstants.PROFILE, null);
        if (profileString != null) {
            profile = (Profile) gson.fromJson(profileString, Profile.class);
            return profile;
        } else {
            return null;
        }
    }

}

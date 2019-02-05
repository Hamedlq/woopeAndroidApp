package ir.woope.woopeapp.ui.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.ViewTarget;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;

import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.ListPaddingDecoration;

import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.MainListModel;
import ir.woope.woopeapp.models.MallModel;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.GiftActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.PayActivity;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import ir.woope.woopeapp.ui.Activities.StoreListActivity;
import ir.woope.woopeapp.ui.Activities.TransactionActivity;
import me.toptas.fancyshowcase.FancyShowCaseView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

/**
 * Created by Hamed on 6/11/2018.
 */

public class home_fragment extends Fragment {

    private View mRecycler;
    private List<Store> albumList;
    String ALBUM_FRAGMENT = "AlbumFragment";
    String authToken;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private StoresAdapter adapter;
    ItemTouchListener itemTouchListener;
    FloatingActionButton fab;
    ProgressBar progressBar;

    Toolbar toolbar;

    int PageNumber = 0, cPage = 0;
    List<Store> stores;

    boolean scrollDirection = false; //up=false,down=true

    private boolean itShouldLoadMore = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        stores = new ArrayList<>();

        getActivity().setTitle("");
        mRecycler = inflater.inflate(R.layout.fragment_home, null);
        setHasOptionsMenu(true);
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

                //open activity
                //Toast.makeText(getActivity(), "شد", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAdvTap(View view, int position) {
                Store s = albumList.get(position);
                if (s.isAdvertise) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s.website));
                    startActivity(browserIntent);
                }

            }

            @Override
            public void onFollowTap(View view, int position) {
                Store s = albumList.get(position);
                followStore(s);
            }

        };

        progressBar = (ProgressBar) mRecycler.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) mRecycler.findViewById(R.id.recycler_view);


        toolbar = (Toolbar) mRecycler.findViewById(R.id.home_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
/*
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_card_giftcard);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

        //toolbar.setTitle(R.string.app_name);

        albumList = new ArrayList<>();
        adapter = new StoresAdapter(getActivity(), albumList, itemTouchListener, this);
        LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(getActivity());
        mLayoutManager = linearlayoutmanager;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ListPaddingDecoration());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getStoresByPage(PageNumber);

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
                            getStoresByPage(PageNumber);
                        }
                    }

                }
            }
        });

//        getOrderListFromServer();


        return mRecycler;
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    /*public void goToPaying(Store theStore) {

        Intent myIntent = new Intent(getActivity(), PayActivity.class);
        Profile profile=((MainActivity)getActivity()).getUserProfile();
        myIntent.putExtra(PREF_PROFILE, profile);
        //myIntent.putExtra(STORE, store);
        PayListModel model=new PayListModel();
        model.storeName=theStore.storeName;
        model.branchId=theStore.storeId;
        model.totalPrice=totalPrice;
        model.logoSrc=store.logoSrc;
        model.switchWoope=false;
        model.switchCredit=false;
        model.basePrice=store.basePrice;
        model.returnPoint=store.returnPoint;
        myIntent.putExtra(PAY_LIST_ITEM, model);
        this.startActivity(myIntent);
        this.finish();
    }*/

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
                    Toast.makeText(getActivity(), ar.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "خطا در تغییر علاقمندی‌ها", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_store:
                getActivity().finish();
            default:
                break;
        }
        return true;
    }

//    private void getOrderListFromServer() {
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
//        Call<List<Store>> call =
//                providerApiInterface.getStoreFromServer("bearer " + authToken);
//
//
//        call.enqueue(new Callback<List<Store>>() {
//            @Override
//            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
//                hideProgreeBar();
//                int code = response.code();
//                if (code == 200) {
//
//                    stores.addAll(response.body());
//                    //adapter.notifyDataSetChanged();
//
//                    adapter = new StoresAdapter(getActivity(), stores, itemTouchListener, home_fragment.this);
//                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//                    ordersList.setLayoutManager(mLayoutManager);*/
//                    recyclerView.setAdapter(adapter);
//                    //prepareAlbums();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Store>> call, Throwable t) {
//                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
//                hideProgreeBar();
//            }
//        });
//
//    }

    int size;

    private int getStoresByPage(final int pageNumber) {

        showProgreeBar();

        itShouldLoadMore = false;
        MainListModel model=((StoreListActivity)getActivity()).getMainListModel();
        Integer mallModelId=((StoreListActivity)getActivity()).getMallModel();
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
                providerApiInterface.GetStoresFilter("bearer " + authToken, pageNumber,model.listOrder,9,mallModelId);

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {

                    hideProgreeBar();

                    itShouldLoadMore = true;

                    if (response.body().size() > 1) {

                        adapter.addItem(response.body());

                        PageNumber++;

                    }
                    // interfaceInfinite.onSuccess(response);

//                    adapter.addItem(response.body());
//
//                    size = response.body().size();

//                    for(Store s: response.body()) {
//                        stores.add(s);
//                    }

//                    for(int i=0;i<=11;i++)
//                    {
//                        stores.add(response.body().get(i));
//                    }

//                    stores.addAll(response.body());
                    //adapter.notifyDataSetChanged();

//                    adapter = new StoresAdapter(getActivity(), stores, itemTouchListener, home_fragment.this);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
//                    recyclerView.setAdapter(adapter);
                    //prepareAlbums();

                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                itShouldLoadMore = true;
                size = 0;
            }
        });

        return size;

    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

        public void onAdvTap(View view, int position);

        public void onFollowTap(View view, int position);
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    View v;

    public void showHint() {


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                v = mLayoutManager.getChildAt(0);
//
//            }
//        },50);
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(0);
//
//
//        }
//        },100);

        final TapTargetSequence sequence = new TapTargetSequence(getActivity())
                .targets(
                        // Likewise, this tap target will target the search button
                        TapTarget.forToolbarMenuItem(toolbar, R.id.nav_store, "کد هدیه", "اینجا میتونید کد هدیه تون رو به ووپ تبدیل کنید")
                                // All options below are optional
                                .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(14)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)// Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60)

                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {

                        SharedPreferences prefs =
                                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("HOMEFIRSTRUN", false);
                        editor.commit();

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        final AlertDialog dialog = new AlertDialog.Builder(PayActivity.this)
//                                .setTitle("Uh oh")
//                                .setMessage("You canceled the seque.setPositiveButton("Oops", null).show();nce")
//
//                        TapTargetView.showFor(dialog,
//                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
//                                        .cancelable(false)
//                                        .tintTarget(false), new TapTargetView.Listener() {
//                                    @Override
//                                    public void onTargetClick(TapTargetView view) {
//                                        super.onTargetClick(view);
//                                        dialog.dismiss();
//                                    }
//                                });
                    }
                });

        sequence.start();

    }


}

package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.request.target.ViewTarget;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.interfaces.TransactionInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.GiftActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.PayActivity;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import ir.woope.woopeapp.ui.Activities.TransactionActivity;
import me.toptas.fancyshowcase.FancyShowCaseView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
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
    private StoresAdapter adapter;
    ItemTouchListener itemTouchListener;
    FloatingActionButton fab;
    ProgressBar progressBar;

    GuideView s;

    Toolbar toolbar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

//        new MaterialTapTargetPrompt.Builder(this)
//                .setTarget(R.id.nav_store)
//                .setPrimaryText("لیست پرداخت")
//                .setSecondaryText("فاکتور اولیه ی خود را تکمیل کنید")
//                .show();

//        ((MainActivity)getActivity()).nav_store_showcase

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
                getActivity().startActivityForResult(myIntent,RELOAD_LIST);

                //open activity
                //Toast.makeText(getActivity(), "شد", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAdvTap(View view, int position) {
                Store s = albumList.get(position);
                if(s.isAdvertise){
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

        progressBar=(ProgressBar)mRecycler.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) mRecycler.findViewById(R.id.recycler_view);


        Toolbar toolbar = (Toolbar) mRecycler.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(R.drawable.ic_card_giftcard);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbar.setTitle(R.string.app_name);

        albumList = new ArrayList<>();
        adapter = new StoresAdapter(getActivity(), albumList,itemTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //prepareAlbums();

        getOrderListFromServer();

        try {
            Picasso.with(getActivity()).load(R.drawable.woope_blue).into((ImageView) mRecycler.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }



        return mRecycler;



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
                providerApiInterface.followStore("bearer "+authToken,s.storeId);


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
                Profile obj =((MainActivity)getActivity()).getUserProfile();
                Intent myIntent = new Intent(getActivity(), TransactionActivity.class);
                myIntent.putExtra(PREF_PROFILE, obj);
                getActivity().startActivity(myIntent);
                getActivity().overridePendingTransition(R.anim.slide_up,R.anim.no_change);
                break;
            case android.R.id.home:
                Profile userobj =((MainActivity)getActivity()).getUserProfile();
                Intent giftIntent = new Intent(getActivity(), GiftActivity.class);
                giftIntent.putExtra(PREF_PROFILE, userobj);
                getActivity().startActivity(giftIntent);
                getActivity().overridePendingTransition(R.anim.slide_up,R.anim.no_change);
                break;
            default:
                break;
        }
        return true;
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
                providerApiInterface.getStoreFromServer("bearer "+authToken);


        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    albumList = response.body();
                    //adapter.notifyDataSetChanged();

                    adapter = new StoresAdapter(getActivity(),albumList, itemTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                    recyclerView.setAdapter(adapter);
                    //prepareAlbums();
                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });

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
        view.post(new Runnable() {
            @Override
            public void run() {

                SharedPreferences prefs =
                        getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                boolean isFirstRun = prefs.getBoolean("FIRSTRUN", true);
                if (isFirstRun)
                {
                    // Code to run once

                    showhint();

                }

            }
        });
    }

    public void showhint(){

        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(toolbar.findViewById(R.id.nav_store), "لیست پرداخت ها", "پیش فاکتور خود را تکمیل کنید")
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
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional

                        SharedPreferences prefs =
                                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("FIRSTRUN", false);
                        editor.commit();

                    }
                });

    }


}

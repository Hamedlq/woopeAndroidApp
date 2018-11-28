package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.StoreSearchAdapter;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import ir.woope.woopeapp.ui.Activities.TransactionActivity;
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
    private List<Store> albumList;
    String ALBUM_FRAGMENT = "AlbumFragment";
    String authToken;
    private RecyclerView recyclerView;
    private StoreSearchAdapter adapter;
    private FloatingSearchView floatingSearchView;
    ItemTouchListener itemTouchListener;
    FloatingActionButton fab;
    ProgressBar progressBar;
    boolean searchInProgress=false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_search, null);

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

            }

            @Override
            public void onFollowTap(int position) {
                Store s = albumList.get(position);
                followStore(s);
            }
        };

        progressBar=(ProgressBar)mRecycler.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) mRecycler.findViewById(R.id.recycler_view);

        Toolbar toolbar = (Toolbar) mRecycler.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //fab=(FloatingActionButton)mRecycler.findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), TransactionActivity.class);
                getActivity().startActivity(myIntent);
            }
        });*/
        //initCollapsingToolbar();

        floatingSearchView=(FloatingSearchView)mRecycler.findViewById(R.id.floating_search_view);
        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                //if(newQuery.length()>0){
                    //Toast.makeText(getActivity(), newQuery, Toast.LENGTH_LONG).show();
                    findStores(newQuery);
                //}
            }
        });

        albumList = new ArrayList<>();
        adapter = new StoreSearchAdapter(getActivity(), albumList,itemTouchListener);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //prepareAlbums();
        findStores("");
        //getOrderListFromServer();

        try {
            Picasso.with(getActivity()).load(R.drawable.woope_blue).into((ImageView) mRecycler.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    adapter = new StoreSearchAdapter(getActivity(),albumList, itemTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                    recyclerView.setAdapter(adapter);



                    //prepareAlbums();

                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
            }
        });

    }

    private void findStores(String storeQuery ) {
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
        if(!searchInProgress) {
            searchInProgress = true;
            Call<List<Store>> call =
                    providerApiInterface.FindStore("bearer "+authToken, storeQuery);


            call.enqueue(new Callback<List<Store>>() {
                @Override
                public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                    hideProgreeBar();
                    searchInProgress = false;
                    int code = response.code();
                    if (code == 200) {
                        albumList = response.body();
                        //adapter.notifyDataSetChanged();

                        adapter = new StoreSearchAdapter(getActivity(), albumList, itemTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                        recyclerView.setAdapter(adapter);


                        //prepareAlbums();

                    }
                }

                @Override
                public void onFailure(Call<List<Store>> call, Throwable t) {
                    searchInProgress = false;
                    //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                    hideProgreeBar();
                }
            });
        }
    }



    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
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

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);
        public void onFollowTap( int position);
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }


}

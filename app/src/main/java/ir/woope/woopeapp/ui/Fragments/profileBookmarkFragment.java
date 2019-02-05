package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.ProfileBookmarkAdapter;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.ListPaddingDecoration;
import ir.woope.woopeapp.helpers.Utility;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE_NAME;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

/**
 * Created by Hamed on 6/11/2018.
 */

public class profileBookmarkFragment extends Fragment {

    private View mRecycler;
    private List<Store> albumList;
    String ALBUM_FRAGMENT = "AlbumFragment";
    String authToken;
    private RecyclerView recyclerView;
    private ProfileBookmarkAdapter adapter;
    BookmarkTouchListener itemTouchListener;
    FloatingActionButton fab;
    ProgressBar progressBar;
    TextView noBookmark;

    View layout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noBookmark = getView().findViewById(R.id.txtNoBookmarkAvailable);

        layout = getView().findViewById(R.id.activity_profileBookmarkFragment);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        mRecycler = inflater.inflate(R.layout.fragment_bookmark, null);
        setHasOptionsMenu(true);
        itemTouchListener = new BookmarkTouchListener() {

            @Override
            public void onCardViewTap(int position) {
                Store s = albumList.get(position);
                final SharedPreferences prefs =
                        getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString(PROFILE, "");
                Profile obj = gson.fromJson(json, Profile.class);
                Intent myIntent = new Intent(getActivity(), StoreActivity.class);
                myIntent.putExtra(PREF_PROFILE, obj);
                myIntent.putExtra(STORE, s); //Optional parameters
                getActivity().startActivity(myIntent);


            }

            @Override
            public void onFollowTap(int position) {
                Store s = albumList.get(position);
                followStore(s);
            }
        };

        progressBar = (ProgressBar) mRecycler.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) mRecycler.findViewById(R.id.recycler_view);
        /*fab=(FloatingActionButton)mRecycler.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), TransactionActivity.class);
                getActivity().startActivity(myIntent);
            }
        });*/
        //initCollapsingToolbar();

        albumList = new ArrayList<>();
        adapter = new ProfileBookmarkAdapter(getActivity(), albumList, itemTouchListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ListPaddingDecoration());
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //prepareAlbums();

        getOrderListFromServer();


        return mRecycler;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                    if (albumList.size() == 0)
                        noBookmark.setVisibility(View.VISIBLE);
                    else if (albumList.size() >= 0)
                        noBookmark.setVisibility(View.GONE);
                    getOrderListFromServer();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_store:
                Intent myIntent = new Intent(getActivity(), TransactionActivity.class);
                getActivity().startActivity(myIntent);

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
                providerApiInterface.getFollowStore("bearer " + authToken);


        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {

                    albumList = response.body();


                    //adapter.notifyDataSetChanged();
                    adapter = new ProfileBookmarkAdapter(getActivity(), albumList, itemTouchListener);
                    /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    ordersList.setLayoutManager(mLayoutManager);*/
                    recyclerView.setAdapter(adapter);
                    //prepareAlbums();

                    if (albumList.size() == 0)
                        noBookmark.setVisibility(View.VISIBLE);
                    else if (albumList.size() >= 0)
                        noBookmark.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });

    }


    public interface BookmarkTouchListener {
        public void onCardViewTap(int position);

        public void onFollowTap(int position);
    }

    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }

}

package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.ProfileBookmarkAdapter;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.StoreInterface;
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

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_bookmark, null);
        setHasOptionsMenu(true);
        itemTouchListener = new BookmarkTouchListener() {

            @Override
            public void onCardViewTap( int position) {
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
        };

        progressBar=(ProgressBar)mRecycler.findViewById(R.id.progressBar);

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
        adapter = new ProfileBookmarkAdapter(getActivity(), albumList,itemTouchListener);
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
            Picasso.with(getActivity()).load(R.drawable.cover).into((ImageView) mRecycler.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }


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

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
/*    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout)mRecycler.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) mRecycler.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }*/

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};

        /*Store a = new Store("آدیداس","هر ۱۰۰ هزار تومان ۵ ووپ", 7, covers[0]);
        albumList.add(a);

        a = new Store("نایک","هر ۱۰ هزار تومان ۱ ووپ", 10, covers[1]);
        albumList.add(a);

        a = new Store("LC WAIKIKI","هر ۲۰ هزار تومان ۲ ووپ", 10, covers[2]);
        albumList.add(a);

        a = new Store("ecco","هر ۳۰ هزار تومان ۱۰ ووپ", 15, covers[3]);
        albumList.add(a);

        a = new Store("پرپروک","هر ۱۰۰ هزار تومان ۵ ووپ", 10, covers[4]);
        albumList.add(a);

        a = new Store("سیب۳۶۰","هر ۳۰ هزار تومان ۱۰ ووپ", 10, covers[5]);
        albumList.add(a);

        a = new Store("شیلا","هر ۵۰ هزار تومان ۷ ووپ", 5, covers[6]);
        albumList.add(a);

        a = new Store("پدرخوب","هر ۳۰ هزار تومان ۱۰ ووپ", 10, covers[7]);
        albumList.add(a);

        a = new Store("در ب در","هر ۱۰۰ هزار تومان ۵ ووپ", 15, covers[8]);
        albumList.add(a);

        a = new Store("آدیداس","هر ۸۰ هزار تومان ۵ ووپ", 15, covers[9]);
        albumList.add(a);*/

        adapter.notifyDataSetChanged();
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
                providerApiInterface.getFollowStore("bearer "+authToken);


        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    albumList = response.body();
                    //adapter.notifyDataSetChanged();

                    adapter = new ProfileBookmarkAdapter(getActivity(),albumList, itemTouchListener);
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

    public interface BookmarkTouchListener {
        public void onCardViewTap(int position);
    }


    public void showProgreeBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgreeBar() {
        progressBar.setVisibility(View.GONE);
    }


}

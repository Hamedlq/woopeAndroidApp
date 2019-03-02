package ir.woope.woopeapp.ui.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.StoreGalleryAdapter;
import ir.woope.woopeapp.adapters.StoreSearchAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ItemClickListener;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.ui.Activities.ProductActivity;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class storeGalleryFragment extends Fragment implements ItemClickListener {

    TextView noPhoto;
    RecyclerView recyclerView;
    StoreGalleryAdapter adapter;

    String authToken;

    private boolean itShouldLoadMore = true;
    boolean searchInProgress = false;

    private List<StoreGalleryItem> albumList;

    long branchId;

    int PageNumber = 0;

    String newquery;

    View layout;

    public storeGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = getView().findViewById(R.id.store_gallery_activity);

    }

    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click
        StoreGalleryItem product = albumList.get(position);
        Intent i = new Intent(getActivity(), ProductActivity.class);
        i.putExtra("product", product);
        startActivity(i);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_gallery, container, false);

        if (getActivity() instanceof StoreActivity) {
            branchId = ((StoreActivity) getActivity()).getBranchId();
        }

        noPhoto = rootView.findViewById(R.id.txt_store_gallery_noPhoto);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.storeGallery_recyclerView);

        albumList = new ArrayList<>();
        adapter = new StoreGalleryAdapter(getActivity(), albumList);
        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                            getStoreGallery(branchId, PageNumber);
                        }
                    }

                }
            }
        });

        //prepareAlbums();
        getStoreGallery(branchId, 0);

        return rootView;

    }

    private void getStoreGallery(long branchId, int pageNumber) {

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

        if (!searchInProgress) {
            searchInProgress = true;
            Call<List<StoreGalleryItem>> call =
                    providerApiInterface.getActiveBranchProduct("bearer " + authToken, null, branchId, pageNumber, 500);

            call.enqueue(new Callback<List<StoreGalleryItem>>() {
                @Override
                public void onResponse(Call<List<StoreGalleryItem>> call, Response<List<StoreGalleryItem>> response) {

                    searchInProgress = false;
                    int code = response.code();
                    if (code == 200) {

                        noPhoto.setVisibility(View.GONE);

                        itShouldLoadMore = true;

                        adapter.addItem(response.body());

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
                public void onFailure(Call<List<StoreGalleryItem>> call, Throwable t) {

                    searchInProgress = false;
                    //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                    Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                    itShouldLoadMore = true;

                }
            });
        }
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

}

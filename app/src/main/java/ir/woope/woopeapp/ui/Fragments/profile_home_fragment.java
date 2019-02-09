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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.ProductHomeAdapter;
import ir.woope.woopeapp.adapters.StoreGalleryAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ItemClickListener;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.ui.Activities.ProductActivity;
import ir.woope.woopeapp.ui.Activities.ProductHomeActivity;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class profile_home_fragment extends Fragment implements ItemClickListener {

    RecyclerView recyclerView;
    ProductHomeAdapter adapter;

    private boolean itShouldLoadMore = true;
    boolean searchInProgress = false;
    int PageNumber = 0;

    private List<StoreGalleryItem> albumList;

    profile_home_fragment.ItemTouchListener itemTouchListener;

    String authToken;
    int size;

    public profile_home_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public void onClick(View view, int position) {
        // The onClick implementation of the RecyclerView item click

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_home, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.product_home_recyclerview);

//        itemTouchListener = new profile_home_fragment().ItemTouchListener() {
//
//            @Override
//            public void onLikeClicked(View view, int position) {
//
//                StoreGalleryItem s = albumList.get(position);
//                LikeImage(s.id);
//
//                //open activity
//                //Toast.makeText(getActivity(), "شد", Toast.LENGTH_LONG).show();
//
//            }
//
//            @Override
//            public void onDoubleTap(View view, int position) {
//
//                StoreGalleryItem s = albumList.get(position);
//                LikeImage(s.id);
//
//            }
//
//        };

        albumList = new ArrayList<>();
        adapter = new ProductHomeAdapter(this.getActivity(), albumList,itemTouchListener);

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

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
                            getProductsByPage(PageNumber);
                        }
                    }

                }
            }
        });

        //prepareAlbums();
        getProductsByPage(0);

        return rootView;

    }

    private int getProductsByPage(final int pageNumber) {

//        showProgreeBar();

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

//        showProgreeBar();
        Call<List<StoreGalleryItem>> call =
                providerApiInterface.getAllActiveProducts("bearer " + authToken, pageNumber,10);

        call.enqueue(new Callback<List<StoreGalleryItem>>() {
            @Override
            public void onResponse(Call<List<StoreGalleryItem>> call, Response<List<StoreGalleryItem>> response) {
//                hideProgreeBar();
                int code = response.code();
                if (code == 200) {

//                    hideProgreeBar();

                    itShouldLoadMore = true;

                    if (response.body().size() > 1) {

                        adapter.addItem(response.body());

                        PageNumber++;

                    }

                }
            }

            @Override
            public void onFailure(Call<List<StoreGalleryItem>> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
//                hideProgreeBar();
                itShouldLoadMore = true;
                size = 0;
//                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });

        return size;

    }


    String countLike;
    private String LikeImage(long ImageId) {

//        loading.smoothToShow() ;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<StoreGalleryItem> call =
                providerApiInterface.LikeImage("bearer " + authToken, ImageId);

        call.enqueue(new Callback<StoreGalleryItem>() {
            @Override
            public void onResponse(Call<StoreGalleryItem> call, Response<StoreGalleryItem> response) {

                int code = response.code();
                if (code == 200) {

//                    adapter.updateProduct(response.body());

//                    loading.smoothToHide();
//
//                    if (response.body().getInfo().equals("0")) {
//                        LikeCount.setText("");
//
//                    } else {
//                        LikeCount.setText(response.body().getInfo() + " " + getResources().getString(R.string.like));
//
//                    }


                }
            }

            @Override
            public void onFailure(Call<StoreGalleryItem> call, Throwable t) {
//                loading.smoothToHide();
//                likeButton.setChecked(false);
//
//                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);


//                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        return countLike;
    }

    public interface ItemTouchListener {

        public void onLikeClicked(View view, int position);

        public void onDoubleTap(View view, int position);

    }

}
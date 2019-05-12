package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.Utils.CropSquareTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ItemClickListener;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.ui.Fragments.search_fragment;

public class StoreGalleryAdapter extends RecyclerView.Adapter<StoreGalleryAdapter.MyViewHolder> {

    private Context mContext;
    private List<StoreGalleryItem> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;

    private ItemClickListener clickListener;

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView thumbnail;
        ShimmerLayout shimmer;


        public MyViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.galleryThumbnail);
            itemView.setOnClickListener(this); // bind the listener
            shimmer = (ShimmerLayout) view.findViewById(R.id.shimmer_layout);

        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }


    public StoreGalleryAdapter(Context mContext, List<StoreGalleryItem> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_gallery_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        StoreGalleryItem store = albumList.get(position);

        holder.shimmer.startShimmerAnimation();

        // loading album cover using Glide library
        Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + store.postImageAddress).fit().centerInside().into(holder.thumbnail, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                //do smth when picture is loaded successfully

                holder.shimmer.stopShimmerAnimation();
                holder.shimmer.setVisibility(View.GONE);

            }

            @Override
            public void onError() {
                //do smth when there is picture loading error
            }
        });
        holder.shimmer.stopShimmerAnimation();
        holder.shimmer.setVisibility(View.GONE);

    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void addItem(final List<StoreGalleryItem> list) {

//        final int oldsize = albumList.size();
//        for (int i = list.size() - 1; i >= 0; i--) {
//            albumList.add(0, list.get(i));
//        }

        if (list.size() >= 1) {
            for (StoreGalleryItem s : list) {
                albumList.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }

    public void emptyList() {

        albumList.clear();

    }
}

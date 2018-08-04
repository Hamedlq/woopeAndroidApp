package ir.woope.woopeapp.adapters;

/**
 * Created by Hamed on 6/10/2018.
 */

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.home_fragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.MyViewHolder> {
    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<Store> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public BrandsAdapter(Context mContext, List<Store> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Store album = albumList.get(position);
        // loading album cover using Glide library
        Picasso.with(mContext).load(album.thumbnail).into(holder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
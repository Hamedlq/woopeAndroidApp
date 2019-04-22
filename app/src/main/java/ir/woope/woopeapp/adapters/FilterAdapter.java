package ir.woope.woopeapp.adapters;

/**
 * Created by Hamed on 6/10/2018.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.filter_fragment;
import ir.woope.woopeapp.ui.Fragments.search_fragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {
    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<Store> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;
    private filter_fragment.ItemTouchListener onItemTouchListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, point;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            point = (TextView) view.findViewById(R.id.point);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //overflow = (ImageView) view.findViewById(R.id.overflow);
            thumbnail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if (clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onCardViewTap(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if (clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onCardViewTap(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });

        }
    }


    public FilterAdapter(Context mContext, List<Store> albumList, filter_fragment.ItemTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Store store = albumList.get(position);
        holder.title.setText(store.storeName);
        //holder.count.setText(store.discountPercent + "٪ تخفیف");
        holder.count.setText("");
        holder.point.setText(store.returnPoint + " عدد ووپ");
        // loading album cover using Glide library
        Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
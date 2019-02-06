package ir.woope.woopeapp.adapters;

/**
 * Created by Hamed on 6/10/2018.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.MallModel;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.filter_fragment;
import ir.woope.woopeapp.ui.Fragments.mall_fragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class MallAdapter extends RecyclerView.Adapter<MallAdapter.MyViewHolder> {
    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<MallModel> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;
    private mall_fragment.ItemTouchListener onItemTouchListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public TextView title2;
        public ImageView thumbnail2;
        public CardView card_view, card_view2;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            card_view = (CardView) view.findViewById(R.id.card_view);

            title2 = (TextView) view.findViewById(R.id.title2);
            thumbnail2 = (ImageView) view.findViewById(R.id.thumbnail2);
            card_view2 = (CardView) view.findViewById(R.id.card_view2);

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

            thumbnail2.setOnTouchListener(new View.OnTouchListener() {
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
                                onItemTouchListener.onCardViewTap(v, getPosition() + 1);
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


    public MallAdapter(Context mContext, List<MallModel> albumList, mall_fragment.ItemTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mall_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            MallModel mall = albumList.get(position);
            holder.title.setText(mall.name);
            // loading album cover using Glide library
            Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + mall.srcImage).into(holder.thumbnail);
            if (position < albumList.size() - 1) {
                MallModel mall2 = albumList.get(position + 1);
                holder.title2.setText(mall2.name);
                // loading album cover using Glide library
                Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + mall2.srcImage).into(holder.thumbnail2);
            } else {
                holder.card_view2.setVisibility(View.GONE);
            }
        }else {
            holder.card_view2.setVisibility(View.GONE);
            holder.card_view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
package ir.woope.woopeapp.adapters;

/**
 * Created by Hamed on 6/10/2018.
 */
import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.home_fragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class StoresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<Store> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;
    private home_fragment.ItemTouchListener onItemTouchListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count,points,points_brief;
        public ImageView thumbnail;
        public ImageView followIcon;
        //public LinearLayout action_layout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            points = (TextView) view.findViewById(R.id.points);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            followIcon = (ImageView) view.findViewById(R.id.follow);
            points_brief=(TextView) view.findViewById(R.id.points_brief);
            //action_layout = (LinearLayout) view.findViewById(R.id.action_layout);
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
            /*action_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });*/
            followIcon.setOnTouchListener(new View.OnTouchListener() {
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
                                onItemTouchListener.onFollowTap(v, getPosition());
                                Store store = albumList.get(getPosition());
                               /* Drawable fDraw = followIcon.getBackground();
                                Drawable sDraw = getResources().getDrawable(R.drawable.twt_hover);

                                Bitmap bitmap = ((BitmapDrawable)fDraw).getBitmap();
                                Bitmap bitmap2 = ((BitmapDrawable)sDraw).getBitmap();
*/
                                if(!store.isFollowed){
                                    store.isFollowed=true;
                                    followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_like));
                                }else {
                                    store.isFollowed=false;
                                    followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_border));
                                }
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

    public class AdvViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public AdvViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
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
                                onItemTouchListener.onAdvTap(v, getPosition());
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



    public StoresAdapter(Context mContext, List<Store> albumList, home_fragment.ItemTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(albumList.get(position).isAdvertise){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adv_card, parent, false);

            return new AdvViewHolder(itemView);
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vholder, int position) {
        switch (vholder.getItemViewType()) {
            case 0:
                AdvViewHolder advHolder = (AdvViewHolder)vholder;
                Store advStore = albumList.get(position);
                // loading album cover using Glide library
                Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + advStore.logoSrc).into(advHolder.thumbnail);
                break;

            case 1:
                MyViewHolder holder = (MyViewHolder)vholder;
                Store store = albumList.get(position);
                holder.title.setText(store.storeName);
                holder.points_brief.setText(store.returnPoint + " عدد ووپ");
                holder.count.setText(store.discountPercent + "٪ تخفیف");
                holder.points.setText("به ازای هر " + store.basePrice + " تومان خرید " + store.returnPoint + " عدد ووپ هدیه بگیرید");

                // loading album cover using Glide library
                Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(holder.thumbnail);

                if (store.isFollowed) {
                    //store.isFollowed = true;
                    holder.followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_like));
                } else {
                    //store.isFollowed = false;
                    holder.followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_border));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
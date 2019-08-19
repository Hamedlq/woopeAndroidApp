package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.search_fragment;

public class PayMoneyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<Store> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;
    private search_fragment.ItemTouchListener onItemTouchListener;
    Boolean isFirst = true;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView storeName, pointCount, pointBrief, zone;
        public CardView zoneLayout;
        public RelativeLayout pointCountLayout;
        public ImageView thumbnail;

        public MyViewHolder(View view) {

            super(view);
            storeName = (TextView) view.findViewById(R.id.storeName);
//            pointCount = (TextView) view.findViewById(R.id.storePointCountText);
//            pointCountLayout = (RelativeLayout) view.findViewById(R.id.storePointCountLayout);
//            pointBrief = (TextView) view.findViewById(R.id.storePointBrief);
            thumbnail = (ImageView) view.findViewById(R.id.storeLogo);
//            zone = (TextView) view.findViewById(R.id.store_zone);
//            zoneLayout = (CardView) view.findViewById(R.id.store_zone_layout);
//            action_layout = (LinearLayout) view.findViewById(R.id.action_layout);
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

    public class SpaceViewHolder extends RecyclerView.ViewHolder {

        public SpaceViewHolder(View view) {
            super(view);


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (albumList.get(position).isSpace!=null && albumList.get(position).isSpace) {
            return 0;
        } else {
            return 1;
        }
    }

    public PayMoneyAdapter(Context mContext, List<Store> albumList, search_fragment.ItemTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.space_layout, parent, false);

            return new SpaceViewHolder(itemView);
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pay_money_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vholder, int position) {


        switch (vholder.getItemViewType()) {
            case 0:

                SpaceViewHolder advHolder = (SpaceViewHolder) vholder;

                break;

            case 1:
                MyViewHolder holder = (MyViewHolder) vholder;
                Store store = albumList.get(position);
                holder.storeName.setText(store.storeName);
//                if (store.zone != null) {
//                    holder.zoneLayout.setVisibility(View.VISIBLE);
//                    holder.zone.setText(store.zone);
//                }

//                holder.pointCountLayout.setVisibility(View.VISIBLE);
////                holder.pointCount.setText(store.returnPoint + " ووپ");
//                holder.pointCount.setText(String.valueOf(store.returnCashGift));

//                if (store.returnPoint != 0) {
//                    holder.pointBrief.setVisibility(View.VISIBLE);
//                    holder.pointBrief.setText("به ازای هر " + Utility.commaSeprate(store.basePrice) + " تومان خرید " + store.returnPoint + " عدد ووپ هدیه بگیرید");
//                } else if (store.storeDescription != null) {
//                    holder.pointBrief.setText(store.storeDescription);
//                }

//                if (store.zone == null)
//                    holder.zoneLayout.setVisibility(View.GONE);
//                else if (store.returnPoint != 0) {
//                    holder.zoneLayout.setVisibility(View.VISIBLE);
//                    holder.zone.setText(store.zone);
//                }

                // loading album cover using Glide library
                Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + store.coverSrc).into(holder.thumbnail);

                break;
        }


    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_buy:
                    Toast.makeText(mContext, "خرید", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_store:
                    Toast.makeText(mContext, "اطلاعات فروشگاه", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void addItem(Store s) {

        albumList.add(s);
        notifyDataSetChanged();

    }

    public void addList(final List<Store> list) {

//        final int oldsize = albumList.size();
//        for (int i = list.size() - 1; i >= 0; i--) {
//            albumList.add(0, list.get(i));
//        }

        if (list.size() >= 1) {
            for (Store s : list) {
                albumList.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }

    public void emptyList() {

        albumList.clear();
        notifyDataSetChanged();

    }
}


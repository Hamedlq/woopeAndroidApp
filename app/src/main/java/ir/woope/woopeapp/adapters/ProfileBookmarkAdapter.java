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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.StoreBookmarkActivity;
import ir.woope.woopeapp.ui.Fragments.profileBookmarkFragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class ProfileBookmarkAdapter extends RecyclerView.Adapter<ProfileBookmarkAdapter.MyViewHolder> {
    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<Store> albumList;
    private long startClickTime;
    private float mDownX;
    private float mDownY;
    private StoreBookmarkActivity.BookmarkTouchListener onItemTouchListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, points, points_brief;
        public ImageView thumbnail;
        public ImageView followIcon;
        //public LinearLayout action_layout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            followIcon = (ImageView) view.findViewById(R.id.follow);
            points = (TextView) view.findViewById(R.id.points);
            points_brief = (TextView) view.findViewById(R.id.points_brief);
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
                                onItemTouchListener.onCardViewTap(getPosition());
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
                                onItemTouchListener.onCardViewTap(getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });

           /* action_layout.setOnTouchListener(new View.OnTouchListener() {
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
                                onItemTouchListener.onFollowTap(getPosition());
                                Store store = albumList.get(getPosition());
                               /* Drawable fDraw = followIcon.getBackground();
                                Drawable sDraw = getResources().getDrawable(R.drawable.twt_hover);

                                Bitmap bitmap = ((BitmapDrawable)fDraw).getBitmap();
                                Bitmap bitmap2 = ((BitmapDrawable)sDraw).getBitmap();
*/
                                if (!store.isFollowed) {
                                    store.isFollowed = true;
                                    followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bookmarked));
                                } else {
                                    store.isFollowed = false;
                                    followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.notbookmarked));
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


    public ProfileBookmarkAdapter(Context mContext, List<Store> albumList, StoreBookmarkActivity.BookmarkTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_bookmark_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Store store = albumList.get(position);
        holder.title.setText(store.storeName);
        //holder.count.setText(store.discountPercent + "٪ تخفیف");
        holder.count.setText("");
        holder.points_brief.setText(store.returnPoint + " عدد ووپ");
        holder.points.setText("به ازای هر " + store.basePrice + " تومان خرید " + store.returnPoint + " عدد ووپ هدیه بگیرید");


// loading album cover using Glide library
        Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(holder.thumbnail);

        if (store.isFollowed) {
            //store.isFollowed=true;
            holder.followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bookmarked));
        } else {
            //store.isFollowed=false;
            holder.followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.notbookmarked));
        }
       /* holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/


    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem
                    .getMenuInfo();
            switch (menuItem.getItemId()) {
                case R.id.action_buy:
                    Toast.makeText(mContext, "خرید", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_store:
                    //onItemTouchListener.onCardViewTap(v, getPosition());
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

}
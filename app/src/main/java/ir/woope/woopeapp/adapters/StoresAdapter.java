package ir.woope.woopeapp.adapters;

/**
 * Created by Hamed on 6/10/2018.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.home_fragment;

import static android.content.Context.MODE_PRIVATE;

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
    home_fragment f;

    int lastItemPosition = -1;

    String scrollDirection;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, points, points_brief;
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
                                if (!store.isFollowed) {
                                    store.isFollowed = true;
                                    followIcon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_like));
                                } else {
                                    store.isFollowed = false;
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


    public StoresAdapter(Context mContext, List<Store> albumList, home_fragment.ItemTouchListener onItemTouchListener, home_fragment f) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.f = f;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (albumList.get(position).isAdvertise) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
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
                AdvViewHolder advHolder = (AdvViewHolder) vholder;
                Store advStore = albumList.get(position);
                // loading album cover using Glide library
                Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + advStore.logoSrc).into(advHolder.thumbnail);
                break;

            case 1:
                MyViewHolder holder = (MyViewHolder) vholder;
                Store store = albumList.get(position);
                holder.title.setText(store.storeName);
                holder.points_brief.setText(store.returnPoint + " عدد ووپ");
                //holder.count.setText(store.discountPercent + "٪ تخفیف");
                holder.count.setText("");
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

                SharedPreferences prefs =
                        mContext.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                boolean isFirstRun = prefs.getBoolean("HOMEFIRSTRUN", true);
                if (isFirstRun) {

                    if (position == 1) {

                        final TapTargetSequence sequence = new TapTargetSequence((Activity) mContext)
                                .targets(
                                        // Likewise, this tap target will target the search button
                                        TapTarget.forView(holder.followIcon, "علاقمندی ها", "فروشگاه رو به لیست علاقمندی های خودتون اضافه کنید")
                                                // All options below are optional
                                                .outerCircleColor(R.color.red)      // Specify a color for the outer circle
                                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                                .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                                .descriptionTextSize(14)            // Specify the size (in sp) of the description text
                                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                                .tintTarget(true)                   // Whether to tint the target view's color
                                                .transparentTarget(false)// Specify whether the target is transparent (displays the content underneath)
                                                .targetRadius(60)
                                )
                                .listener(new TapTargetSequence.Listener() {
                                    // This listener will tell us when interesting(tm) events happen in regards
                                    // to the sequence
                                    @Override
                                    public void onSequenceFinish() {

                                        SharedPreferences prefs =
                                                mContext.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("HOMEFIRSTRUN", false);
                                        editor.commit();

                                        f.showHint();

                                    }

                                    @Override
                                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                                    }

                                    @Override
                                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        final AlertDialog dialog = new AlertDialog.Builder(PayActivity.this)
//                                .setTitle("Uh oh")
//                                .setMessage("You canceled the seque.setPositiveButton("Oops", null).show();nce")
//
//                        TapTargetView.showFor(dialog,
//                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
//                                        .cancelable(false)
//                                        .tintTarget(false), new TapTargetView.Listener() {
//                                    @Override
//                                    public void onTargetClick(TapTargetView view) {
//                                        super.onTargetClick(view);
//                                        dialog.dismiss();
//                                    }
//                                });
                                    }
                                });

//                        sequence.start();

                    }

                }

                break;
        }

        if (position > lastItemPosition) {
            // Scrolled Down
            scrollDirection = "DOWN";
        } else {
            // Scrolled Up
            scrollDirection = "UP";
        }
        lastItemPosition = position;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public String getScrollDirection() {
        return scrollDirection;
    }

    public void addItem(final List<Store> list) {

//        final int oldsize = albumList.size();
//        for (int i = list.size() - 1; i >= 0; i--) {
//            albumList.add(0, list.get(i));
//        }

        if (list.size() > 1) {
            for (Store s : list) {
                albumList.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }

}
package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ItemClickListener;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.ui.Fragments.product_home_fragment;

public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.MyViewHolder> {

    private Context context;

    private List<StoreGalleryItem> Items;

    profile_home_fragment.ItemTouchListener onItemTouchListener;

    private ItemClickListener clickListener;



    public void setLikeClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public ProductHomeAdapter(Context context, List<StoreGalleryItem> list, product_home_fragment.ItemTouchListener onItemTouchListener) {
        this.Items = list;
        this.onItemTouchListener = onItemTouchListener;
//        this.payTransactionTouchListener = payTransactionTouchListener;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_home_item, parent, false);

        return new MyViewHolder(itemView);
//        return null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView storeName;
        private TextView likeCount;
        private TextView productDescription;
        private ImageView productImage;
        private SparkButton likeButton;


        public MyViewHolder(View view) {
            super(view);

            storeName = view.findViewById(R.id.store_name_product_home_item);

            likeCount = view.findViewById(R.id.txt_likeCount_product_home_item);

            productDescription = view.findViewById(R.id.productDescription_home_item);

            productImage = view.findViewById(R.id.product_home_item_Image);

            likeButton = view.findViewById(R.id.product_home_item_likebutton);

            productImage.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                }

                @Override
                public void onDoubleClick(View view) {
                    onItemTouchListener.onDoubleTap(view, getPosition());
                    StoreGalleryItem s_item= Items.get(getPosition());
                    if (!s_item.isLiked) {
                        s_item.isLiked= true;
                        likeButton.setChecked(true);
                    } else {
                        s_item.isLiked= false;
                        likeButton.setChecked(false);
                    }
                }
            }));

            likeButton.setEventListener(new SparkEventListener() {
                @Override
                public void onEvent(ImageView button, boolean buttonState) {
                    if (buttonState) {
                        // Button is active
                        onItemTouchListener.onLikeClicked(button, getPosition());
                        StoreGalleryItem s_item= Items.get(getPosition());
                        if (!s_item.isLiked) {
                            s_item.isLiked= true;
                            likeButton.setChecked(true);
                        } else {
                            s_item.isLiked= false;
                            likeButton.setChecked(false);
                        }
                    }
                }

                @Override
                public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                }

                @Override
                public void onEventAnimationStart(ImageView button, boolean buttonState) {

                }
            });

        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (Items.get(position).countLike == 0) {
            holder.likeCount.setText("");
        } else {
            holder.likeCount.setText(Items.get(position).countLike + " " + context.getResources().getString(R.string.like));
        }

        holder.productDescription.setText(Items.get(position).description);

        holder.storeName.setText(Items.get(position).storeName);

        if (Items.get(position).isLiked) {
            holder.likeButton.setChecked(true);
        } else if (!Items.get(position).isLiked) {
            holder.likeButton.setChecked(false);
        }

        Picasso.with(context).load(Constants.GlobalConstants.LOGO_URL + Items.get(position).productImageAddress).into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return Items.size();
    }


    public void addItem(final List<StoreGalleryItem> list) {

//        final int oldsize = albumList.size();
//        for (int i = list.size() - 1; i >= 0; i--) {
//            albumList.add(0, list.get(i));
//        }

        if (list.size() >= 1) {
            for (StoreGalleryItem s : list) {
                Items.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }

    public void emptyList() {

        Items.clear();

    }
}

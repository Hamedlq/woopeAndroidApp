package ir.woope.woopeapp.adapters;

import android.content.ClipData;
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
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.ui.Activities.ProductHomeActivity;
import ir.woope.woopeapp.ui.Activities.TransactionActivity;

public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.MyViewHolder> {

    private TextView storeName;
    private TextView likeCount;
    private TextView productDescription;
    private ImageView productImage;
    private SparkButton likeButton;
    private Context context;

    private List<StoreGalleryItem> Items;

    private DoubleClickListener doubleClickListener;
    private SparkEventListener sparkEventListener;

    ProductHomeActivity.ItemTouchListener onItemTouchListener;

    private ItemClickListener clickListener;

    int countLike;
    boolean isLiked;

    public void setLikeClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public ProductHomeAdapter(Context context, List<StoreGalleryItem> list, ProductHomeActivity.ItemTouchListener onItemTouchListener) {
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

        public MyViewHolder(View view) {
            super(view);

            storeName = view.findViewById(R.id.store_name_product_home_item);

            likeCount = view.findViewById(R.id.txt_likeCount_product_home_item);

            productDescription = view.findViewById(R.id.productDescription_home_item);

            productImage = view.findViewById(R.id.product_home_item_Image);

            productImage.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {

                    // Single tap here.
                }

                @Override
                public void onDoubleClick(View view) {
                    onItemTouchListener.onDoubleTap(view, getPosition());
//
//                    StoreGalleryItem product = Items.get(getPosition());
//                               /* Drawable fDraw = followIcon.getBackground();
//                                Drawable sDraw = getResources().getDrawable(R.drawable.twt_hover);
//
//                                Bitmap bitmap = ((BitmapDrawable)fDraw).getBitmap();
//                                Bitmap bitmap2 = ((BitmapDrawable)sDraw).getBitmap();
//*/
//                    if (product.isLiked) {
//                        likeButton.setChecked(true);
//                    } else if (!product.isLiked){
//                        likeButton.setChecked(false);
//                    }

                    // Double tap here.
                }
            }));
            //  use this to define your own interval
            //  }, 100));

            likeButton = view.findViewById(R.id.product_home_item_likebutton);

            likeButton.setEventListener(new SparkEventListener() {
                @Override
                public void onEvent(ImageView button, boolean buttonState) {
                    if (buttonState) {
                        // Button is active
                        onItemTouchListener.onLikeClicked(button, getPosition());
                        if (countLike >= 0) {
                            Items.get(getPosition()).countLike--;
                            likeCount.setText(Items.get(getPosition()).countLike);
//                            countLike--;
                            Items.get(getPosition()).isLiked = false;
                            likeButton.setChecked(Items.get(getPosition()).isLiked);
                        }
                    } else {
                        // Button is inactive
                        onItemTouchListener.onLikeClicked(button, getPosition());
                        Items.get(getPosition()).countLike++;
                        likeCount.setText(Items.get(getPosition()).countLike);
//                            countLike--;
                        Items.get(getPosition()).isLiked = true;
                        likeButton.setChecked(Items.get(getPosition()).isLiked);
//                        countLike++;
//                        isLiked = true;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (Items.get(position).countLike == 0) {
            likeCount.setText("");
        } else {
            likeCount.setText(Items.get(position).countLike + " " + context.getResources().getString(R.string.like));
        }

        countLike = Items.get(position).countLike;

        productDescription.setText(Items.get(position).description);

        storeName.setText(Items.get(position).storeName);

        if (Items.get(position).isLiked) {
            likeButton.setChecked(true);
        } else if (!Items.get(position).isLiked) {
            likeButton.setChecked(false);
        }

        isLiked = Items.get(position).isLiked;

        Picasso.with(context).load(Constants.GlobalConstants.LOGO_URL + Items.get(position).productImageAddress).into(productImage);

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

        if (list.size() > 1) {
            for (StoreGalleryItem s : list) {
                Items.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }


    public void updateProduct(StoreGalleryItem product) {
        int index = findProduct(Items, product);
        Items.set(index, product);
        notifyItemChanged(index);
    }

    private int findProduct(List<StoreGalleryItem> list, StoreGalleryItem product) {

        for (StoreGalleryItem p : list) {
            if (p.id == product.id) {
                return list.indexOf(p);
            }
        }
        return -1;
    }


}

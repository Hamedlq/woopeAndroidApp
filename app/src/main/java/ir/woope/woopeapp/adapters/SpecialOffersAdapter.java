package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import com.jsibbold.zoomage.ZoomageView;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.DoubleClickListener;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ItemClickListener;
import ir.woope.woopeapp.models.SpecialOfferItem;
import ir.woope.woopeapp.models.StoreGalleryItem;
import ir.woope.woopeapp.ui.Fragments.product_home_fragment;

public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.MyViewHolder> {

    private Context context;

    private List<SpecialOfferItem> Items;

    product_home_fragment.ItemTouchListener onItemTouchListener;

    private ItemClickListener clickListener;


    public void setLikeClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public SpecialOffersAdapter(Context context, List<SpecialOfferItem> list, product_home_fragment.ItemTouchListener onItemTouchListener) {
        this.Items = list;
        this.onItemTouchListener = onItemTouchListener;
//        this.payTransactionTouchListener = payTransactionTouchListener;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.special_offer_item, parent, false);

        return new MyViewHolder(itemView);
//        return null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView storeName;
        //        private TextView likeCount;
        private TextView productDescription;
        TextView offerDate;
        private SliderLayout productImage;
        private SparkButton likeButton;
        private RelativeLayout storeNameLayout;
        RelativeLayout weekDayLayout;
        TextView weekDay;
//        private Button sendOnlineRequest;

        public MyViewHolder(final View view) {
            super(view);

            storeName = view.findViewById(R.id.store_name_product_home_item);

            storeNameLayout = view.findViewById(R.id.storeNameLayout_product_home_item);

            storeNameLayout.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {

                    onItemTouchListener.onStoreNameClicked(arg0, getPosition());

                }
            });

//            sendOnlineRequest = view.findViewById(R.id.btn_send_onlineRequest_productHome_item);

//            sendOnlineRequest.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View arg0) {
//
//                    onItemTouchListener.onSendOnlineRequest(arg0, getPosition());
//
//                }
//            });

//            likeCount = view.findViewById(R.id.txt_likeCount_product_home_item);

            productDescription = view.findViewById(R.id.productDescription_home_item);

            productImage = view.findViewById(R.id.product_home_item_ImageSlider);

            likeButton = view.findViewById(R.id.product_home_item_likebutton);

            offerDate = view.findViewById(R.id.dateText_offer_item);

            weekDay = view.findViewById(R.id.weekDay_offer_item);
            weekDayLayout = view.findViewById(R.id.weekDay_layout_offer_item);

//            productImage.setOnClickListener(new DoubleClickListener() {
//
//                @Override
//                public void onSingleClick(View v) {
//
//                }
//
//                @Override
//                public void onDoubleClick(View v) {
//                    onItemTouchListener.onLikeClicked(v, getPosition());
//                    StoreGalleryItem s_item = Items.get(getPosition());
//                    if (!s_item.isLiked) {
////                            s_item.isLiked = true;
//                        likeButton.setChecked(true);
//                    } else {
////                            s_item.isLiked = false;
//                        likeButton.setChecked(false);
//                    }
//                }
//            });

//            productImage.setOnClickListener(new DoubleClick(new DoubleClickListener() {
//                @Override
//                public void onSingleClick(View view) {
//                }
//
//                @Override
//                public void onDoubleClick(View view) {
////                    onItemTouchListener.onDoubleTap(view, getPosition());
////                    StoreGalleryItem s_item = Items.get(getPosition());
////                    if (!s_item.isLiked) {
////                        s_item.isLiked = true;
////                        likeButton.setChecked(true);
////                    } else {
////                        s_item.isLiked = false;
////                        likeButton.setChecked(false);
////                    }
//                }
//            }));

//            likeButton.setEventListener(new SparkEventListener() {
//                @Override
//                public void onEvent(ImageView button, boolean buttonState) {
//                    if (buttonState) {
//                        // Button is active
//                        onItemTouchListener.onLikeClicked(button, getPosition());
//                        StoreGalleryItem s_item = Items.get(getPosition());
//                        if (!s_item.isLiked) {
////                            s_item.isLiked = true;
//                            likeButton.setChecked(true);
//                        } else {
////                            s_item.isLiked = false;
//                            likeButton.setChecked(false);
//                        }
//                    } else {
//                        // Button is inactive
//                        onItemTouchListener.onLikeClicked(button, getPosition());
//                        StoreGalleryItem s_item = Items.get(getPosition());
//                        if (s_item.isLiked) {
////                            s_item.isLiked= true;
//                            likeButton.setChecked(true);
//                        } else {
////                            s_item.isLiked= false;
//                            likeButton.setChecked(false);
//                        }
//                    }
//                }
//
//                @Override
//                public void onEventAnimationEnd(ImageView button, boolean buttonState) {
//
//                }
//
//                @Override
//                public void onEventAnimationStart(ImageView button, boolean buttonState) {
//
//                }
//            });

        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition()); // call the onClick in the OnItemClickListener
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

//        if (Items.get(position).countLike == 0) {
//            holder.likeCount.setText("");
//        } else {
//            holder.likeCount.setText(Items.get(position).countLike + " " + context.getResources().getString(R.string.like));
//        }

        holder.productDescription.setText(Items.get(position).description);

        holder.storeName.setText(Items.get(position).storeName);

//        if (Items.get(position).isLiked) {
//            holder.likeButton.setChecked(true);
//        } else if (!Items.get(position).isLiked) {
//            holder.likeButton.setChecked(false);
//        }

//        Picasso.with(context).load(Constants.GlobalConstants.LOGO_URL + Items.get(position).postImageAddress).into(holder.productImage);

        if (Items.get(position).listOfImageAddress != null)
            for (int i = 0; i < Items.get(position).listOfImageAddress.size(); i++) {
                DefaultSliderView textSliderView = new DefaultSliderView(context);

                textSliderView
                        .image(Constants.GlobalConstants.LOGO_URL + Items.get(position).listOfImageAddress.get(i))
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                holder.productImage.addSlider(textSliderView);

            }

        holder.offerDate.setText("از " + Items.get(position).persianRegisterDate + " تا " + Items.get(position).persianExpireDate);

        if (Items.get(position).weekDayName != null) {
            holder.weekDayLayout.setVisibility(View.VISIBLE);
            holder.weekDay.setText(Items.get(position).weekDayName);
        }

//        if(Items.get(position).canBeSold)
//            holder.sendOnlineRequest.setVisibility(View.VISIBLE);
//        else if(!Items.get(position).canBeSold)
//        holder.sendOnlineRequest.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return Items.size();
    }


    public void addItem(final List<SpecialOfferItem> list) {

//        final int oldsize = albumList.size();
//        for (int i = list.size() - 1; i >= 0; i--) {
//            albumList.add(0, list.get(i));
//        }

        if (list.size() >= 1) {
            for (SpecialOfferItem s : list) {
                Items.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }

    public void emptyList() {
        Items.clear();
    }

    public void updateProduct(SpecialOfferItem s, int position) {
        Items.set(position, s);
    }

    public SpecialOfferItem getProduct(int position) {
        return Items.get(position);
    }

    public void clearProducts() {
        Items.clear();
        notifyDataSetChanged();
    }

    public List<SpecialOfferItem> getProductList() {
        return Items;
    }

    public void LikeProduct(int position) {
        if (Items.get(position).isLiked) {
            Items.get(position).countLike--;
            Items.get(position).isLiked = false;
        } else if (!Items.get(position).isLiked) {
            Items.get(position).countLike++;
            Items.get(position).isLiked = true;
        }
        notifyItemChanged(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
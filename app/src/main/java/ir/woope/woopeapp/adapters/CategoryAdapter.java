package ir.woope.woopeapp.adapters;

/**
 * Created by Hamed on 6/10/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.ui.Fragments.main_fragment;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private Context mContext;
    private List<CategoryModel> albumList;
    private main_fragment.CategoryTouchListener onItemTouchListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public ImageView icon;
        public MaterialRippleLayout layout;

        public MyViewHolder(View view) {
            super(view);

            icon = (ImageView) view.findViewById(R.id.category_icon);
            label = (TextView) view.findViewById(R.id.category_label);
            layout = (MaterialRippleLayout) view.findViewById(R.id.categoryLayout);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemTouchListener.onCategoryTap(view,getPosition());

                }
            });

        }
    }

    public CategoryAdapter(Context mContext, List<CategoryModel> albumList,main_fragment.CategoryTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_tab, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryModel album = albumList.get(position);
        // loading album cover using Glide library
        if (album.isSelected)
            holder.layout.setBackground(mContext.getResources().getDrawable(R.color.categoryBackground));
        else if (!album.isSelected)
            holder.layout.setBackground(null);
        holder.label.setText(album.name);
        Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + album.image).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void selectItem(int position) {
        albumList.get(position).isSelected = true;
        notifyItemChanged(position);
    }

    public void deselectItem(int position){
        albumList.get(position).isSelected = false;
        notifyItemChanged(position);
    }

}
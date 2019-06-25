package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.SortType;
import ir.woope.woopeapp.ui.Fragments.search_fragment;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.MyViewHolder> {

    private Context mContext;
    private List<SortType> sortTypeList;
    private search_fragment.SortTouchListener onItemTouchListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public ImageView icon;
        public MaterialRippleLayout layout;
        public FrameLayout borderLayout;

        public MyViewHolder(View view) {
            super(view);

            icon = (ImageView) view.findViewById(R.id.sortIcon);
            label = (TextView) view.findViewById(R.id.sortLabel);
            layout = (MaterialRippleLayout) view.findViewById(R.id.sortRippleLayout);
            borderLayout = (FrameLayout) view.findViewById(R.id.sortFrameLayout);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemTouchListener.onSortTap(view, getPosition());

                }
            });

        }
    }

    public SortAdapter(Context mContext, List<SortType> albumList, search_fragment.SortTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.sortTypeList = albumList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sort_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SortType sortType = sortTypeList.get(position);
        if (sortType.isSelected)
            holder.borderLayout.setBackground(mContext.getResources().getDrawable(R.drawable.sort_border));
        else if (!sortType.isSelected)
            holder.borderLayout.setBackground(null);
        holder.label.setText(sortType.name);
//        Picasso.with(mContext).load(Constants.GlobalConstants.LOGO_URL + sortType.imgUrl).into(holder.icon);
    }


    @Override
    public int getItemCount() {
        return sortTypeList.size();
    }

    public void selectItem(int position) {
        sortTypeList.get(position).isSelected = true;
        notifyItemChanged(position);
    }

    public void deselectItem(int position){
        sortTypeList.get(position).isSelected = false;
        notifyItemChanged(position);
    }
}
package ir.woope.woopeapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.ULocale;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.CategoryModel;
import ir.woope.woopeapp.models.SortType;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.models.ZoneModel;
import ir.woope.woopeapp.ui.Fragments.search_fragment;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ViewHolder> {

    private static final int MAX_CLICK_DURATION = 200;
    private Context mContext;
    private List<ZoneModel> ZoneList;
    private long startClickTime;

    private search_fragment.ZoneTouchListener onItemTouchListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView zoneName;
        public CustomCheckBox zoneCheck;
        CardView zoneCard;private List<CategoryModel> categoryList;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View view) {

            super(view);

            zoneName = view.findViewById(R.id.zoneNameItem);
            zoneCheck = (CustomCheckBox) view.findViewById(R.id.zoneCheckItem);
            zoneCard = view.findViewById(R.id.zoneCardItem);

//            zoneCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//            {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//                {
//
//                    onItemTouchListener.onZoneTap(buttonView,getPosition());
//
//                }
//            });

            zoneCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    zoneCheck.performClick();

                }
            });

            zoneCheck.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                    onItemTouchListener.onZoneTap(checkBox,getPosition());
                }
            });

//            zoneCheck.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN: {
//                            startClickTime = Calendar.getInstance().getTimeInMillis();
//                            break;
//                        }
//                        case MotionEvent.ACTION_UP: {
//                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
//                            if (clickDuration < MAX_CLICK_DURATION) {
//                                onItemTouchListener.onZoneTap(v,getPosition());
//                            }
//                            break;
//                        }
//                        default:
//                            break;
//                    }
//                    return true;
//                }
//            });
        }
    }


    public ZoneAdapter(Context mContext, List<ZoneModel> zoneList, search_fragment.ZoneTouchListener onItemTouchListener) {
        this.mContext = mContext;
        this.ZoneList = zoneList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ZoneModel zone = ZoneList.get(position);
        holder.zoneName.setText(zone.name);
        if (zone.isChecked)
            holder.zoneCheck.setChecked(true);
        else if (!zone.isChecked) {
            holder.zoneCheck.setChecked(false);
        }
    }

    public void updateList(List<ZoneModel> newList)
    {

        ZoneList = new ArrayList<>();
        ZoneList = newList;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return ZoneList.size();
    }

    public void addItem(final List<ZoneModel> list) {

//        final int oldsize = albumList.size();
//        for (int i = list.size() - 1; i >= 0; i--) {
//            albumList.add(0, list.get(i));
//        }

        if (list.size() >= 1) {
            for (ZoneModel s : list) {
                ZoneList.add(s);
            }
        }
        notifyDataSetChanged();

//        notifyItemRangeInserted(0, albumList.size() - oldsize);


    }

    public void emptyList() {

        ZoneList.clear();

    }

    public List<ZoneModel> getList(){
        return ZoneList;
    }

}
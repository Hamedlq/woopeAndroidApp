package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.PayListModel;
import ir.woope.woopeapp.models.TransactionModel;
import ir.woope.woopeapp.ui.Fragments.ProfileTransactionListFragment;
import ir.woope.woopeapp.ui.Fragments.TransListFragment;

/**
 * Created by alireza on 3/30/18.
 */

public class ProfileTransactionListAdapter extends RecyclerView.Adapter<ProfileTransactionListAdapter.MyViewHolder> {

    private TextView name_tv;
    private TextView total_price;
    private TextView paytype;
    private ImageView ic_item;
    private TextView return_woop;
private Context context;
    private List<TransactionModel> orderModels;

    private ProfileTransactionListFragment.ProfileTransactionTouchListener payTransactionTouchListener;

    private long startClickTime;
    private static final int MAX_CLICK_DURATION = 200;


    public ProfileTransactionListAdapter(List<TransactionModel> list) {
        this.orderModels = list;
    }


    public ProfileTransactionListAdapter(Context context,List<TransactionModel> list, ProfileTransactionListFragment.ProfileTransactionTouchListener payTransactionTouchListener) {
        context=context;
        this.orderModels = list;
        this.payTransactionTouchListener=payTransactionTouchListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trans_list_item, parent, false);

        return new MyViewHolder(itemView);
//        return null;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
            total_price = view.findViewById(R.id.total_price);
            name_tv= view.findViewById(R.id.store_name_textview);
            paytype= view.findViewById(R.id.paytype);
            ic_item=view.findViewById(R.id.ic_item);
            return_woop=view.findViewById(R.id.return_woop);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payTransactionTouchListener.onBtnClick(v, getPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(ProfileTransactionListAdapter.MyViewHolder holder, int position) {
        total_price.setText(String.valueOf(orderModels.get(position).totalPrice)+" تومان ");
        name_tv.setText(orderModels.get(position).storeName);
        return_woop.setText(orderModels.get(position).returnWoop);
        Picasso.with(context).load(Constants.GlobalConstants.LOGO_URL + orderModels.get(position).logoSrc).into(ic_item);
        if(orderModels.get(position).payType==1){
            paytype.setText("نقدی");
        }else{
            paytype.setText("اعتباری");
        }
    }

    @Override
    public int getItemCount() {
        int i = orderModels.size();
        return orderModels.size();
    }
}

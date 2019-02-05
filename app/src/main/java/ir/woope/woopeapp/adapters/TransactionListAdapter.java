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
import ir.woope.woopeapp.ui.Activities.TransactionActivity;

/**
 * Created by alireza on 3/30/18.
 */

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MyViewHolder> {

    private TextView title;
    private TextView total_price;
    private TextView paytype;
    private ImageView thumbnail;
    private Context context;

    private List<PayListModel> orderModels;

    private TransactionActivity.PayTransactionTouchListener payTransactionTouchListener;

    private long startClickTime;
    private static final int MAX_CLICK_DURATION = 200;


    public TransactionListAdapter(List<PayListModel> list) {
        this.orderModels = list;
    }


    public TransactionListAdapter(Context context, List<PayListModel> list, TransactionActivity.PayTransactionTouchListener payTransactionTouchListener) {
        this.orderModels = list;
        this.payTransactionTouchListener = payTransactionTouchListener;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pay_list_item, parent, false);

        return new MyViewHolder(itemView);
//        return null;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
            total_price = view.findViewById(R.id.total_price);
            title = view.findViewById(R.id.title);
            paytype = view.findViewById(R.id.paytype);
            thumbnail = view.findViewById(R.id.thumbnail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payTransactionTouchListener.onBtnClick(v, getPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(TransactionListAdapter.MyViewHolder holder, int position) {
        total_price.setText(String.valueOf(orderModels.get(position).totalPrice) + " تومان ");
        title.setText(orderModels.get(position).storeName);
        Picasso.with(context).load(Constants.GlobalConstants.LOGO_URL + orderModels.get(position).logoSrc).into(thumbnail);
        if (orderModels.get(position).payType == 1) {
            paytype.setText("نقدی");
        } else {
            paytype.setText("اعتباری");
        }

    }

    @Override
    public int getItemCount() {
        int i = orderModels.size();
        return orderModels.size();
    }
}

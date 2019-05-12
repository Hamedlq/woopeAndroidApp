package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.models.DocumentModel;
import ir.woope.woopeapp.ui.Activities.TransHistoryActivity;

import static ir.woope.woopeapp.helpers.Utility.commaSeprate;

/**
 * Created by alireza on 3/30/18.
 */

public class ProfileTransactionListAdapter extends RecyclerView.Adapter<ProfileTransactionListAdapter.MyViewHolder> {

    private TextView name_tv;
    private TextView total_price;
    private ImageView paytype;
    //private ImageView ic_item;
    private TextView return_woop;
    private RelativeLayout tr1;
    private TextView tr1_ct;
    private TextView tr1_am;
    private TextView tr1_tt;
    private TextView tr1_time;
    private RelativeLayout tr2;
    private TextView tr2_ct;
    private TextView tr2_am;
    private TextView tr2_tt;
    private TextView tr2_time;
    private RelativeLayout tr3;
    private TextView tr3_ct;
    private TextView tr3_am;
    private TextView tr3_tt;
    private TextView tr3_time;
    private RelativeLayout tr4;
    private TextView tr4_ct;
    private TextView tr4_am;
    private TextView tr4_tt;
    private TextView tr4_time;
    private RelativeLayout tr5;
    private TextView tr5_ct;
    private TextView tr5_am;
    private TextView tr5_tt;
    private TextView tr5_time;

    private Context context;
    private List<DocumentModel> orderModels;

    //private TransHistoryActivity.ProfileTransactionTouchListener payTransactionTouchListener;

    private long startClicktime;
    private static final int MAX_CLICK_DURATION = 200;


    public ProfileTransactionListAdapter(List<DocumentModel> list) {
        this.orderModels = list;
    }


    public ProfileTransactionListAdapter(Context context, List<DocumentModel> list) {
        context = context;
        this.orderModels = list;
        //this.payTransactionTouchListener=payTransactionTouchListener;
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
            name_tv = view.findViewById(R.id.store_name_textview);
            paytype = view.findViewById(R.id.paytype);
            // ic_item=view.findViewById(R.id.ic_item);
            return_woop = view.findViewById(R.id.return_woop);
            tr1 = view.findViewById(R.id.tr1);
            tr1_ct = view.findViewById(R.id.tr1_ct);
            tr1_am = view.findViewById(R.id.tr1_am);
            tr1_tt = view.findViewById(R.id.tr1_tt);
            tr1_time = view.findViewById(R.id.tr1_time);
            tr2 = view.findViewById(R.id.tr2);
            tr2_ct = view.findViewById(R.id.tr2_ct);
            tr2_am = view.findViewById(R.id.tr2_am);
            tr2_tt = view.findViewById(R.id.tr2_tt);
            tr2_time = view.findViewById(R.id.tr2_time);
            tr3 = view.findViewById(R.id.tr3);
            tr3_ct = view.findViewById(R.id.tr3_ct);
            tr3_am = view.findViewById(R.id.tr3_am);
            tr3_tt = view.findViewById(R.id.tr3_tt);
            tr3_time = view.findViewById(R.id.tr3_time);
            tr4 = view.findViewById(R.id.tr4);
            tr4_ct = view.findViewById(R.id.tr4_ct);
            tr4_am = view.findViewById(R.id.tr4_am);
            tr4_tt = view.findViewById(R.id.tr4_tt);
            tr4_time = view.findViewById(R.id.tr4_time);
            tr5 = view.findViewById(R.id.tr5);
            tr5_ct = view.findViewById(R.id.tr5_ct);
            tr5_am = view.findViewById(R.id.tr5_am);
            tr5_tt = view.findViewById(R.id.tr5_tt);
            tr5_time = view.findViewById(R.id.tr5_time);
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payTransactionTouchListener.onBtnClick(v, getPosition());
                }
            });*/
        }
    }

    @Override
    public void onBindViewHolder(ProfileTransactionListAdapter.MyViewHolder holder, int position) {
        DocumentModel model = orderModels.get(position);
        total_price.setText(commaSeprate(orderModels.get(position).totalPrice) + " تومان " );
        name_tv.setText(orderModels.get(position).storeName);
        return_woop.setText(orderModels.get(position).returnWoop + " عدد ووپ ");
        if (model.payType == 1) {
            paytype.setImageResource(R.drawable.wallet_mini);
        } else {
            paytype.setImageResource(R.drawable.mini_woope);
        }
        //if(model.payType==10)
        if (model.transactionList.size() > 0) {
            tr1_ct.setText(model.transactionList.get(0).currencyType);
            tr1_am.setText(commaSeprate(model.transactionList.get(0).amount) );
            tr1_tt.setText(model.transactionList.get(0).transType);
            tr1_time.setText(model.transactionList.get(0).time);
        } else {
            tr1.setVisibility(View.GONE);
        }
        if (model.transactionList.size() > 1) {
            tr2_ct.setText(model.transactionList.get(1).currencyType);
            tr2_am.setText(commaSeprate(model.transactionList.get(1).amount) );
            tr2_tt.setText(model.transactionList.get(1).transType);
            tr2_time.setText(model.transactionList.get(1).time);
        } else {
            tr2.setVisibility(View.GONE);
        }
        if (model.transactionList.size() > 2) {
            tr3_ct.setText(model.transactionList.get(2).currencyType);
            tr3_am.setText(commaSeprate(model.transactionList.get(2).amount) );
            tr3_tt.setText(model.transactionList.get(2).transType);
            tr3_time.setText(model.transactionList.get(2).time);
        } else {
            tr3.setVisibility(View.GONE);
        }
        if (model.transactionList.size() > 3) {
            tr4_ct.setText(model.transactionList.get(3).currencyType);
            tr4_am.setText(commaSeprate(model.transactionList.get(3).amount) );
            tr4_tt.setText(model.transactionList.get(3).transType);
            tr4_time.setText(model.transactionList.get(3).time);
        } else {
            tr4.setVisibility(View.GONE);
        }
        if (model.transactionList.size() > 4) {
            tr5_ct.setText(model.transactionList.get(4).currencyType);
            tr5_am.setText(commaSeprate(model.transactionList.get(4).amount) );
            tr5_tt.setText(model.transactionList.get(4).transType);
            tr5_time.setText(model.transactionList.get(4).time);
        } else {
            tr5.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        int i = orderModels.size();
        return orderModels.size();
    }
}

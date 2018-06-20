package ir.woope.woopeapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;


import ir.woope.woopeapp.R;
import ir.woope.woopeapp.models.TransactionModel;
import ir.woope.woopeapp.ui.Fragments.TransListFragment;

/**
 * Created by alireza on 3/30/18.
 */

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MyViewHolder> {

    //    List<Model> modelList = new ArrayList<>();
    private TextView name_tv, price_tv, visitor_price;
    private TextView brand_tv;
    private TextView description_tv;
    private LinearLayout desc_layout;
    private TextView supplier_name_textview;
    private TextView supplier_phone_textview;
    //private Button cancel_btn;

    private List<TransactionModel> orderModels;

    private TransListFragment.CancelOrderTouchListener cancelOrderTouchListener;

    private long startClickTime;
    private static final int MAX_CLICK_DURATION = 200;


    public TransactionListAdapter(List< TransactionModel> list) {
        this.orderModels = list;
    }


    public TransactionListAdapter(List<TransactionModel> list,TransListFragment.CancelOrderTouchListener cancelOrderTouchListener) {
        this.orderModels = list;
        this.cancelOrderTouchListener=cancelOrderTouchListener;
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
            price_tv = view.findViewById(R.id.item_price_textview);
            visitor_price= view.findViewById(R.id.visitor_price);
            name_tv= view.findViewById(R.id.item_name_textview);
            brand_tv= view.findViewById(R.id.item_brand_textview);
            description_tv = view.findViewById(R.id.item_description_textview);
            desc_layout= view.findViewById(R.id.desc_layout);
            supplier_name_textview = view.findViewById(R.id.supplier_name_textview);
            supplier_phone_textview = view.findViewById(R.id.supplier_phone_textview);
/*            cancel_btn=view.findViewById(R.id.cancel_btn);
            cancel_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                cancelOrderTouchListener.onBtnClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });*/

        }
    }

    @Override
    public void onBindViewHolder(TransactionListAdapter.MyViewHolder holder, int position) {
//        testText.setText("wow this is a test with position number" + position);
        name_tv.setText(orderModels.get(position).storeName);
        price_tv.setText(orderModels.get(position).amount);
        /*visitor_price.setText(orderModels.get(position).visitorPrice);
        brand_tv.setText(orderModels.get(position).itemBrand);
        if (!TextUtils.isEmpty(orderModels.get(position).itemDescription)){
            description_tv.setText(orderModels.get(position).itemDescription);
            desc_layout.setVisibility(View.VISIBLE);
        }else {
            desc_layout.setVisibility(View.GONE);
        }
        String name=orderModels.get(position).name + " "+orderModels.get(position).family+"("+orderModels.get(position).shopname+") - "+orderModels.get(position).address ;
        supplier_name_textview.setText(name);
        String contact=orderModels.get(position).mobile+ " - "+orderModels.get(position).shopphone;
        supplier_phone_textview.setText(contact);*/
    }

    @Override
    public int getItemCount() {
        int i = orderModels.size();
        return orderModels.size();
    }
}

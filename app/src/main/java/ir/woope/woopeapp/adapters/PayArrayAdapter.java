package ir.woope.woopeapp.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.models.PayState;

public class PayArrayAdapter  extends ArrayAdapter<PayState> {

    private List<PayState> items;
    private Activity activity;
    private int selectedPosition;

    public PayArrayAdapter(Activity activity, List<PayState> items) {
        super(activity, R.layout.spinner_row, items);
        this.items = items;
        this.activity = activity;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
        /*TextView v = (TextView) getView(position, convertView, parent);

        if (v == null) {
            v = new TextView(activity);
        }
        v.setTextColor(Color.BLACK);
        v.setText(items.get(position).toString());
        return v;*/
    }

    @Override
    public PayState getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
        /*View v = convertView;

        if (v == null) {

            v = inflater.inflate(R.layout.spinner_row, null);
        }
        TextView lbl = (TextView) v.findViewById(R.id.mode);
        lbl.setTextColor(Color.BLACK);
        lbl.setText(items.get(position).toString());
        return v;*/
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        //LayoutInflater inflater=getLayoutInflater();
        View row=inflater.inflate(R.layout.spinner_row, parent, false);
        TextView label=(TextView)row.findViewById(R.id.mode);
        label.setText(items.get(position).getMode());

        TextView sub=(TextView)row.findViewById(R.id.credit);
        sub.setText( " اعتبار "+items.get(position).getCredit());

        /*ImageView icon=(ImageView)row.findViewById(R.id.image);
        icon.setImageResource(arr_images[position]);*/

        return row;
    }


}


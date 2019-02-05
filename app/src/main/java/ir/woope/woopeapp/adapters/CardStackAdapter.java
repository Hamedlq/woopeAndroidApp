package ir.woope.woopeapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ir.woope.woopeapp.R;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Drawable> slides;

    public CardStackAdapter(Context context, List<Drawable> slides) {
        this.inflater = LayoutInflater.from(context);
        this.slides = slides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.slider_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawable spot = slides.get(position);

        Glide.with(holder.image)
                .load(spot)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    public List<Drawable> getSlides() {
        return slides;
    }

    public void setSpots(List<Drawable> slides) {
        this.slides = slides;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        ViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.card_background);
        }
    }


}

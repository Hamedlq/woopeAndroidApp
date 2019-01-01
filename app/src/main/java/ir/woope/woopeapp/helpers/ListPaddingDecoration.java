package ir.woope.woopeapp.helpers;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ListPaddingDecoration extends RecyclerView.ItemDecoration {
    private final int mPadding=70;
    public ListPaddingDecoration() {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        if (itemPosition == 0) {
            outRect.top = mPadding;
        }
    }

}
package ir.woope.woopeapp.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RectangleLayout extends FrameLayout {

    public RectangleLayout(Context context) {
        super(context);
    }


    public RectangleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RectangleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    // Here note that the height is width/2

    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, (widthMeasureSpec/2));
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, (size/2));
    }


}

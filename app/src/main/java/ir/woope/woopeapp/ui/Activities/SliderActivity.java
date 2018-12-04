package ir.woope.woopeapp.ui.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.HashMap;

import ir.woope.woopeapp.R;

public class SliderActivity extends AppCompatActivity {

    SliderLayout slider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        //slider = findViewById(R.id.imageSlider);

        //slider.setIndicatorAnimation(SliderLayout.Animations.SWAP);

        //setSliderViews();

    }

    private void setSliderViews(){

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.slider1);
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.slider2);
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.slider3);
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.slider4);
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.FIT_CENTER);
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    Toast.makeText(SliderActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                }
            });

            //at last add this view in your layout :
            slider.addSliderView(sliderView);
        }

    }

}

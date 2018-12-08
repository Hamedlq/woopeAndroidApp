package ir.woope.woopeapp.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.ChildAnimationExample;
import ir.woope.woopeapp.adapters.CardStackAdapter;
import ir.woope.woopeapp.helpers.Constants;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class SliderActivity extends AppCompatActivity implements CardStackListener {

    List<Drawable> slides = new ArrayList<Drawable>();

    CardStackView cardStackView;
    CardStackLayoutManager manager;
    CardStackAdapter adapter;

    PageIndicatorView pageIndicatorView;

    int cardPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        slides.add(getResources().getDrawable(R.drawable.slider1));
        slides.add(getResources().getDrawable(R.drawable.slider2));
        slides.add(getResources().getDrawable(R.drawable.slider3));
        slides.add(getResources().getDrawable(R.drawable.slider4));

        manager = new CardStackLayoutManager(this, this);
        adapter = new CardStackAdapter(this, slides);
        cardStackView = findViewById(R.id.card_stack_view);
        cardStackView.setLayoutManager(manager);
        manager.setStackFrom(StackFrom.Bottom);
        manager.setVisibleCount(3);
        manager.setSwipeThreshold(0.1f);
        manager.setTranslationInterval(8f);
        cardStackView.setAdapter(adapter);
        manager.setDirections(Direction.FREEDOM);
        manager.setStackFrom(StackFrom.Left);

//        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
//                .setDirection(Direction.Right)
//                .setDuration(200)
//                .setInterpolator(new DecelerateInterpolator())
//                .build();
//        manager.setRewindAnimationSetting(setting);
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Do something after 1 second
//                        cardStackView.swipe();
//                    }
//                }, 750);
//            }
//        });
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Do something after 1 second
//                        cardStackView.rewind();
//                    }
//                }, 1500);
//            }
//        });

        cardPosition = 0;

        pageIndicatorView.setAnimationType(AnimationType.SWAP);

        pageIndicatorView.setCount(4); // specify total count of indicators
        pageIndicatorView.setSelection(0);


    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {

//        cardPosition++;
        pageIndicatorView.setSelected(manager.getTopPosition());

        if (manager.getTopPosition() == adapter.getItemCount()) {
            // -------------------- last position reached, do something ---------------------

            SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("tutorialIsShown", true);
            editor.apply();

            startActivity(new Intent(this, SplashSelectActivity.class));
            finishAffinity();
        }

    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

}

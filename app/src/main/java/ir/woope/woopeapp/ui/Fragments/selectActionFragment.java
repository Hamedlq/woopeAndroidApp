package ir.woope.woopeapp.ui.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.ui.Activities.EarnMoneyActivity;
import ir.woope.woopeapp.ui.Activities.PayMoneyActivity;
import ir.woope.woopeapp.ui.Activities.StoreBookmarkActivity;
import ir.woope.woopeapp.ui.Activities.TransHistoryActivity;

import static android.view.HapticFeedbackConstants.CONTEXT_CLICK;


public class selectActionFragment extends Fragment {

    private View mRecycler;
    CardView earnMoney,payMoney;
    ImageView eye_logo,bg;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        mRecycler = inflater.inflate(R.layout.fragment_select_action, container, false);

        earnMoney = mRecycler.findViewById(R.id.btn_earnMoney);
        payMoney = mRecycler.findViewById(R.id.btn_payMoney);
        eye_logo = mRecycler.findViewById(R.id.eye_logo_selectAction);
        bg = mRecycler.findViewById(R.id.selectAction_background);

        return mRecycler;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bg.setBackground(getContext().getResources().getDrawable(R.drawable.select_action_background_v2));

        earnMoney.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                showEarnDialog();

            }
        });

        payMoney.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                showPayDialog();
            }
        });

        YoYo.with(Techniques.FadeIn)
                .duration(1500)
                .repeat(9999999)
                .playOn(eye_logo);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private void showEarnDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.earn_money_dialog);
        dialog.setTitle("");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2; //style id
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
        CardView card = dialog.findViewById(R.id.card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent goto_earnMoney = new Intent(getActivity(),
                        EarnMoneyActivity.class);
                getActivity().startActivity(goto_earnMoney);
            }
        });
        dialog.show();
    }

    private void showPayDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.pay_money_dialog);
        dialog.setTitle("");
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2; //style id
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
        CardView card = dialog.findViewById(R.id.card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent goto_earnMoney = new Intent(getActivity(),
                        PayMoneyActivity.class);
                getActivity().startActivity(goto_earnMoney);
            }
        });
        dialog.show();
    }

}

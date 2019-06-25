package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Categories;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.SocialModel;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.SplashSelectActivity;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.OPEN_MAIN_ACTIVITY;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class storeInfoFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity().getIntent() != null && getActivity().getIntent().getExtras() != null) {
//            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            store = (Store) getActivity().getIntent().getExtras().getSerializable(STORE);
            vip_store_layout.setVisibility(View.GONE);
            if (store.categoryId != null) {
                for (long cId : store.categoryId) {
                    if (cId == Categories.VipService.value()) {
                        vip_store_layout.setVisibility(View.VISIBLE);
                    }
                }
            }
//            Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(logo);
        }

        getStore(store.storeId);

    }

    View layout;

    ImageView logo;
    ImageView backdrop;
    TextView store_name;
    TextView store_desc;
    AVLoadingIndicatorView progressBar;
    //    TextView store_point;
    TextView store_discount;
    TextView point_desc;
    TextView store_address;
    TextView store_phones;
    CardView desc_layout;
    CardView point_layout;
    CardView store_phones_layout;
    CardView store_address_layout;
    CardView report_store_layout;
    CardView vip_store_layout;

    String authToken = null;

    Store store;

    LinearLayout socialsLayout;

    TextView giftWoopeDescription;
    CardView giftWoopeLayout;

    AVLoadingIndicatorView shareLoading;

    public storeInfoFragment() {
        // Required empty public constructor
    }

    private boolean IsLogedIn() {
        final SharedPreferences prefs =
                getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String tokenString = prefs.getString(TOKEN, null);
        if (tokenString == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_info, container, false);

        layout = rootView.findViewById(R.id.info_layout);
        logo = rootView.findViewById(R.id.logo);
        backdrop = rootView.findViewById(R.id.backdrop);
        store_name = rootView.findViewById(R.id.store_name);
        store_desc = rootView.findViewById(R.id.store_desc);
        progressBar = rootView.findViewById(R.id.progressBar_store_info);
//        store_point = rootView.findViewById(R.id.store_point);
//        store_discount = rootView.findViewById(R.id.store_discount);
        point_desc = rootView.findViewById(R.id.point_desc);
        store_address = rootView.findViewById(R.id.store_address);
        store_phones = rootView.findViewById(R.id.store_phones);
        desc_layout = rootView.findViewById(R.id.desc_layout);
        point_layout = rootView.findViewById(R.id.point_layout);
        store_phones_layout = rootView.findViewById(R.id.store_phones_layout);
        store_address_layout = rootView.findViewById(R.id.store_address_layout);
        report_store_layout = rootView.findViewById(R.id.report_store_layout);
        vip_store_layout = rootView.findViewById(R.id.vip_store_layout);
        socialsLayout = rootView.findViewById(R.id.store_socials_layout);

        giftWoopeDescription = rootView.findViewById(R.id.dedicated_giftCode_desc);
        giftWoopeLayout = rootView.findViewById(R.id.dedicated_giftCode_layout);

        shareLoading = rootView.findViewById(R.id.progressBar_shareStore_storeinfo);

        report_store_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // item clicked
                if (IsLogedIn()) {
                    Snackbar snackbar = Snackbar
                            .make(layout, R.string.areYouSureAboutReportingStore, Snackbar.LENGTH_LONG)
                            .setAction(R.string.yes, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    reportStore(store.storeId);
                                }
                            });

                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);

                    tv.setGravity(Gravity.CENTER_HORIZONTAL);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }

                    snackbar.show();

                } else {
                    Intent goto_login = new Intent(getActivity(),
                            SplashSelectActivity.class);
                    goto_login.putExtra(OPEN_MAIN_ACTIVITY, false);
                    goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //finish();
                    startActivity(goto_login);
                }

            }
        });

        vip_store_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // item clicked

                sendOnlineRequestStore(store.storeId);

            }
        });

        giftWoopeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // item clicked

                shareStore(store.storeId);

            }
        });

        return rootView;

    }

    public void showProgreeBar() {
        progressBar.smoothToShow();
//        payBtn.setEnabled(false);
    }

    public void hideProgreeBar() {
        progressBar.smoothToHide();
//        payBtn.setEnabled(true);
    }

    private void getStore(long storeId) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<Store> call =
                providerApiInterface.getStore("bearer " + authToken, storeId);

        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                hideProgreeBar();
                int code = response.code();
                if (code == 200) {
                    store = response.body();
//                    Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.logoSrc).transform(new CircleTransformation()).into(logo);
//                    Picasso.with(StoreActivity.this).load(Constants.GlobalConstants.LOGO_URL + store.coverSrc).into(backdrop);
//                    store_name.setText(store.storeName);
                    if (!TextUtils.isEmpty(store.storeDescription)) {
                        desc_layout.setVisibility(View.VISIBLE);
                        store_desc.setText(store.storeDescription);
                    }
                    String phones = "";
                    if (!TextUtils.isEmpty(store.firstPhone)) {
                        store_phones_layout.setVisibility(View.VISIBLE);
                        phones = store.firstPhone;
                    }
                    if (!TextUtils.isEmpty(store.secondPhone)) {
                        store_phones_layout.setVisibility(View.VISIBLE);
                        phones += " - " + store.secondPhone;
                    }
                    store_phones.setText(phones);
//                    if (!TextUtils.isEmpty(store.discountPercent)) {
//                        store_discount.setVisibility(View.VISIBLE);
//                        store_discount.setText(store.discountPercent + " درصد تخفیف ");
//                    }
                    if (!TextUtils.isEmpty(store.address)) {
                        store_address_layout.setVisibility(View.VISIBLE);
                        store_address.setText(store.address);
                    }

                    if (store.basePrice != 0) {
                        point_layout.setVisibility(View.VISIBLE);
//                        store_point.setText(store.returnPoint + " عدد ووپ ");
                        point_desc.setText("به ازای هر " + Utility.commaSeprate(store.basePrice) + " تومان خرید " + store.returnPoint + " ووپ دریافت می‌کنید");
                    }

                    if (store.socialList != null) {
                        socialsLayout.setVisibility(View.VISIBLE);
                        for (SocialModel social : store.socialList) {
                            addSocial(social.keySocial, social.valueSocial);
                        }
                    }

                    if (store.describeCountDiscountCode != null) {
                        giftWoopeLayout.setVisibility(View.VISIBLE);
                        giftWoopeDescription.setText(store.describeCountDiscountCode);
                    }

                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    private void reportStore(long branchId) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();
        Call<ApiResponse> call =
                providerApiInterface.notCooperating("bearer " + authToken, branchId);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgreeBar();
                int code = response.code();

                if (response.body().getStatus() == 101) {

                    Utility.showSnackbar(layout, "عدم همکاری فروشگاه با موفقیت گزارش شد", Snackbar.LENGTH_LONG);

                } else if (response.body().getStatus() == 202) {

                    Utility.showSnackbar(layout, "خطا در ثبت گزارش", Snackbar.LENGTH_LONG);

                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideProgreeBar();
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });
    }

    private void addSocial(String key, String value) {
        //set the properties for button
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 15, 15, 15);
        cardView.setLayoutParams(params);
        cardView.setVisibility(View.VISIBLE);
        cardView.setCardElevation(5);
        cardView.setRadius(5);

        ImageView icon = new ImageView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(125, 125);
        layoutParams.gravity = Gravity.RIGHT;
        icon.setLayoutParams(layoutParams);

        if (key.equals("وبسایت")) {
            Drawable img = getContext().getResources().getDrawable(R.drawable.website_icon);
            icon.setBackground(img);
        } else if (key.equals("تلگرام")) {
            Drawable img = getContext().getResources().getDrawable(R.drawable.telegram_icon);
            icon.setBackground(img);
        } else if (key.equals("اینستاگرام")) {
            Drawable img = getContext().getResources().getDrawable(R.drawable.instagram_icon);
            icon.setBackground(img);
        } else if (key.equals("توئیتر")) {
            Drawable img = getContext().getResources().getDrawable(R.drawable.twiiter_icon);
            icon.setBackground(img);
        } else if (key.equals("فیسبوک")) {
            Drawable img = getContext().getResources().getDrawable(R.drawable.facebook_icon);
            icon.setBackground(img);
        }

        //add button to the layout
        cardView.addView(icon);

        TextView text = new TextView(getContext());
        FrameLayout.LayoutParams layoutParamss = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamss.gravity = Gravity.CENTER;
//        layoutParamss.setMargins(10,10,10,10);
        text.setLayoutParams(layoutParamss);
        if (key.equals("وبسایت")) {
            text.setText(value);
        } else if (key.equals("تلگرام")) {
            text.setText(value);

        } else if (key.equals("اینستاگرام")) {
            text.setText(value);

        } else if (key.equals("توئیتر")) {
            text.setText(value);

        } else if (key.equals("فیسبوک")) {
            text.setText(value);

        } else {
            text.setText(key + " " + value);
        }

        text.setTextColor(getResources().getColor(R.color.black));

        cardView.addView(text);

        socialsLayout.addView(cardView);
    }

    private void sendOnlineRequestStore(long branchId) {

        final Snackbar snack = Snackbar.make(layout, R.string.sending_vip_request, 999999999);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);

        tv.setGravity(Gravity.CENTER_HORIZONTAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        snack.show();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        showProgreeBar();

        Call<ApiResponse> call =
                providerApiInterface.sendOnlineRequestStore("bearer " + authToken, branchId);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                hideProgreeBar();
                snack.dismiss();
                Utility.showPayDialog(getContext(), response.body().getMessage());

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                hideProgreeBar();
                snack.dismiss();
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

            }
        });

    }

    private void shareStore(long branchId) {

        giftWoopeLayout.setEnabled(false);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();
        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<ApiResponse> call =
                providerApiInterface.shareStore("bearer " + authToken, branchId);

        showShareProgreeBar();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideShareProgreeBar();
                int code = response.body().status;
                giftWoopeLayout.setEnabled(true);
                if (code == 101) {

                    shareText(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                giftWoopeLayout.setEnabled(true);
                //Toast.makeText(getActivity(), "failure", Toast.LENGTH_LONG).show();
                hideShareProgreeBar();
                Utility.showSnackbar(getActivity().findViewById(R.id.activity_store_layout), R.string.network_error, Snackbar.LENGTH_LONG);
            }
        });
    }

    private void shareText(String text) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");// Plain format text

        // You can add subject also
        /*
         * sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT,
         * "Subject Here");
         */
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share Text Using"));
    }

    public void hideShareProgreeBar() {
        shareLoading.hide();
        giftWoopeDescription.setVisibility(View.VISIBLE);
    }

    public void showShareProgreeBar() {
        shareLoading.smoothToShow();
        giftWoopeDescription.setVisibility(View.GONE);
    }


}
package ir.woope.woopeapp.ui.Fragments;

import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Activities.StoreActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.STORE;

public class storeInfoFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity().getIntent() != null && getActivity().getIntent().getExtras() != null) {
//            profile = (Profile) getIntent().getExtras().getSerializable(PREF_PROFILE);
            store = (Store) getActivity().getIntent().getExtras().getSerializable(STORE);
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

    String authToken = null;

    Store store;

    public storeInfoFragment() {
        // Required empty public constructor
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
        store_discount = rootView.findViewById(R.id.store_discount);
        point_desc = rootView.findViewById(R.id.point_desc);
        store_address = rootView.findViewById(R.id.store_address);
        store_phones = rootView.findViewById(R.id.store_phones);
        desc_layout = rootView.findViewById(R.id.desc_layout);
        point_layout = rootView.findViewById(R.id.point_layout);
        store_phones_layout = rootView.findViewById(R.id.store_phones_layout);
        store_address_layout = rootView.findViewById(R.id.store_address_layout);
        report_store_layout = rootView.findViewById(R.id.report_store_layout);

        report_store_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // item clicked

                Snackbar snackbar = Snackbar
                        .make(layout, "آیا مطمعنید؟", Snackbar.LENGTH_LONG)
                        .setAction("بله", new View.OnClickListener() {
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
                        point_desc.setText("به ازای هر " + store.basePrice + " تومان خرید " + store.returnPoint + " ووپ دریافت می‌کنید");
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

}
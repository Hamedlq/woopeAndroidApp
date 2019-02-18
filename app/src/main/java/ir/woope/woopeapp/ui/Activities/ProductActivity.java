package ir.woope.woopeapp.ui.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.StoreInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.StoreGalleryItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;

public class ProductActivity extends AppCompatActivity {

    ImageView productImage;
    TextView productDescription;

    StoreGalleryItem product;

    Context context;

    String authToken;

    TextView LikeCount;

    SparkButton likeButton;

    View layout;

    AVLoadingIndicatorView loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_product);
        context = this;
        productImage = findViewById(R.id.productImage);
        productDescription = findViewById(R.id.productDescription);
        LikeCount = findViewById(R.id.txt_likeCount);
        layout = findViewById(R.id.productActivity_layout);
        likeButton = findViewById(R.id.product_likebutton);

        loading = findViewById(R.id.loading_product);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.product_toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow);

        if (getIntent() != null && getIntent().getExtras() != null) {

            product = (StoreGalleryItem) getIntent().getSerializableExtra("product");

            getProduct(product.id, product.branchId);

        }

//        likeButton.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(LikeButton likeButton) {
//
//            }
//
//            @Override
//            public void unLiked(LikeButton likeButton) {
//
//            }
//        });

        likeButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState) {
                    // Button is active
                    LikeImage(product.id);
                } else {
                    // Button is inactive
                    LikeImage(product.id);
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void LikeImage(long ImageId) {

        loading.smoothToShow();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<StoreGalleryItem> call =
                providerApiInterface .LikeImage("bearer " + authToken, ImageId);

        call.enqueue(new Callback<StoreGalleryItem>() {
            @Override
            public void onResponse(Call<StoreGalleryItem> call, Response<StoreGalleryItem> response) {

                int code = response.code();
                if (code == 200) {

                    loading.smoothToHide();

                    if (response.body().countLike==0) {
                        LikeCount.setText("");

                    } else {
                        LikeCount.setText(response.body().countLike + " " + getResources().getString(R.string.like));

                    }


                }
            }

            @Override
            public void onFailure(Call<StoreGalleryItem> call, Throwable t) {
                loading.smoothToHide();
                likeButton.setChecked(false);

                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);


//                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void getProduct(long productId, long branchId) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        StoreInterface providerApiInterface =
                retrofit.create(StoreInterface.class);

        SharedPreferences prefs =
                getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");

        Call<List<StoreGalleryItem>> call =
                providerApiInterface.getActiveBranchSingleProduct("bearer " + authToken, productId, branchId, 0, 2);

        loading.smoothToShow();

        call.enqueue(new Callback<List<StoreGalleryItem>>() {
            @Override
            public void onResponse(Call<List<StoreGalleryItem>> call, Response<List<StoreGalleryItem>> response) {

                int code = response.code();
                if (code == 200) {

                    loading.smoothToHide();

                    StoreGalleryItem product = response.body().get(0);

                    Picasso.with(context).load(Constants.GlobalConstants.LOGO_URL + product.productImageAddress).fit().centerInside().into(productImage);

                    productDescription.setText(product.description);

                    if (product.isLiked) {
                        likeButton.setChecked(true);
                    } else {
                        likeButton.setChecked(false);
                    }

                    if (product.countLike == 0) {
                        LikeCount.setText("");

                    } else {
                        LikeCount.setText(product.countLike + " " + getResources().getString(R.string.like));
                    }

                }
            }

            @Override
            public void onFailure(Call<List<StoreGalleryItem>> call, Throwable t) {
//                    searchInProgress = false;
                Utility.showSnackbar(layout, R.string.network_error, Snackbar.LENGTH_LONG);

                loading.smoothToHide();

//                    Utility.showSnackbar(layout, t.getMessage(), Snackbar.LENGTH_LONG);
//                    itShouldLoadMore = true;

            }
        });

    }

}



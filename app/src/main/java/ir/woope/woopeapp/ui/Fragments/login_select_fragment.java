package ir.woope.woopeapp.ui.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.Utils.RevealBackgroundView;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.ui.Activities.EditProfileActivity;
import ir.woope.woopeapp.ui.Activities.LoginActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.SplashSelectActivity;
import ir.woope.woopeapp.ui.Activities.TransHistoryActivity;
import ir.woope.woopeapp.ui.Activities.UserRegisterActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.FIRST_RUN_PROFILE_FRAGMENT;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;


/**
 * Created by Hamed on 6/11/2018.
 */

public class login_select_fragment extends Fragment {


    @BindView(R.id.btn_enter_login)
    protected MaterialRippleLayout btn_enter_login;
    @BindView(R.id.btn_register)
    protected MaterialRippleLayout btn_register;


    private View mRecycler;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private String userChoosenTask;
    boolean isImageUploaded = false;
    Toolbar toolbar;
    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    //private Unbinder mUnbinder;
    View vUserProfileRoot;



    View layout;

//    private ArrayList<Image> list = new ArrayList<Image>();
//
//    public void addImage(ArrayList<Image> list) {
//        this.list.clear();
//        this.list.addAll(list);
//
//    }

    Retrofit retrofit_splash;

    TabLayout user_tabs;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getActivity().setTitle("");

        mRecycler = inflater.inflate(R.layout.fragment_login_select, container, false);
        ButterKnife.bind(this, mRecycler);
        setHasOptionsMenu(true);

        LinearLayout linearLayout = (LinearLayout) mRecycler.findViewById(R.id.vUserStats);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        vUserProfileRoot = mRecycler.findViewById(R.id.vUserProfileRoot);
        //Initializing viewPager
        //viewPager = (ViewPager) mRecycler.findViewById(R.id.pager);
        btn_register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_register = new Intent(getActivity(),
                        UserRegisterActivity.class);
                {
                    startActivity(goto_register);
                    //getActivity().finish();
                }
            }
        });

        btn_enter_login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent goto_login = new Intent(getActivity(),
                        LoginActivity.class);
                {
                    startActivity(goto_login);
                    //getActivity().finish();
                }
            }
        });
        return mRecycler;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        }
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = getView().findViewById(R.id.activity_fragment_user_profile);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_edit_profile:
                Intent giftIntent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivityForResult(giftIntent, SHOULD_GET_PROFILE);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}

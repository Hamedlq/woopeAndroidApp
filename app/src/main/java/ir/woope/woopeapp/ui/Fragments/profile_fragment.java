package ir.woope.woopeapp.ui.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.Utils.RevealBackgroundView;
import ir.woope.woopeapp.adapters.ProfilePageAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.ui.Activities.EditProfileActivity;
import ir.woope.woopeapp.ui.Activities.LoginActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.SplashActivity;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.REQUEST_CAMERA;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SELECT_FILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

/**
 * Created by Hamed on 6/11/2018.
 */

public class profile_fragment extends Fragment implements TabLayout.OnTabSelectedListener, RevealBackgroundView.OnStateChangeListener{

    private View mRecycler;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private String userChoosenTask;
    boolean isImageUploaded=false;

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    //private Unbinder mUnbinder;

    //@BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    //@BindView(R.id.rvUserProfile)
    //RecyclerView rvUserProfile;

    //@BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;
    //This is our viewPager
    private ViewPager viewPager;

    //@BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    //@BindView(R.id.vUserDetails)
    View vUserDetails;
    //@BindView(R.id.btnFollow)
    Button btnEdit;
    Button btnlogout;
    //@BindView(R.id.vUserStats)
    View vUserStats;
    //@BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;

    TextView userNameFamily;
    TextView username;
    TextView userBio;
    TextView cashCredit;
    TextView woopeCredit;
    TextView useNumber;


    private int avatarSize;
    private String profilePhoto;

    SharedPreferences settings;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_user_profile, container,false);
        //mUnbinder=ButterKnife.bind(this,mRecycler);
        vRevealBackground=mRecycler.findViewById(R.id.vRevealBackground);
        userNameFamily=mRecycler.findViewById(R.id.userNameFamily);
        username=mRecycler.findViewById(R.id.username);
        userBio=mRecycler.findViewById(R.id.userBio);
        cashCredit=mRecycler.findViewById(R.id.cashCredit);
        woopeCredit=mRecycler.findViewById(R.id.woope_credit);
        useNumber=mRecycler.findViewById(R.id.transactionCount);


        //rvUserProfile=mRecycler.findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto=mRecycler.findViewById(R.id.ivUserProfilePhoto);
        tlUserProfileTabs=mRecycler.findViewById(R.id.tlUserProfileTabs);
        vUserDetails=mRecycler.findViewById(R.id.vUserDetails);
        btnEdit=mRecycler.findViewById(R.id.btnEditProfile);
        btnlogout=mRecycler.findViewById(R.id.btn_logout_editprofile);
        vUserStats=mRecycler.findViewById(R.id.vUserStats);
        vUserProfileRoot=mRecycler.findViewById(R.id.vUserProfileRoot);
        //Initializing viewPager
        viewPager = (ViewPager) mRecycler.findViewById(R.id.pager);
        return mRecycler;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnEdit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0){

                Intent goto_edit = new Intent(getActivity(),
                        EditProfileActivity.class);
                startActivity(goto_edit);

            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0){

                SharedPreferences settings = getContext().getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(TOKEN, null);
                editor.apply();

                Intent goto_splash = new Intent(getActivity(),
                        SplashActivity.class);
                getActivity().finish();
                startActivity(goto_splash);

            }
        });

        Profile pp=((MainActivity)getActivity()).getUserProfile();
        if(pp==null){
            userNameFamily.setText("");
            username.setText("");
            userBio.setText("");
            cashCredit.setText("0");
            woopeCredit.setText("0");
            useNumber.setText("0");
        }else {
            userNameFamily.setText(pp.getName()+" "+pp.getFamily());
            username.setText(pp.getUsername());
            userBio.setText(pp.getUserBio());
            cashCredit.setText(pp.getCreditString());
            woopeCredit.setText(pp.getWoopeCreditString());
            useNumber.setText(pp.getUseNumberString());
        }
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        //this.profilePhoto = getString(R.string.user_profile_photo);

        Picasso.with(getActivity())
                .load(R.drawable.album2)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);

        ivUserProfilePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP){
                    selectImage();
                }
                return true;
            }
        });

        setupUserProfileGrid();

        setupTabs();

        setupRevealBackground(savedInstanceState);
    }


    private void setupTabs() {

        tlUserProfileTabs.setupWithViewPager(viewPager);

        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_bookmark));
        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
    }

    private void setupUserProfileGrid() {

        //Creating our pager adapter
        ProfilePageAdapter adapter = new ProfilePageAdapter(getActivity().getSupportFragmentManager(), tlUserProfileTabs.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);


        //Adding onTabSelectedListener to swipe views

        tlUserProfileTabs.setOnTabSelectedListener(this) ;

        /*final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);*/

        //rvUserProfile.setAdapter(profileTabAdapter);
        /*rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                profileTabAdapter.setLockedAnimations(true);
            }
        });*/

        /*rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });*/
    }

    private void selectImage() {
        final CharSequence[] items = {"با دوربین", "انتخاب از گالری",
                "نه فعلا"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("افزودن عکس");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = ir.aronapp.supplierapp.Helpers.Utility.checkPermission(getActivity());
                if (items[item].equals("با دوربین")) {
                    userChoosenTask = "با دوربین";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("انتخاب از گالری")) {
                    userChoosenTask = "انتخاب از گالری";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("نه فعلا")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        ((MainActivity)getActivity()).galleryIntent();
    }

    private void cameraIntent() {
        ((MainActivity)getActivity()).cameraIntent();
    }



    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            //final int[] startingLocation = getActivity().getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            final int[] startingLocation ={0,0,0};
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            viewPager.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            ProfilePageAdapter adapter = new ProfilePageAdapter(getActivity().getSupportFragmentManager(), tlUserProfileTabs.getTabCount());
            viewPager.setAdapter(adapter);
            tlUserProfileTabs.getTabAt(0).setIcon(R.drawable.ic_bookmark);
            tlUserProfileTabs.getTabAt(1).setIcon(R.drawable.ic_list_white);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
        vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
        ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
        vUserDetails.setTranslationY(-vUserDetails.getHeight());
        vUserStats.setAlpha(0);

        vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    public void setValues(Profile pp) {
        userNameFamily.setText(pp.getName()+" "+pp.getFamily());
        username.setText(pp.getUsername());
        userBio.setText(pp.getUserBio());
        cashCredit.setText(pp.getCreditString());
        woopeCredit.setText(pp.getWoopeCreditString());
        useNumber.setText(pp.getUseNumberString());
    }

    public void setPhoto(String filePath){
        //ivUserProfilePhoto.setImageBitmap(bitmap);
        Picasso.with(getActivity())
                .load(Constants.GlobalConstants.LOGO_URL + filePath)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);
        isImageUploaded=true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}

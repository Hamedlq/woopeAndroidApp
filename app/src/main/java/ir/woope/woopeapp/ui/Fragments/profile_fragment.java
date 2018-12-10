package ir.woope.woopeapp.ui.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;


import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.Utils.RevealBackgroundView;
import ir.woope.woopeapp.adapters.ProfilePageAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.ui.Activities.EditProfileActivity;
import ir.woope.woopeapp.ui.Activities.LoginActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.SplashActivity;
import ir.woope.woopeapp.ui.Activities.TransHistoryActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.FIRST_RUN_PROFILE_FRAGMENT;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.REQUEST_CAMERA;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SELECT_FILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;


/**
 * Created by Hamed on 6/11/2018.
 */

public class profile_fragment extends Fragment implements RevealBackgroundView.OnStateChangeListener {

    private View mRecycler;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private String userChoosenTask;
    boolean isImageUploaded = false;
    Toolbar toolbar;
    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    //private Unbinder mUnbinder;

    //@BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    //@BindView(R.id.rvUserProfile)
    //RecyclerView rvUserProfile;

    //@BindView(R.id.tlUserProfileTabs)
    //TabLayout tlUserProfileTabs;
    //This is our viewPager
    //private ViewPager viewPager;

    //@BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    //@BindView(R.id.vUserDetails)
    View vUserDetails;
    //@BindView(R.id.btnFollow)
    Button btnEdit;
    //Button btnlogout;
    //@BindView(R.id.vUserStats)
    View vUserStats;
    //@BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;

    public TextView userNameFamily;
    public TextView username;
    public TextView userBio;
    public TextView cashCredit;
    public TextView woopeCredit;
    public TextView useNumber;
    public RelativeLayout history_layout;


    private int avatarSize;
    private String profilePhoto;

    SharedPreferences settings;
    String token;

    Retrofit uploader;

    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    //captured picture uri
    private Uri picUri;

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private AlertDialog dialog;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    File f;


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

//        contextOfApplication = getApplicationContext();
        settings = getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        token = settings.getString(TOKEN, null);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getActivity().setTitle("");

        mRecycler = inflater.inflate(R.layout.fragment_user_profile, container, false);
        setHasOptionsMenu(true);
        //mUnbinder=ButterKnife.bind(this,mRecycler);
        vRevealBackground = mRecycler.findViewById(R.id.vRevealBackground);
        userNameFamily = mRecycler.findViewById(R.id.userNameFamily);
        username = mRecycler.findViewById(R.id.username);
        userBio = mRecycler.findViewById(R.id.userBio);
        cashCredit = mRecycler.findViewById(R.id.cashCredit);
        woopeCredit = mRecycler.findViewById(R.id.woope_credit);
        useNumber = mRecycler.findViewById(R.id.transactionCount);

        history_layout= mRecycler.findViewById(R.id.history_layout);
        //rvUserProfile=mRecycler.findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto = mRecycler.findViewById(R.id.ivUserProfilePhoto);
        //tlUserProfileTabs = mRecycler.findViewById(R.id.tlUserProfileTabs);
        vUserDetails = mRecycler.findViewById(R.id.vUserDetails);
        //btnEdit = mRecycler.findViewById(R.id.btnEditProfile);
        //btnlogout = mRecycler.findViewById(R.id.btn_logout_editprofile);
        vUserStats = mRecycler.findViewById(R.id.vUserStats);
        vUserProfileRoot = mRecycler.findViewById(R.id.vUserProfileRoot);
        //Initializing viewPager
        //viewPager = (ViewPager) mRecycler.findViewById(R.id.pager);
        toolbar = (Toolbar) mRecycler.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        return mRecycler;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        history_layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent goto_history = new Intent(getActivity(),
                        TransHistoryActivity.class);
                getActivity().startActivityForResult(goto_history, 110);

            }
        });
        Profile pp = ((MainActivity) getActivity()).getUserProfile();
        if (pp == null) {
            userNameFamily.setText("");
            username.setText("");
            userBio.setText("");
            cashCredit.setText("0");
            woopeCredit.setText("0");
            useNumber.setText("0");
        } else {
            userNameFamily.setText(pp.getName() + " " + pp.getFamily());
            username.setText(pp.getUsername());
            userBio.setText(pp.getUserBio());
            cashCredit.setText(pp.getCreditString());
            woopeCredit.setText(pp.getWoopeCreditString());
            useNumber.setText(pp.getUseNumberString());
        }

        ivUserProfilePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                selectImage();
                return false;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 110) {

            UpdateProfileDetails();

        }
    }//onActivityResult

    private void selectImage() {
        final CharSequence[] items = {"با دوربین", "انتخاب از گالری",
                "نه فعلا"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("افزودن عکس");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());
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
        ((MainActivity) getActivity()).galleryIntent();
    }

    private void cameraIntent() {
        ((MainActivity) getActivity()).cameraIntent();
    }

    private Bitmap bitmap;

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        //this.profilePhoto = getString(R.string.user_profile_photo);

        //setupUserProfileGrid();

       //setupTabs();

        setupRevealBackground(savedInstanceState);

        GetProfilePicture();


        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

        boolean isFirstRun = prefs.getBoolean(FIRST_RUN_PROFILE_FRAGMENT, true);
        if (isFirstRun)
        {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                view.post(new Runnable() {
                    @Override
                    public void run() {
                            // Code to run once
                           //showHint();
                    }
                });
            }
        }, 2000);
        }
    }

    /*private void showHint () {

        final TapTargetSequence sequence = new TapTargetSequence(getActivity())
                .targets(
                        // Likewise, this tap target will target the search button
                        TapTarget.forView(((ViewGroup) tlUserProfileTabs.getChildAt(0)).getChildAt(0), "مورد علاقه ها", "در اینجا لیستی از مورد علاقه های خود را ببینید")
                                // All options below are optional
                                .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(14)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60),
                        TapTarget.forView(((ViewGroup) tlUserProfileTabs.getChildAt(0)).getChildAt(1), "تاریخچه پرداخت ها", "در اینجا لیستی از پرداخت های اخیر خود را ببینید")
                                // All options below are optional
                                .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(14)            // Specify the size (in sp) of the description text
                                .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60)
                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        SharedPreferences prefs =
                                getActivity().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(FIRST_RUN_PROFILE_FRAGMENT, false);
                        editor.commit();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        final AlertDialog dialog = new AlertDialog.Builder(PayActivity.this)
//                                .setTitle("Uh oh")
//                                .setMessage("You canceled the seque.setPositiveButton("Oops", null).show();nce")
//
//                        TapTargetView.showFor(dialog,
//                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
//                                        .cancelable(false)
//                                        .tintTarget(false), new TapTargetView.Listener() {
//                                    @Override
//                                    public void onTargetClick(TapTargetView view) {
//                                        super.onTargetClick(view);
//                                        dialog.dismiss();
//                                    }
//                                });
                    }
                });

        sequence.start();

    }*/

    /*private void setupTabs() {

        tlUserProfileTabs.setupWithViewPager(viewPager);

        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_dots));
        //tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_dots));

    }*/

   /* private void setupUserProfileGrid() {

        //Creating our pager adapter
        ProfilePageAdapter adapter = new ProfilePageAdapter(getActivity().getSupportFragmentManager(), tlUserProfileTabs.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);


        //Adding onTabSelectedListener to swipe views

        tlUserProfileTabs.setOnTabSelectedListener(this);

        *//*final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);*//*

        //rvUserProfile.setAdapter(profileTabAdapter);
        *//*rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                profileTabAdapter.setLockedAnimations(true);
            }
        });*//*

        *//*rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });*//*
    }*/

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            //final int[] startingLocation = getActivity().getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            final int[] startingLocation = {0, 0, 0};
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
            //viewPager.setVisibility(View.VISIBLE);
            //tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            //ProfilePageAdapter adapter = new ProfilePageAdapter(getActivity().getSupportFragmentManager(), tlUserProfileTabs.getTabCount());
            //viewPager.setAdapter(adapter);
            //tlUserProfileTabs.getTabAt(0).setIcon(R.drawable.ic_dots);
            //tlUserProfileTabs.getTabAt(1).setIcon(R.drawable.ic_dots);
            //animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            //tlUserProfileTabs.setVisibility(View.INVISIBLE);
            //viewPager.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

   /* private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }*/

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

    @SuppressLint("SetTextI18n")
    public void setValues(Profile pp) {
        userNameFamily.setText(pp.getName() + " " + pp.getFamily());
        username.setText(pp.getUsername());
        userBio.setText(pp.getUserBio());
        cashCredit.setText(pp.getCreditString());
        woopeCredit.setText(pp.getWoopeCreditString());
        useNumber.setText(pp.getUseNumberString());
    }

    public void setPhoto(String filePath) {
        //ivUserProfilePhoto.setImageBitmap(bitmap);

        Picasso.with(getActivity())
                .load(Constants.GlobalConstants.LOGO_URL + filePath)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);

        isImageUploaded = true;
    }

/*    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }*/

    private void uploadFile(Uri fileUri) {
        // create upload service client

        uploader = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ProfileInterface uploadprofile = uploader.create(ProfileInterface.class);

        // use the FileUtils to get the actual file by uri
        File file = new File(fileUri.getPath());

        // create RequestBody instance from file
        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), fbody);

        // finally, execute the request
        Call<ApiResponse> call = uploadprofile.updateImage(body, "bearer " + token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {

                if (response.body().getStatus() == 101) {
                    Toast.makeText(
                            getActivity()
                            , "آپلود شد",
                            Toast.LENGTH_SHORT).show();
                    GetProfilePicture();
                } else {
                    Toast.makeText(
                            getActivity()
                            , "آپلود نشد",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(
                        getActivity()
                        , t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    public void GetProfilePicture() {

        Retrofit getProfile;

        getProfile = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        final SplashInterface splash = getProfile.create(SplashInterface.class);

        splash.check_connection("bearer " + token).enqueue(new Callback<Profile>() {

            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                if (response.code() == 200) {

                    if (!response.body().getImageSrc().equals("") && !response.body().getImageSrc().equals(null)) {

                        String imagesrc = response.body().getImageSrc();
                        setPhoto(imagesrc);

                    } else
                        Picasso.with(getActivity())
                                .load(R.drawable.group34)
                                .placeholder(R.drawable.img_circle_placeholder)
                                .resize(avatarSize, avatarSize)
                                .centerCrop()
                                .transform(new CircleTransformation())
                                .into(ivUserProfilePhoto);

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

                Toast.makeText(
                        getContext()
                        , t.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });

    }

    public void UpdateProfileDetails() {
        retrofit_splash = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        final SplashInterface splash = retrofit_splash.create(SplashInterface.class);

        final SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        final String token = settings.getString(TOKEN, null);

        splash.check_connection("bearer " + token).enqueue(new Callback<Profile>() {

            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                if (response.code() == 200) {

                    Profile user = response.body();


                    userNameFamily.setText(user.getName() + " " + user.getFamily());
                    username.setText(user.getUsername());
                    userBio.setText(user.getUserBio());
                    cashCredit.setText(user.getCreditString());
                    woopeCredit.setText(user.getWoopeCreditString());
                    useNumber.setText(user.getUseNumberString());

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

                Toast.makeText(
                        getContext()
                        , t.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_edit_profile:
                Intent giftIntent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(giftIntent);
                getActivity().overridePendingTransition(R.anim.slide_up,R.anim.no_change);
                break;
            default:
                break;
        }
        return true;
    }

}

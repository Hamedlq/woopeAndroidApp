package ir.woope.woopeapp.ui.Fragments;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.R;
import ir.woope.woopeapp.Utils.CircleTransformation;
import ir.woope.woopeapp.Utils.RevealBackgroundView;
import ir.woope.woopeapp.adapters.ProfilePageAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.ui.Activities.EditProfileActivity;
import ir.woope.woopeapp.ui.Activities.LoginActivity;
import ir.woope.woopeapp.ui.Activities.MainActivity;
import ir.woope.woopeapp.ui.Activities.SplashActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;


/**
 * Created by Hamed on 6/11/2018.
 */

public class profile_fragment extends Fragment implements TabLayout.OnTabSelectedListener, RevealBackgroundView.OnStateChangeListener {

    private View mRecycler;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private String userChoosenTask;
    boolean isImageUploaded = false;

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
    String token;

    Retrofit uploader;

    private Context context;



    private ArrayList<Image> list = new ArrayList<Image>();

    public void addImage(ArrayList<Image> list) {
        this.list.clear();
        this.list.addAll(list);

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        contextOfApplication = getApplicationContext();
        settings=getContext().getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        token = settings.getString(TOKEN, null);

    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.fragment_user_profile, container, false);
        //mUnbinder=ButterKnife.bind(this,mRecycler);
        vRevealBackground = mRecycler.findViewById(R.id.vRevealBackground);
        userNameFamily = mRecycler.findViewById(R.id.userNameFamily);
        username = mRecycler.findViewById(R.id.username);
        userBio = mRecycler.findViewById(R.id.userBio);
        cashCredit = mRecycler.findViewById(R.id.cashCredit);
        woopeCredit = mRecycler.findViewById(R.id.woope_credit);
        useNumber = mRecycler.findViewById(R.id.transactionCount);


        //rvUserProfile=mRecycler.findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto = mRecycler.findViewById(R.id.ivUserProfilePhoto);
        tlUserProfileTabs = mRecycler.findViewById(R.id.tlUserProfileTabs);
        vUserDetails = mRecycler.findViewById(R.id.vUserDetails);
        btnEdit = mRecycler.findViewById(R.id.btnEditProfile);
        btnlogout = mRecycler.findViewById(R.id.btn_logout_editprofile);
        vUserStats = mRecycler.findViewById(R.id.vUserStats);
        vUserProfileRoot = mRecycler.findViewById(R.id.vUserProfileRoot);
        //Initializing viewPager
        viewPager = (ViewPager) mRecycler.findViewById(R.id.pager);
        return mRecycler;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnEdit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent goto_edit = new Intent(getActivity(),
                        EditProfileActivity.class);
                startActivity(goto_edit);

            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

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

        final int requestcode = 1151;

        ivUserProfilePhoto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

//                Pix.start(profile_fragment.this, requestcode);

//                final BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.woope.woopeapp.fileprovider")
//                        .setMaximumDisplayingImages(24) //Default: Integer.MAX_VALUE. Don't worry about performance :)
//                        .setSpanCount(5) //Default: 3. This is the number of columns
//                        .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
//                        .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
//                        .hideCameraTile() //Default: show. Set this if you don't want user to take photo.
//                        .hideGalleryTile()//Default: show. Set this if you don't want to further let user select from a gallery app. In such case, I suggest you to set maximum     displaying    images to Integer.MAX_VALUE.
//                        .build();
//                singleSelectionPicker.show(getFragmentManager(), "picker");

                ImagePicker.with(profile_fragment.this) //  Initialize ImagePicker with activity or fragment context
                        .setProgressBarColor("#4CAF50")     //  ProgressBar color
                        .setBackgroundColor("#212121")      //  Background color
                        .setCameraOnly(false)               //  Camera mode
                        .setMultipleMode(false)             //  Select multiple images or single image
                        .setFolderMode(true)                //  Folder mode
                        .setShowCamera(true)                //  Show camera button
                        .setFolderTitle("")                 //  Folder title (works with FolderMode = true)
                        .setImageTitle("")                  //  Image title (works with FolderMode = false)
                        .setDoneTitle("انتخاب")             //  Done button title
                        .setMaxSize(1)                      //  Max images can be selected
                        .setSavePath("ImagePicker")         //  Image capture folder name
                        .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                        .setKeepScreenOn(true)              //  Keep screen on when selecting images
                        .start();                           //  Start ImagePicker

            }
        });
    }

    private Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1151) {
//            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
//            addImage(returnValue);
//            File f = new File(list.get(0));
//            cropimage(Uri.fromFile(f));
//
//        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                uploadFile(result.getUri());

        }

        if (requestCode == Config.RC_PICK_IMAGES && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            final Image image = images.get(0);
            File f = new File(image.getPath());
            cropimage(Uri.fromFile(f));
        }
        super.onActivityResult(requestCode, resultCode, data);  // You MUST have this line to be here
        // so ImagePicker can work with fragment mode
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        //this.profilePhoto = getString(R.string.user_profile_photo);

        setupUserProfileGrid();

        setupTabs();

        setupRevealBackground(savedInstanceState);

        GetProfilePicture();
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

        tlUserProfileTabs.setOnTabSelectedListener(this);

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
                        , "خطای اتصال",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cropimage(Uri imgUri){
        CropImage.activity(imgUri)
                .setCropMenuCropButtonTitle("برش")
                .setAspectRatio(1,1)
                .setAllowFlipping(false)
                .start(getContext(), this);
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

                    if(!response.body().getImageSrc().equals("")) {
                        String imagesrc = response.body().getImageSrc();
                        setPhoto(imagesrc);
                    }
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


}

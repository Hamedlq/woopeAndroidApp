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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import ir.woope.woopeapp.Manifest;
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
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.MY_SHARED_PREFERENCES;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.REQUEST_CAMERA;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SELECT_FILE;
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
    //Button btnlogout;
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
        //btnlogout = mRecycler.findViewById(R.id.btn_logout_editprofile);
        vUserStats = mRecycler.findViewById(R.id.vUserStats);
        vUserProfileRoot = mRecycler.findViewById(R.id.vUserProfileRoot);
        //Initializing viewPager
        viewPager = (ViewPager) mRecycler.findViewById(R.id.pager);
        return mRecycler;
    }

    @SuppressLint("SetTextI18n")
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

        /*btnlogout.setOnClickListener(new View.OnClickListener() {

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
        });*/

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

       /* ArrayList<String> pickImageDialogOptions = new ArrayList<>();
        pickImageDialogOptions.add("انتخاب عکس از گالری");
        pickImageDialogOptions.add("گرفتن عکس با دوربین");*/

        ivUserProfilePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                selectImage();
                return false;
            }
        });



        /*ivUserProfilePhoto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // setup the alert builder
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

// add a list
                String[] options = {"انتخاب عکس از گالری", "گرفتن عکس با دوربین"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0: {

                                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickIntent.setType("image/*");
                                startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), PICK_FROM_FILE);

                                break;

                            }

                            case 1: {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
                                }

                                break;

                            }
                        }
                    }
                });

                //create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

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
        ((MainActivity)getActivity()).galleryIntent();
    }

    private void cameraIntent() {
        ((MainActivity)getActivity()).cameraIntent();
    }

    private Bitmap bitmap;

    /*@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_FROM_FILE) {

                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    doCrop(selectedImageUri);

                }

            } else if (requestCode == CROP) {

                Uri selectedImageURI = data.getData();
                File imageFile = new File(getRealPathFromURI(selectedImageURI));

                uploadFile(Uri.fromFile(imageFile));

                //File file = FileUtils.getFile(this, fileUri);


            } else if (requestCode == PICK_FROM_CAMERA) {

                if (data != null) {

                    Uri selectedImageUri = data.getData();
                    doCrop(selectedImageUri);

                }


            }
        }

    }*/

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





        //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
//                Intent cameraIntent = new
//                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
//            } else {
//                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
//            }
//
//        }
    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
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

                    if (!response.body().getImageSrc().equals("")) {
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

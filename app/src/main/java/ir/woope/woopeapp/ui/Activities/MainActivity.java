package ir.woope.woopeapp.ui.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.gson.Gson;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.helpers.Utility;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.home_fragment;
import ir.woope.woopeapp.ui.Fragments.profileBookmarkFragment;
import ir.woope.woopeapp.ui.Fragments.profile_fragment;
import ir.woope.woopeapp.ui.Fragments.profile_home_fragment;
import ir.woope.woopeapp.ui.Fragments.search_fragment;
import me.toptas.fancyshowcase.FancyShowCaseView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.CROP_IMAGE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PREF_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.REQUEST_CAMERA;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SELECT_FILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SHOULD_GET_PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

import co.ronash.pushe.Pushe;

public class MainActivity extends AppCompatActivity {


    boolean doubleBackToExitPressedOnce = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;

    //private TextView mTextMessage;
    String HOME_FRAGMENT = "HomeFragment";
    String SEARCH_FRAGMENT = "SearchFragment";
    String PROFILE_FRAGMENT = "ProfileFragment";
    String PRODUCT_HOME_FRAGMENT = "ProductHomeFragment";
    boolean getProfileFromServer = false;
    String authToken = null;
    Profile profile = null;
    FloatingActionButton fab;

    View nav_store;

    private long mLastClickTime = 0;

    boolean IsOnHome = false, IsOnSearch = false, IsOnProfile = false, IsOnFavorite = false;

    UCrop.Options options;
    private String pictureImagePath = "";

    View layout;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (!IsOnHome) {
                        //mTextMessage.setText(R.string.title_home);
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            break;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, new home_fragment(), HOME_FRAGMENT)
                                .commit();
                        IsOnHome = true;
                        IsOnSearch = false;
                        IsOnProfile = false;
                        IsOnFavorite = false;
                        return true;
                    } else break;
                case R.id.navigation_‌search:
                    if (!IsOnSearch) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            break;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, new search_fragment(), SEARCH_FRAGMENT)
                                .commit();
                        IsOnSearch = true;
                        IsOnHome = false;
                        IsOnProfile = false;
                        IsOnFavorite = false;
                        return true;
                    } else break;
                case R.id.navigation_favorite:
                    if (!IsOnFavorite) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            break;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, new profileBookmarkFragment(), SEARCH_FRAGMENT)
                                .commit();
                        IsOnFavorite = true;
                        IsOnSearch = false;
                        IsOnHome = false;
                        IsOnHome = false;
                        IsOnProfile = false;
                        return true;
                    } else break;
                case R.id.navigation_profile:
                    if (!IsOnProfile) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            break;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        //mTextMessage.setText(R.string.title_notifications);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, new profile_fragment(), PROFILE_FRAGMENT)
                                .commit();
                        IsOnProfile = true;
                        IsOnHome = false;
                        IsOnSearch = false;
                        IsOnFavorite = false;
                        return true;
                    } else break;
            }
            return false;
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.activity_main);
        if (getIntent() != null && getIntent().getExtras() != null) {
            getProfileFromServer = getIntent().getBooleanExtra(GET_PROFILE_FROM_SERVER, false);
        }
        if (getProfileFromServer) {
            getProfileFromServer();
        } else {
            getUserProfile();
        }

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                final View iconView = item.findViewById(android.support.design.R.id.icon);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
                final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                iconView.setLayoutParams(layoutParams);
            }
        } catch (NoSuchFieldException e) {
            //Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            //Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        sendRegistrationToServer();
        //getProfileFromServer();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new home_fragment(), HOME_FRAGMENT)
                .commit();

        Pushe.initialize(this, true);

//        nav_store = findViewById(R.id.nav_store);

        //Toolbar tb = (Toolbar) findViewById(R.id.home_frag    `ment_toolbar);


//


//        nav_store_showcase = new FancyShowCaseView.Builder(this)
//                .focusOn(findViewById(R.id.nav_store))
//                .title("لیست پرداخت ها")
//                .showOnce("id0")
//                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new profile_home_fragment(), PRODUCT_HOME_FRAGMENT)
                        .commit();

                IsOnHome = false;
                IsOnSearch = false;
                IsOnProfile = false;
                IsOnFavorite = false;

//                Profile obj = getUserProfile();
//                Intent myIntent = new Intent(MainActivity.this, ProductHomeActivity.class);
//                myIntent.putExtra(PREF_PROFILE, obj);
//                startActivity(myIntent);
//                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
            }
        });

        options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setToolbarTitle("ویرایش عکس");
        options.setCompressionQuality(100);
        options.setMaxBitmapSize(10000);

    }


    public Profile getUserProfile() {
        if (profile == null) {
            Gson gson = new Gson();
            final SharedPreferences prefs =
                    this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
            String profileString = prefs.getString(Constants.GlobalConstants.PROFILE, null);
            if (profileString != null) {
                profile = (Profile) gson.fromJson(profileString, Profile.class);
                return profile;
            } else {
                return new Profile();
            }
        } else {
            return profile;
        }
    }

    public void getProfileFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        ProfileInterface providerApiInterface =
                retrofit.create(ProfileInterface.class);

        final SharedPreferences prefs =
                this.getSharedPreferences(Constants.GlobalConstants.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "null");
        Call<Profile> call =
                providerApiInterface.getProfileFromServer("bearer " + authToken);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                int code = response.code();
                if (code == 200) {


                    profile = response.body();
                    //profile=user.getMessage();
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(profile);
                    prefsEditor.putString(PROFILE, json);
                    prefsEditor.apply();
                    Fragment fragment = fragmentManager.findFragmentByTag(PROFILE_FRAGMENT);
                    if (fragment != null) {
                        ((profile_fragment) fragment).setValues(profile);
                    }

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

            }
        });

    }

    private void sendRegistrationToServer() {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        ProfileInterface profileInterface =
                retrofit.create(ProfileInterface.class);

        SharedPreferences prefs = getSharedPreferences(
                Constants.GlobalConstants.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "");
        try {

            String oneSignalToken = "1";
            String pusheToken = Pushe.getPusheId(this);

            Call<ApiResponse> call =
                    profileInterface.sendPushNotificationTokens("bearer " + authToken, oneSignalToken, pusheToken);

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    //Toast.makeText(MainActivity.this, "do", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    //Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {

        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(1);
            return;
        }
//        Toast.makeText(MainActivity.this, R.string.press_again_to_exit, Toast.LENGTH_LONG).show();
        Utility.showSnackbar(layout, R.string.press_again_to_exit, Snackbar.LENGTH_LONG);
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 4000);
    }

    public void galleryIntent() {

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "انتخاب عکس"), SELECT_FILE);
        }
    }

    public void cameraIntent() {
        if (checkPermission_image_capture(this)) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, REQUEST_CAMERA);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = timeStamp + ".jpg";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
            File file = new File(pictureImagePath);
            Uri outputFileUri = Uri.fromFile(file);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
//            else if (requestCode == CROP_IMAGE) {
//                File file = new File(getExternalCacheDir(), "tempItemFile.jpg");
//
//                //////////
//
////                File file = new File(Environment.getExternalStorageDirectory()+"/woopeCache/");
//                if (file.exists()) {
//                    String filePath = file.getPath();
//                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                    //Fragment fragment = fragmentManager.findFragmentByTag(PROFILE_FRAGMENT);
//                    sendPicToServer(bitmap, filePath);
//
//                    /*if(fragment !=null){
//                        ((profile_fragment) fragment).setPhoto(bitmap);
//                    }*/
//
//                }
//
//                ///////////
//
////                String path = data.getStringExtra(CropImage.IMAGE_PATH);
////
////                // if nothing received
////                if (path == null) {
////
////                    return;
////                }
////
////                // cropped bitmap
////                Bitmap bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
//
//
//
//            }

            } else if (requestCode == SHOULD_GET_PROFILE) {
                getProfileFromServer();
            } else if (requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);

                try {
                    sendPicToServer(MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri), resultUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
            }
        }
    }


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(getExternalCacheDir(), "tempItemFile.jpg");
//                File destination = new File(Environment.getExternalStorageDirectory()+"/woopeCache/");
                FileOutputStream fo;

                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
                Uri uri = Uri.fromFile(destination);
//                doCrop(uri);
                ////

                UCrop.of(uri, uri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(1024, 1024)
                        .withOptions(options)
                        .start(this);
                ////
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //imgView.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {

        File imgFile = new File(pictureImagePath);
        Bitmap thumbnail = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
        File destination = new File(getExternalCacheDir(), "tempItemFile.jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            Uri uri = Uri.fromFile(destination);
//            doCrop(uri);
            ///

            UCrop.of(uri, uri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(1024, 1024)
                    .withOptions(options)
                    .start(this);
            ///
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imgView.setImageBitmap(thumbnail);
    }

    private void doCrop(Uri picFileUri) {
        try {
//            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent photoPickerIntent = new Intent("com.android.camera.action.CROP");
            photoPickerIntent.setDataAndType(picFileUri, "image/*");
            //photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra("crop", "true");
            photoPickerIntent.putExtra("aspectX", 1);
            photoPickerIntent.putExtra("aspectY", 1);
//            photoPickerIntent.putExtra("outputX", 512);
//            photoPickerIntent.putExtra("outputY", 512);
            photoPickerIntent.putExtra("return-data", true);
            photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, picFileUri);
            photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(photoPickerIntent, CROP_IMAGE);

        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "امکان آپلود عکس در گوشی شما وجود ندارد";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
            Utility.showSnackbar(layout, errorMessage, Snackbar.LENGTH_LONG);
        } catch (Exception e) {
            // display an error message
            String errorMessage = "خطایی رخ داده است";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();

            Utility.showSnackbar(layout, errorMessage, Snackbar.LENGTH_LONG);
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("ذخیره خارجی", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    public boolean checkPermission_image_capture(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.CAMERA)) {
                    showCameraDialog("دوربین", context,
                            Manifest.permission.CAMERA);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("دسترسی مورد نیاز است");
        alertBuilder.setMessage(msg + " لطفا برای ذخیره عکس دسترسی بدهید");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public void showCameraDialog(final String msg, final Context context,
                                 final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("دسترسی مورد نیاز است");
        alertBuilder.setMessage(msg + " لطفا برای ذخیره عکس دسترسی بدهید");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nothing happen
                } else {

//                    Toast.makeText(MainActivity.this, "عدم دسترسی",
//                            Toast.LENGTH_SHORT).show();

                    Utility.showSnackbar(layout, R.string.noPermission, Snackbar.LENGTH_LONG);

                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nothing happen
                } else {

//                    Toast.makeText(MainActivity.this, "عدم دسترسی",
//                            Toast.LENGTH_SHORT).show();

                    Utility.showSnackbar(layout, R.string.noPermission, Snackbar.LENGTH_LONG);

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    private void sendPicToServer(final Bitmap bitmap, final String filePath) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.HTTP.BASE_URL)
                .build();

        ProfileInterface profileInterface =
                retrofit.create(ProfileInterface.class);

        SharedPreferences prefs = this.getSharedPreferences(
                Constants.GlobalConstants.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String authToken = prefs.getString(Constants.GlobalConstants.TOKEN, "");

        File file = new File(getExternalCacheDir(), "tempItemFile.jpg");

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);

        Call<ApiResponse> call =
                profileInterface.updateImage(body, "bearer " + authToken);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int code = response.code();
                if (code == 200) {
                    ApiResponse i = response.body();
                    Fragment fragment = fragmentManager.findFragmentByTag(PROFILE_FRAGMENT);
                    if (fragment != null) {
                        ((profile_fragment) fragment).setPhoto(i.getMessage());
                    }
                } else {
//                    Toast.makeText(MainActivity.this, R.string.upload_error, Toast.LENGTH_LONG).show();
                    Utility.showSnackbar(layout, R.string.upload_error, Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                Toast.makeText(MainActivity.this, R.string.upload_error, Toast.LENGTH_LONG).show();
                Utility.showSnackbar(layout, R.string.upload_error, Snackbar.LENGTH_LONG);
            }
        });
    }


}

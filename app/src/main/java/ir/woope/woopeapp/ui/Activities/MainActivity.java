package ir.woope.woopeapp.ui.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ir.woope.woopeapp.R;
import ir.woope.woopeapp.adapters.StoresAdapter;
import ir.woope.woopeapp.helpers.Constants;
import ir.woope.woopeapp.interfaces.ProfileInterface;
import ir.woope.woopeapp.interfaces.SplashInterface;
import ir.woope.woopeapp.models.ApiResponse;
import ir.woope.woopeapp.models.Profile;
import ir.woope.woopeapp.models.Store;
import ir.woope.woopeapp.ui.Fragments.home_fragment;
import ir.woope.woopeapp.ui.Fragments.profile_fragment;
import ir.woope.woopeapp.ui.Fragments.search_fragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ir.aronapp.supplierapp.Helpers.Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.GET_PROFILE_FROM_SERVER;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.PROFILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.RELOAD_LIST;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.REQUEST_CAMERA;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.SELECT_FILE;
import static ir.woope.woopeapp.helpers.Constants.GlobalConstants.TOKEN;

public class MainActivity extends AppCompatActivity {


    boolean doubleBackToExitPressedOnce = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    //private TextView mTextMessage;
    String HOME_FRAGMENT = "HomeFragment";
    String SEARCH_FRAGMENT = "SearchFragment";
    String PROFILE_FRAGMENT = "ProfileFragment";
    boolean getProfileFromServer = false;
    String authToken = null;
    Profile profile = null;
    private int CROP = 2;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, new home_fragment(), HOME_FRAGMENT)
                            .commit();
                    return true;
                case R.id.navigation_‌search:
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, new search_fragment(), SEARCH_FRAGMENT)
                            .commit();
                    return true;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.title_notifications);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, new profile_fragment(), PROFILE_FRAGMENT)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        sendRegistrationToServer();
        //getProfileFromServer();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new home_fragment(), HOME_FRAGMENT)
                .commit();

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

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String oneSignalToken = status.getSubscriptionStatus().getUserId();

        Call<ApiResponse> call =
                profileInterface.sendOneSignalToken("bearer " + authToken, oneSignalToken);

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
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(1);
            return;
        }
        Toast.makeText(MainActivity.this, R.string.press_again_to_exit, Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == CROP) {
                File file = new File(getExternalCacheDir(), "tempItemFile.jpg");
                if (file.exists()) {
                    String filePath = file.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    //Fragment fragment = fragmentManager.findFragmentByTag(PROFILE_FRAGMENT);
                    sendPicToServer(bitmap, filePath);
                    /*if(fragment !=null){
                        ((profile_fragment) fragment).setPhoto(bitmap);
                    }*/

                }
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
                FileOutputStream fo;

                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
                Uri uri = Uri.fromFile(destination);
                doCrop(uri);

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
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(getExternalCacheDir(), "tempItemFile.jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            Uri uri = Uri.fromFile(destination);
            doCrop(uri);
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
            photoPickerIntent.putExtra("outputX", 512);
            photoPickerIntent.putExtra("outputY", 512);
            photoPickerIntent.putExtra("return-data", true);
            photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, picFileUri);
            photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(photoPickerIntent, CROP);

        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "امکان آپلود عکس در گوشی شما وجود ندارد";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            // display an error message
            String errorMessage = "خطایی رخ داده است";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nothing happen
                } else {
                    Toast.makeText(MainActivity.this, "عدم دسترسی",
                            Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, R.string.upload_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.upload_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}

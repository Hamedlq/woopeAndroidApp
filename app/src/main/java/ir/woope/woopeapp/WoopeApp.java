package ir.woope.woopeapp;

import android.app.Application;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;


import ir.woope.woopeapp.helpers.FontsOverride;

public class WoopeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization


        //iransens font
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/IRANSans(FaNum)_Light.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/IRANSans(FaNum)_Light.ttf");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}

package com.example.currentweatherforecast;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainApplication extends Application implements
        Serializable, ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private static final String TAG = "MainApplication";

    private static MainApplication myApp;
    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    private static List<String> abbreviations=new ArrayList<>();
    private static List<String> languages=new ArrayList<>();
    private static Map<String, Integer> weatherIcons=new HashMap<>();

    @Override
    public void onCreate() {
        this.registerActivityLifecycleCallbacks(this);
        myApp = this;

        myApp.registerActivityLifecycleCallbacks(myApp);
        myApp.initLanguages();
        myApp.initWeatherDesc();

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();

        super.onCreate();
    }

    private void initLanguages(){
        if (abbreviations.size() <= 0) {
            abbreviations.add("af");
            abbreviations.add("al");
            abbreviations.add("ar");
            abbreviations.add("az");
            abbreviations.add("bg");
            abbreviations.add("ca");
            abbreviations.add("cz");
            abbreviations.add("da");
            abbreviations.add("de");
            abbreviations.add("el");
            abbreviations.add("en");
            abbreviations.add("eu");
            abbreviations.add("fa");
            abbreviations.add("fi");
            abbreviations.add("fr");
            abbreviations.add("gl");
            abbreviations.add("he");
            abbreviations.add("hi");
            abbreviations.add("hr");
            abbreviations.add("hu");
            abbreviations.add("id");
            abbreviations.add("it");
            abbreviations.add("ja");
            abbreviations.add("kr");
            abbreviations.add("la");
            abbreviations.add("lt");
            abbreviations.add("mk");
            abbreviations.add("no");
            abbreviations.add("nl");
            abbreviations.add("pl");
            abbreviations.add("pt");
            abbreviations.add("pt_br");
            abbreviations.add("ro");
            abbreviations.add("ru");
            abbreviations.add("sv");
            abbreviations.add("sk");
            abbreviations.add("sl");
            abbreviations.add("sp");
            abbreviations.add("sr");
            abbreviations.add("th");
            abbreviations.add("tr");
            abbreviations.add("ua");
            abbreviations.add("vi");
            abbreviations.add("zh_cn");
            abbreviations.add("zh_tw");
            abbreviations.add("zu");
        }
        if (languages.size() <= 0) {
            languages.add("Afrikaans");
            languages.add("Albanian");
            languages.add("Arabic");
            languages.add("Azerbaijani");
            languages.add("Bulgarian");
            languages.add("Catalan");
            languages.add("Czech");
            languages.add("Danish");
            languages.add("German");
            languages.add("Greek");
            languages.add("English");
            languages.add("Basque");
            languages.add("Persian (Farsi)");
            languages.add("Finnish");
            languages.add("French");
            languages.add("Galician");
            languages.add("Hebrew");
            languages.add("Hindi");
            languages.add("Croatian");
            languages.add("Hungarian");
            languages.add("Indonesian");
            languages.add("Italian");
            languages.add("Japanese");
            languages.add("Korean");
            languages.add("Latvian");
            languages.add("Lithuanian");
            languages.add("Macedonian");
            languages.add("Norwegian");
            languages.add("Dutch");
            languages.add("Polish");
            languages.add("Portuguese");
            languages.add("Português Brasil");
            languages.add("Romanian");
            languages.add("Russian");
            languages.add("Swedish");
            languages.add("Slovak");
            languages.add("Slovenian");
            languages.add("Spanish");
            languages.add("Serbian");
            languages.add("Thai");
            languages.add("Turkish");
            languages.add("Ukrainian");
            languages.add("Vietnamese");
            languages.add("Chinese Simplified");
            languages.add("Chinese Traditional");
            languages.add("Zulu");
        }
    }

    private void initWeatherDesc() {
        weatherIcons.put("01d", R.mipmap.icon_01d);
        weatherIcons.put("01n", R.mipmap.icon_01n);
        weatherIcons.put("02d", R.mipmap.icon_02d);
        weatherIcons.put("02n", R.mipmap.icon_02n);
        weatherIcons.put("03d", R.mipmap.icon_03d);
        weatherIcons.put("03n", R.mipmap.icon_03n);
        weatherIcons.put("04d", R.mipmap.icon_04d);
        weatherIcons.put("04n", R.mipmap.icon_04n);
        weatherIcons.put("09d", R.mipmap.icon_09d);
        weatherIcons.put("09n", R.mipmap.icon_09n);
        weatherIcons.put("10d", R.mipmap.icon_10d);
        weatherIcons.put("10n", R.mipmap.icon_10n);
        weatherIcons.put("11d", R.mipmap.icon_11d);
        weatherIcons.put("11n", R.mipmap.icon_11n);
        weatherIcons.put("13d", R.mipmap.icon_13d);
        weatherIcons.put("13n", R.mipmap.icon_13n);
        weatherIcons.put("50d", R.mipmap.icon_50d);
        weatherIcons.put("50n", R.mipmap.icon_50n);
    }

    public static MainApplication getInstance() {
        if (null == myApp) {
            myApp = new MainApplication();
            myApp.registerActivityLifecycleCallbacks(myApp);
            myApp.initLanguages();
            myApp.initWeatherDesc();
        }
        return myApp;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getAbbreviation(int position) {
        return abbreviations.get(position);
    }

    public int getWeatherIcon(String key) {
        Log.d(TAG, "getWeatherIcon: weatherIcons is null?"+(weatherIcons==null?true:weatherIcons.size()));
        return weatherIcons.get(key);
    }


    /**
     * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    /**
     * ActivityLifecycleCallback methods.
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    /**
     * Shows an app open ad.
     *
     * @param activity                 the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    public void showAdIfAvailable(
            @NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    /**
     * Inner class that loads and shows app open ads.
     */
    private class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        /**
         * Keep track of the time an app open ad is loaded to ensure you don't show an expired ad.
         */
        private long loadTime = 0;

        /**
         * Constructor.
         */
        public AppOpenAdManager() {
        }

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        private void loadAd(Context context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context,
                    AD_UNIT_ID,
                    request,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        /**
                         * Called when an app open ad has loaded.
                         *
                         * @param ad the loaded app open ad.
                         */
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                            Log.d(LOG_TAG, "onAdLoaded.");
                            Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        /**
                         * Called when an app open ad has failed to load.
                         *
                         * @param loadAdError the error.
                         */
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                            Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                            Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        /**
         * Check if ad was loaded more than n hours ago.
         */
        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        /**
         * Check if ad exists and can be shown.
         */
        private boolean isAdAvailable() {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        private void showAdIfAvailable(@NonNull final Activity activity) {
            showAdIfAvailable(
                    activity,
                    new OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                            // Empty because the user will go back to the activity that shows the ad.
                        }
                    });
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity                 the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        private void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        /** Called when full screen content is dismissed. */
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");
                            Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        /** Called when fullscreen content failed to show. */
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                            Toast.makeText(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                                    .show();

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(activity);
                        }

                        /** Called when fullscreen content is shown. */
                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                            Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
                        }
                    });

            isShowingAd = true;
            appOpenAd.show(activity);
        }
    }
}
package com.artincodes.miniera;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.artincodes.miniera.fragments.HomeFragment;
import com.artincodes.miniera.fragments.LauncherFragment;
import com.artincodes.miniera.fragments.MiniAppdrawerFragment;
import com.artincodes.miniera.fragments.ModesFragment;
import com.artincodes.miniera.fragments.WidgetFragment;
import com.artincodes.miniera.utils.BlurBuilder.BlurBuilder;
import com.artincodes.miniera.utils.HomeViewPagerAdapter;
import com.artincodes.miniera.utils.MiniDrawerDB.MiniDrawerAppsDBHelper;
import com.artincodes.miniera.utils.launcher.AppsDBHelper;
import com.artincodes.miniera.utils.weather.WeatherDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.melnykov.fab.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    HomeViewPagerAdapter pageAdapter;


    public static PackageManager packageManager;

    private ImageView wallpaperImageView;
    Drawable wallpaperDrawable;
    private ImageView blurredImageView;
    FloatingActionButton minieraFAB;

    public static MiniDrawerAppsDBHelper miniDrawerAppsDBHelper;

    ViewPager pager;
    AppsDBHelper AppsDB;


    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);


        RelativeLayout mainActivityLayout = (RelativeLayout) findViewById(R.id.main_layout);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        int marginTop = 0;
        int marginBottom = 0;


        if (Build.VERSION.SDK_INT >= 19) {
            marginTop = getStatusBarHeight();
//            layoutParams.setMargins(0,getStatusBarHeight(), 0, getNavigationBarHeight());
        }
        if (!hasBackKey) {
            marginBottom = getNavigationBarHeight();
        }

        layoutParams.setMargins(0, marginTop, 0, marginBottom);

        mainActivityLayout.setLayoutParams(layoutParams);

        wallpaperImageView = (ImageView) findViewById(R.id.wallpaper_image);
        blurredImageView = (ImageView) findViewById(R.id.blurrered_image);

        new SetWallPaperTask().execute();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
//        setActionBar();

        blurredImageView.setAlpha(0f);

        minieraFAB = (FloatingActionButton) findViewById(R.id.miniera_FAB);
        miniDrawerAppsDBHelper = new MiniDrawerAppsDBHelper(getApplicationContext());
        AppsDB = new AppsDBHelper(getApplication());

        List<Fragment> fragments = getFragments();

        pageAdapter = new HomeViewPagerAdapter(getSupportFragmentManager(), fragments);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 1 && positionOffset > 0) {
                    blurredImageView.setAlpha(positionOffset);

                } else if (positionOffset > 0) {
                    blurredImageView.setAlpha(1 - positionOffset);

                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    minieraFAB.show();
                    blurredImageView.setAlpha(0f);

                } else {
                    minieraFAB.hide();
                    blurredImageView.setAlpha(1f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                getSupportFragmentManager().popBackStack();
            }
        });

        pager.setAdapter(pageAdapter);

        pager.setCurrentItem(1);


        wallpaperImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
                minieraFAB.show();
            }
        });


        minieraFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_mini_appdrawer, new MiniAppdrawerFragment())
                        .addToBackStack("MINI DRAWER")
                        .commit();
                minieraFAB.hide();

            }
        });

        minieraFAB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new BoostDevice().execute();
                return false;
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        registerReceiver(new PacReceiver(), intentFilter);

        new LoadApplicationTask().execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        new SetWallPaperTask().execute();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

//    ********************************** WEATHER starts***************************************************

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnected(Bundle bundle) {

//        if (mRequestingLocationUpdates) {
        startLocationUpdates();
//        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
//            Toast.makeText(getApplicationContext(),mLastLocation.getLatitude()+", "+mLastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

//        mLastLocation =location;

//        Toast.makeText(getApplicationContext(),mLastLocation.getLatitude()+", "+mLastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        setWeather(location);

    }


    public void setWeather(Location location) {

        final WeatherDBHelper weatherDBHelper = new WeatherDBHelper(getApplicationContext());
        double latitude;
        double longitude;
        String URL = "";
        String API_KEY = "7263875678946287ab7659919ad6f";
        AsyncHttpClient client = new AsyncHttpClient();

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        URL = "http://api2.worldweatheronline.com/free/v2/weather.ashx?" +
                "q=" + latitude + "," + longitude +
                "&format=json&" +
                "num_of_days=1&" +
                "includelocation=yes&" +
                "key=" + API_KEY;
        client.get(URL, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                //temperatureDes.setText("Loading Weather...");
//                circularProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
//                circularProgressBar.setVisibility(View.INVISIBLE);
                String res = new String(response);
                JSONObject myJson;

                try {
                    myJson = new JSONObject(res);
                    JSONObject dataObject = myJson.getJSONObject("data");
                    JSONArray currentCondition = dataObject.getJSONArray("current_condition");
                    //String test = currentCondition.toString();
                    JSONObject weather = currentCondition.getJSONObject(0);
                    String weatherC = weather.getString("temp_C");
                    JSONArray weatherDescArray = weather.getJSONArray("weatherDesc");
                    JSONObject weatherDesc = weatherDescArray.getJSONObject(0);
                    String weatherDes = weatherDesc.getString("value");
                    //String weatherDesc = weather.getString("weatherDesc");
//                    temperatureText.setText(weatherC + (char) 0x00B0);
//                    temperatureDes.setText(weatherDes);

                    JSONArray nearestArea = dataObject.getJSONArray("nearest_area");
                    JSONObject areaObj = nearestArea.getJSONObject(0);
                    JSONArray areaArray = areaObj.getJSONArray("areaName");
                    JSONObject areaOb = areaArray.getJSONObject(0);
                    String areaName = areaOb.getString("value");
//                    locationText.setText(areaName);

                    weatherDBHelper.insertWeather(Integer.parseInt(weatherC), weatherDes, areaName);
                    //Toast.makeText(getActivity(),areaName, Toast.LENGTH_SHORT).show();
                    //JSONObject route = routesArray.getJSONObject(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();
                //temperatureText.setText("Kitti");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                locationText.setText("Failed");
//                circularProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
//                locationText.setText("Retrying");
            }

            @Override
            public void onFinish() {

            }
        });
    }


    //    ********************************** WEATHER ends ***************************************************


    public class PacReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.i("PAC RECIEVER", "PACKAGE CHANGED");

            new LoadApplicationTask().execute();
        }

    }


    public class LoadApplicationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            packageManager = getPackageManager();
            AppsDB.refreshDB();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.i("DB", AppsDB.getApps().getCount() + "");
                HomeFragment.updateDock();

            super.onPostExecute(result);
        }

    }

    @Override
    public void onBackPressed() {

        getSupportFragmentManager().popBackStack();
        pager.setCurrentItem(1, true);
        minieraFAB.show();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            getSupportFragmentManager().popBackStack();
            pager.setCurrentItem(1, true);
            minieraFAB.show();

        }
    }

//    @Override
//    protected void onUserLeaveHint(){
//        Toast.makeText(getApplicationContext(),"HOME BUTTON",Toast.LENGTH_LONG).show();
//        super.onUserLeaveHint();
//    }


    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();

//        fList.add(new WidgetFragment().newInstance());
//        fList.add(new WidgetFragment());
        fList.add(new ModesFragment());
        fList.add(new HomeFragment());
        fList.add(new LauncherFragment());

        return fList;

    }

    private class SetWallPaperTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            wallpaperDrawable = wallpaperManager.getDrawable();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            wallpaperImageView.setImageDrawable(wallpaperDrawable);
            new BlurredImageTask().execute();

        }
    }


    private class BlurredImageTask extends AsyncTask<Void, Void, Void> {

        Bitmap bitmap;

        @Override
        protected Void doInBackground(Void... params) {

            bitmap = ((BitmapDrawable) wallpaperImageView.getDrawable()).getBitmap();
            bitmap = scaleDown(bitmap, 300f, true);
            bitmap = BlurBuilder.fastblur(bitmap, 20);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            blurredImageView.setImageBitmap(bitmap);
            blurredImageView.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);
        }

        protected Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
            float ratio = Math.min(
                    (float) maxImageSize / realImage.getWidth(),
                    (float) maxImageSize / realImage.getHeight());
            int width = Math.round((float) ratio * realImage.getWidth());
            int height = Math.round((float) ratio * realImage.getHeight());

            Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                    height, filter);
            return newBitmap;
        }
    }


    public class BoostDevice extends AsyncTask<Void, Void, Void> {

        ActivityManager.MemoryInfo memoryInfo;
        ActivityManager activityManager;
        Vibrator mVibrator;


        @Override
        protected Void doInBackground(Void... params) {
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(50);
            memoryInfo = new ActivityManager.MemoryInfo();
            activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(memoryInfo);
            Log.i("AVAILABLE MEMORY", (memoryInfo.availMem) + " : TOTAL" + getTotalRAM());

            for (ActivityManager.RunningAppProcessInfo pid : activityManager.getRunningAppProcesses()) {
                activityManager.killBackgroundProcesses(pid.processName);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            Toast.makeText(getApplicationContext(), "Boosted", Toast.LENGTH_SHORT).show();
            Log.i("BOOSTED MEMORY", memoryInfo.availMem + " : TOTAL" + getTotalRAM());


        }

        public String getTotalRAM() {
            RandomAccessFile reader = null;
            String load = null;
            try {
                reader = new RandomAccessFile("/proc/meminfo", "r");
                load = reader.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                // Streams.close(reader);
            }
            return load;
        }


    }


    public int getNavigationBarHeight() {
        Resources resources = getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
//            int dimensionPixelSize = resources.getDimensionPixelSize(resourceId);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }




}

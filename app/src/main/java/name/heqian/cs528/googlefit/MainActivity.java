package name.heqian.cs528.googlefit;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, LocationListener {

    @SuppressLint("StaticFieldLeak")
    private static TextView activityText;

    @SuppressLint("StaticFieldLeak")
    private static ImageView activityImage;

    public GoogleApiClient mApiClient;
  // public GoogleApiClient mGoogleApiClient;

    public GoogleMap mGoogleMap;
    public SupportMapFragment mapFrag;
    public LocationRequest mLocationRequest;
    public Location mLastLocation;
    public Marker mCurrLocationMarker;

    private MediaPlayer mediaPlayer;

    private ActInfo currAct;

    private static ActLab actLab;

    public ActivityReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ActivityReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ActivityReceiver();
        registerReceiver(receiver, filter);


        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


        ActInfo newAct0 = new ActInfo("Unknown Activity");
        currAct = newAct0;

        actLab = ActLab.get(this);
        actLab.addAct(newAct0);

        activityText = (TextView) findViewById(R.id.activityTextView);
        activityImage = (ImageView) findViewById(R.id.activityImage);

        activityText.setText(currAct.getAct());
        activityImage.setImageResource(R.mipmap.still);

        mediaPlayer = MediaPlayer.create(this, R.raw.beat_02);
        mediaPlayer.setLooping(true);

        //Initialize Google Play Services and check permissions
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mApiClient = new GoogleApiClient.Builder(this)
                        .addApi(ActivityRecognition.API)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                mApiClient.connect();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
    }

    public static TextView getText() {
        return activityText;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);
        }

        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 1000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    private void handleCurrentActivity(String newActivity) {
        if ((currAct.getAct()).equals(newActivity)) {
            Log.e("MainActivity", "same activity detected: " + newActivity);
            // Do nothing if we detected the same activity we were just doing.
            return;
        }
        // Set text for corresponding activity:
        String text = "You are " + newActivity;
        activityText.setText(text);
        String timeSinceLastActivity; //instatiate in case first activity

        // Set image for the corresponding activity:
        if (newActivity.equals(ActivitiesEnum.STILL.toString())) {
            activityImage.setImageResource(R.mipmap.still);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            ActInfo newAct = new ActInfo(newActivity);
            currAct = newAct;
            actLab.addAct(newAct);

        } else if (newActivity.equals(ActivitiesEnum.WALKING.toString())) {
            activityImage.setImageResource(R.mipmap.walking);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            ActInfo newAct = new ActInfo(newActivity);
            currAct = newAct;
            actLab.addAct(newAct);

        } else if (newActivity.equals(ActivitiesEnum.RUNNING.toString())) {
            activityImage.setImageResource(R.mipmap.running);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            ActInfo newAct = new ActInfo(newActivity);
            currAct = newAct;
            actLab.addAct(newAct);

        } else if (newActivity.equals(ActivitiesEnum.IN_VEHICLE.toString())) {
            activityImage.setImageResource(R.mipmap.in_vehicle);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            ActInfo newAct = new ActInfo(newActivity);
            currAct = newAct;
            actLab.addAct(newAct);

        } else {
            activityText.setText(R.string.noActivity);
            activityImage.setImageResource(R.mipmap.ic_launcher);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            ActInfo newAct = new ActInfo(newActivity);
            currAct = newAct;
            actLab.addAct(newAct);
        }
        timeSinceLastActivity = getTimeSinceLastActivity(currAct);
        toastAnnouncement("you were " + currAct.getAct() + " for " + timeSinceLastActivity);
    }

    public String getTimeSinceLastActivity(ActInfo curr) {
        Date currDate = new Date();
        long diff = currDate.getTime() - curr.getStartTime().getTime();
        long sec = 1000;
        long min = sec * 60;
        long hour = min * 60;
        long day = hour * 24;
        long elapsedDays = diff / day;
        diff = diff % day;
        long elapsedHours = diff / hour;
        diff = diff % hour;
        long elapsedMinutes = diff / min;
        diff = diff % min;
        long elapsedSeconds = diff / sec;

        return elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " +
                elapsedSeconds + " seconds";
    }

    private void toastAnnouncement(String announcement) {
        Context context = getApplicationContext();
        if (context != null) {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, announcement, duration);
            toast.show();
        }
    }


    /// Recieves broadcasts from ActivityRecognizedService
    public class ActivityReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "name.heqian.cs528.googlefit.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseString = intent.getStringExtra(ActivityRecognizedService.RESPONSE_STRING);
            String reponseMessage = intent.getStringExtra(ActivityRecognizedService.RESPONSE_MESSAGE);
            handleCurrentActivity(responseString);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates since only current location is needed
        if (mApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                //request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mApiClient == null) {
                        } else {
                        mGoogleMap.setMyLocationEnabled(true); }
                    }

                } else {
                    // permission denied
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


//    @Override
//    public void onPause() {
//        super.onPause();
//
//        //stop location updates when Activity is no longer active
//        if (mApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
//        }
//    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }
}

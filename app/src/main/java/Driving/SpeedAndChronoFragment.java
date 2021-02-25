package Driving;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.webcamapplication.R;

import java.util.Formatter;
import java.util.Locale;


public class SpeedAndChronoFragment extends Fragment implements LocationListener {

    private static String TAG = "SpeedAndChronoFragment";
    //time chronometer
    private Chronometer chronometer;
    //speed TextView
    private TextView speedTextView;

    LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.speed_chronometer_fragment, container, false);
        chronometer = (Chronometer) v.findViewById(R.id.chronometer);
        speedTextView = (TextView) v.findViewById(R.id.tv_speed);
        initLocationManager();
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        initLocationManager();
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public TextView getSpeedTextView() {
        return speedTextView;
    }

    public void startChronoFromZero() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            CLocation myLocation = new CLocation(location);
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void initLocationManager() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    public void updateSpeed(CLocation location) {
        float nCurrentSpeed = 0;
        if(location != null) {
            nCurrentSpeed = location.getSpeed();
        }
        Log.d(TAG, "updateSpeed: " + nCurrentSpeed);
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");
        speedTextView.setText(strCurrentSpeed + " " + " km/h");
    }

}
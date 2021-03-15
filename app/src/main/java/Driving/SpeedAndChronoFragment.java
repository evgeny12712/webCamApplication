 package Driving;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
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


 public class SpeedAndChronoFragment extends Fragment implements LocationListener, AutoStopDialog.AutoStopDialogListener {

    private static final String TAG = "SpeedAndChronoFragment";
    private static long AUTO_STOP_TIMER_1 = 300000;
    private static final long millisecondsForAutoStopDialog = 15000;
    //time chronometer
    private Chronometer chronometer;
    //speed TextView
    private TextView speedTextView;
    //countdown timer for auto-stop
    private CountDownTimer countDownTimer;
    //check if we already started timer for auto stop
    private boolean isAlreadyStarted;
    private boolean isAutoStopDialogOn;

    float nCurrentSpeed;

    LocationManager locationManager;

    AutoStopDialog autoStopDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlreadyStarted = false;
        isAutoStopDialogOn = false;
        nCurrentSpeed = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.speed_chronometer_fragment, container, false);
        chronometer = (Chronometer) v.findViewById(R.id.chronometer);
        speedTextView = (TextView) v.findViewById(R.id.tv_speed);
        initLocationManager();
        updateSpeed(null);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        initLocationManager();
    }

     @Override
     public void onDestroy() {
         super.onDestroy();
         if(locationManager != null) {
             locationManager.removeUpdates(this);
             locationManager = null;
         }
         if(countDownTimer != null) {
             Log.d(TAG, "onDestroy: countDownTimer destroyed");
             countDownTimer.cancel();
             countDownTimer = null;
         }
         Log.d(TAG, "DESTROYED!");
     }

     public Chronometer getChronometer() {
        return chronometer;
    }

    public TextView getSpeedTextView() {
        return speedTextView;
    }

    public void setIsAutoStopDialogOn(boolean isAutoStopDialogOn) {
        this.isAutoStopDialogOn = isAutoStopDialogOn;
    }




    public boolean getIsAutoStopDialogOn() {
        return isAutoStopDialogOn;
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
        /*we got location information so initialize nCurrentSpeed with the current speed and
        set it visible.
         */
        if(location != null) {
            nCurrentSpeed = location.getSpeed();
            speedTextView.setVisibility(View.VISIBLE);
        } else {
            speedTextView.setVisibility(View.INVISIBLE);
        }
        /* init strCurrentSpeed with the speed with the format we need and an int variable and setting
        the the text-view.
         */
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");
        speedTextView.setText(strCurrentSpeed + " " + " km/h");
        String beforeFirstDot = strCurrentSpeed.split("\\.")[0];
        int intCurrentSpeed = Integer.parseInt(beforeFirstDot);
        /*check if out speed is slow and if we did'nt already stated the timer before (to not reset the timer)
        and start the timer for AUTO STOP.
         */
        if(intCurrentSpeed <= 200 && isAlreadyStarted == false) {
            if(!getActivity().getIntent().hasExtra("fromAutoStopIntent")) {
                startTimer();
                isAlreadyStarted = true;
            }
            // if current speed is'nt "slow" and timer already started so close the dialog, stop timer.
        } else if(intCurrentSpeed >= 200 && isAlreadyStarted == true) {
            if(countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            isAlreadyStarted = false;
            Log.d(TAG, "updateSpeed: moreTHEN200");
            // check if dialog is open to dismiss it.
            if(isAutoStopDialogOn) {
                Log.d(TAG, "updateSpeed: insideisAutoStop");
                autoStopDialog.stopAutoStopDialogCounter();
                autoStopDialog.dismiss();
                autoStopDialog = null;
                isAutoStopDialogOn = false;
            }
        }
    }

    //Start the timer to open auto stop dialog.
    protected void startTimer() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Update timer every tick
                AUTO_STOP_TIMER_1 = millisUntilFinished;
                Log.d(TAG, "onTick: " + AUTO_STOP_TIMER_1);
            }

            // When timer finish we need to open our dialog or notification.
            @Override
            public void onFinish() {
                if(!DrivingActivity.getIsOnBackground()) {
                    openAutoStopDialog(millisecondsForAutoStopDialog);
                } else {
                    ((DrivingActivity) getActivity()).startAutoStopNotification();
                }
            }
        }.start();
    }

    protected void stopTimer() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void updateAlreadyStarted(boolean isAlreadyStarted) {
        this.isAlreadyStarted = isAlreadyStarted;
    }

    protected void openAutoStopDialog(long millisecondsForAutoStopDialog) {
        autoStopDialog = new AutoStopDialog(millisecondsForAutoStopDialog);
        autoStopDialog.setTargetFragment(SpeedAndChronoFragment.this, 1);
        autoStopDialog.setCancelable(false);
        isAutoStopDialogOn = true;
        autoStopDialog.show(getActivity().getSupportFragmentManager(), "auto stop dialog");
    }


 }
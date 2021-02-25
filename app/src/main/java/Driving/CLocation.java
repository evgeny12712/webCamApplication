package Driving;

import android.location.Location;

public class CLocation extends Location {

    public CLocation(Location location) {
        super(location);
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);
        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();

        return nAltitude;
    }

    //getting the speed
    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;
        return nSpeed;
    }

    //getting the deviation in meters
    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();
        return nAccuracy;
    }
}
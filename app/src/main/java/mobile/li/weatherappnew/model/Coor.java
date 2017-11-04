package mobile.li.weatherappnew.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Li on 2017/11/3.
 */

public class Coor {
    private Double lat;
    private Double lon;
    private boolean currentLocation;

    public Coor(Double input_lat, Double input_lon, boolean input_isCurrent){
        lat = input_lat;
        lon = input_lon;
        currentLocation = input_isCurrent;
    }

    public Double getLatitude(){
        return lat;
    }

    public Double getLongitude(){
        return lon;
    }

    public boolean isCurrentLocation(){
        return currentLocation;
    }

    public boolean equals(Coor anotherCoor){
        NumberFormat formatter = new DecimalFormat("#0.0");
        String originalLat = formatter.format(lat);
        String originalLon = formatter.format(lon);
        String anotherLat = formatter.format(anotherCoor.lat);
        String anotherLon = formatter.format(anotherCoor.lon);

        if(originalLat.equals(anotherLat) && originalLon.equals(anotherLon)){
            return true;
        }else{
            return false;
        }
    }
}

package mobile.li.weatherappnew;

import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static mobile.li.weatherappnew.Function.castDateCurr;

/**
 * Created by Li on 2017/11/2.
 */

public class CovertToCoordinateService {
    private String cityName;
    private String countryName;
    private Double latitude;
    private Double longitude;
    private String currentTime;
    private String currentTemp;

    CovertToCoordinateService(String InputCityName, String InputCountryName){
        cityName = InputCityName;
        countryName = InputCountryName;
    }

    public void CovertToCoordinateExecuate(){
        ConvertToCoordinate.placeIdTask asyncTask = new ConvertToCoordinate.placeIdTask(new ConvertToCoordinate.AsyncResponse() {
            public void processFinish(String str1, String str2){}
        });

        if(cityName != null && countryName != null){
            try{
                JSONObject object = asyncTask.execute(cityName, countryName).get();

                JSONObject coor = object.getJSONObject("coord");
                //currentTime = new SimpleDateFormat("hh:mm a").format(new Date(object.getLong("dt") * 1000L));

                JSONObject main = object.getJSONObject("main");
                String getTemp = main.getString("temp");
                currentTemp = String.valueOf(Double.valueOf(getTemp).intValue()) + "°C";

                String lat = coor.getString("lat");
                String lon = coor.getString("lon");
                latitude = Double.valueOf(lat);
                longitude = Double.valueOf(lon);

                SimpleDateFormat dateFormat =new SimpleDateFormat("hh:mm a");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                currentTime = dateFormat.format(castDateCurr(new Date(System.currentTimeMillis()), longitude.intValue()));

                Log.v("Information", "[CovertToCoordinateService] [Convert successful]:"
                        + " |City: " + cityName
                        + " |Country: " + countryName
                        + " |Lat: " + lat
                        + " |Lon: " + lon
                        + " |Local time: " + currentTime
                        + " |Temp: " + currentTemp
                );

            }catch(Exception e){

            }
        }else{
            Log.e("ERROR: ", "[CovertToCoordinateService]");
        }

    }

    public Double getLatitude(){
        if(latitude != null){
            return latitude;
        }else{
            Log.e("ERROR: ", "[CovertToCoordinateService]: latitudePOST");
        }
        return null;
    }

    public Double getLongitude(){
        if(longitude != null){
            return longitude;
        }else{
            Log.e("ERROR: ", "[CovertToCoordinateService]: longitudePOST");
        }
        return null;
    }

    public String getCurrentTime(){
        if(currentTime != null){
            return currentTime;
        }else{
            Log.e("ERROR: ", "[CovertToCoordinateService]: CurrentTimePOST");
        }
        return null;
    }

    public String getCurrentTemp(){
        if(currentTemp != null){
            return currentTemp;
        }else{
            Log.e("ERROR: ", "[CovertToCoordinateService]: CurrentTempPOST");
        }
        return null;
    }
}

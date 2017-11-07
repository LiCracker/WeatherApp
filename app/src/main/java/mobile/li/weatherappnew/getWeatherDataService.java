package mobile.li.weatherappnew;

import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static mobile.li.weatherappnew.Function.castDateCurr;

/**
 * Created by Li on 2017/11/3.
 */

public class getWeatherDataService {
    private String latitude;
    private String longitude;
    private String cityName;
    private String countryName;
    private String currentTime;
    private String currentTemp;

    getWeatherDataService(String lat, String lon){
        latitude = lat;
        longitude = lon;
        if(lat != null && lon != null){
            getWeatherDataExecute();
        }
    }

    public void getWeatherDataExecute(){
        getWeatherData.placeIdTask asyncTask = new getWeatherData.placeIdTask(new getWeatherData.AsyncResponse(){
            public void processFinish(String str1, String str2){}
        });

        if(latitude != null && longitude != null){
            try{
                JSONObject object = asyncTask.execute(latitude, longitude).get();

                cityName = object.getString("name");

                JSONObject sys = object.getJSONObject("sys");
                countryName = sys.getString("country");

                JSONObject main = object.getJSONObject("main");
                String getTemp = main.getString("temp");
                currentTemp = String.valueOf(Double.valueOf(getTemp).intValue()) + "°C";

                SimpleDateFormat dateFormat =new SimpleDateFormat("hh:mm a");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                currentTime = dateFormat.format(castDateCurr(new Date(System.currentTimeMillis()), Double.valueOf(longitude).intValue()));

                Log.v("Information", "[getWeatherDataService] [Convert successful]:"
                        + " |Lat: " + latitude
                        + " |Lon: " + longitude
                        + " |City: " + cityName
                        + " |Country: " + countryName
                        + " |Local time: " + currentTime
                        + " |Temp: " + currentTemp
                );

            }catch(Exception e){

            }
        }else{
            Log.e("ERROR: ", "[getWeatherDataService] Execute");
        }

    }

    public String getCityName(){
        if(cityName != null){
            return cityName;
        }else{
            Log.e("ERROR: ", "[getWeatherDataService]: CityNamePOST");
        }
        return cityName;
    }

    public String getCountryName(){
        if(countryName != null){
            return countryName;
        }else{
            Log.e("ERROR: ", "[getWeatherDataService]: CountryNamePOST");
        }
        return countryName;
    }

    public String getCityCountryName(){
        if(cityName != null && countryName != null){
            return cityName + ", " + countryName;
        }else if(cityName != null){
            return cityName;
        }else if(countryName != null){
            return countryName;
        }else{
            Log.e("ERROR: ", "[getWeatherDataService]: CityCountryNamePOST");
        }
        return "A place on EARTH";
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

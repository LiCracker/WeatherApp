package mobile.li.weatherappnew;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Li on 2017/11/2.
 */

public class CovertToCoordinateService {
    private String cityName;
    private String countryName;
    private Double latitude;
    private Double longitude;
    private boolean success = false;

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
                String lat = coor.getString("lat");
                String lon = coor.getString("lon");
                latitude = Double.valueOf(lat);
                longitude = Double.valueOf(lon);
                Log.v("Information", "[CovertToCoordinateService] [Convert successful]:"
                        + " |City: " + cityName
                        + " |Country: " + countryName
                        + " |lat: " + lat
                        + " |lon: " + lon);

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
}

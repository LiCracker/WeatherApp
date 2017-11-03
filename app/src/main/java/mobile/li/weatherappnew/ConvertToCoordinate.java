package mobile.li.weatherappnew;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Li on 2017/11/2.
 */

public class ConvertToCoordinate{
    public interface AsyncResponse {
        void processFinish(String output1, String output2);
    }

    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "3a3b335b2f571f559d800d785915a563";

    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            JSONObject json = null;
            HttpURLConnection conn = null;
            try {
                String city = params[0];
                String country = params[1];

                URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, city, country));

                conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

                InputStream in = new BufferedInputStream(conn.getInputStream());
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                }

                json = new JSONObject(result);
                in.close();

                return json;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//            if (result != null) {
//                Log.v("Information", result);
//                delegate.processFinish(result, result);
//            }

//            if(result != null){
//                try{
//                    JSONObject coor = result.getJSONObject("coord");
//                    String lat = coor.getString("lat");
//                    String lon = coor.getString("lon");
//
//                    Log.v("Information", "[ConvertToCoor] Convert successful: " + "lat: " + lat + "lon: " + lon);
//                    delegate.processFinish(lat, lon);
//
//                }catch (Exception e){
//                    Log.d("Error", "[ConvertToCoor] Cannot process JSON results", e);
//                }
//            }
        }
    }

}

//public static String ConvertToCoor(String cityName, String countryCode){
//        try {
//            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, cityName, countryCode));
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            StringBuffer json = new StringBuffer(1024);
//            String tmp = "";
//            while ((tmp = reader.readLine()) != null)
//                json.append(tmp).append("\n");
//            reader.close();
//
//            JSONObject data = new JSONObject(json.toString());
//
//            // This value will be 404 if the request was not successful
//            if (data.getInt("cod") != 200) {
//                return null;
//            }
//
//            getData = data;
//        }catch(Exception e){
//            Log.d("Error", "[ConvertToCoor] Cannot get JSON results", e);
//        }
//
//        if(getData != null){
//            try{
//                JSONObject coor = getData.getJSONObject("coord");
//                String lat = coor.getString("lat");
//                String lon = coor.getString("lon");
//
//                Log.v("Information", "[ConvertToCoor] Convert successful: " + "lat: " + lat + "lon: " + lon);
//                return lat + "#" + lon;
//
//            }catch (Exception e){
//                Log.d("Error", "[ConvertToCoor] Cannot process JSON results", e);
//            }
//        }
//
//        Log.v("Error", "[ConvertToCoor] Convert Failed. Return null back");
//        return null;
//    }
//
//}

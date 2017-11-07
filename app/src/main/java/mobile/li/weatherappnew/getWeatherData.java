package mobile.li.weatherappnew;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Li on 2017/11/3.
 */

public class getWeatherData {

    public interface AsyncResponse {
        void processFinish(String output1, String output2);
    }

    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

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
                String lat = params[0];
                String lon = params[1];

                URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));

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
                if (conn != null){
                    conn.disconnect();
                }

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
        }
    }
}

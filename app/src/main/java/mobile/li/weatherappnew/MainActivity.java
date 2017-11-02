package mobile.li.weatherappnew;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import az.openweatherapi.OWService;
import az.openweatherapi.listener.OWRequestListener;
import az.openweatherapi.model.OWResponse;
import az.openweatherapi.model.gson.common.Coord;
import az.openweatherapi.model.gson.common.Weather;
import az.openweatherapi.model.gson.current_day.CurrentWeather;
import az.openweatherapi.model.gson.five_day.ExtendedWeather;
import az.openweatherapi.model.gson.five_day.WeatherForecastElement;
import az.openweatherapi.utils.OWSupportedUnits;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    Typeface weatherFont;
    long sunrise;
    long sunset;

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private LocationManager locationManager;
    private String provider;
    private static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        //humidity_field = (TextView)findViewById(R.id.humidity_field);
        //pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

//        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
//            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
//
//                cityField.setText(weather_city);
//                updatedField.setText(weather_updatedOn);
//                detailsField.setText(weather_description);
//                currentTemperatureField.setText(weather_temperature);
//                //humidity_field.setText("Humidity: "+weather_humidity);
//                //pressure_field.setText("Pressure: "+weather_pressure);
//                weatherIcon.setText(Html.fromHtml(weather_iconText));
//
//            }
//        });
//        asyncTask.execute("37.338981", "-121.886087"); //  asyncTask.execute("Latitude", "Longitude")

        OWService mOWService = new OWService("3a3b335b2f571f559d800d785915a563");
        mOWService.setMetricUnits(OWSupportedUnits.METRIC);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  }, 1);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }
        else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "Please enable the Internet or/and GPS from system settings", Toast.LENGTH_LONG).show();
            return;
        }

        double currLatitude = 0;
        double currLongitude = 0;

        location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            currLatitude = location.getLatitude();
            Log.v(TAG, "Current Latitude: " + String.valueOf(currLatitude));
            currLongitude = location.getLongitude();
            Log.v(TAG, "Current Longitude: " + String.valueOf(currLongitude));
        }

        locationManager.requestLocationUpdates(provider, 2000, 2, locationListener);

        Coord coordinate = new Coord();
        if(location == null){
            Toast.makeText(this, "Get User Location Error. Use Default Location", Toast.LENGTH_LONG).show();
            coordinate.setLat(37.338981);
            coordinate.setLon(-121.886087);
        }else{
            coordinate.setLat(currLatitude);
            coordinate.setLon(currLongitude);
        }

        mOWService.getCurrentDayForecast(coordinate, new OWRequestListener<CurrentWeather>() {
            @Override
            public void onResponse(OWResponse<CurrentWeather> response) {
                CurrentWeather currentWeather = response.body();

                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                updatedField.setText("Last Updated : " + currentDateTimeString);

                sunrise = (long)currentWeather.getSys().getSunrise() * 1000;
                sunset = (long)currentWeather.getSys().getSunset() * 1000;

                long responsetime = (long)currentWeather.getDt() * 1000;
                StringBuilder cityText = new StringBuilder();
                if(location == null){
                    cityText.append("[Default] ");
                }else{
                    cityText.append("➹ ");
                }
                cityText.append(currentWeather.getName() + ", " +currentWeather.getSys().getCountry());

                cityField.setText(cityText.toString());
                az.openweatherapi.model.gson.current_day.Weather currentDetail = currentWeather.getWeather().get(0);
                detailsField.setText(currentDetail.getDescription());
                currentTemperatureField.setText(String.valueOf(currentWeather.getMain().getTemp().intValue()) + "°");
                weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(currentDetail.getId(), sunrise, sunset, responsetime)));


                Log.i(TAG, "CURRENT Time: " + currentDateTimeString
                        + " Temp: " + String.valueOf(currentWeather.getMain().getTemp().intValue())
                        + " Des: " + currentDetail.getDescription()
                        + " Id: "+ String.valueOf(currentDetail.getId()));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Current Day Forecast request failed: " + t.getMessage());
            }
        });

        mOWService.getFiveDayForecast(coordinate, new OWRequestListener<ExtendedWeather>() {
            @Override
            public void onResponse(OWResponse<ExtendedWeather> response) {
                ExtendedWeather extendedWeather = response.body();

                List<WeatherForecastElement> lists = extendedWeather.getList();
                updateItems(lists);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Five Day Forecast request failed: " + t.getMessage());
            }
        });
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onLocationChanged(Location arg0) {
//            Toast.makeText(getBaseContext(), "Location changed: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
//            String longitude = "Longitude: " + location.getLongitude();
//            Log.v(TAG, longitude);
//            String latitude = "Latitude: " + location.getLatitude();
//            Log.v(TAG, latitude);
        }
    };

    private void updateItems(List<WeatherForecastElement> lists){
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        TextView now_icon = (TextView)findViewById(R.id.now_icon);
        TextView now_des = (TextView)findViewById(R.id.now_des);
        TextView now_temp = (TextView)findViewById(R.id.now_temp);
        TextView now_time = (TextView)findViewById(R.id.now_time);

        now_icon.setTypeface(weatherFont);
        WeatherForecastElement now = lists.get(0);
        Weather now_weather = now.getWeather().get(0);
        now_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now.getDt() * 1000L)));
        now_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather.getId(), sunrise, sunset, (long)now.getDt() * 1000)));
        now_des.setText(now_weather.getDescription());
        now_temp.setText(String.valueOf(now.getMain().getTemp().intValue())+ "°");

        TextView now_3_icon = (TextView)findViewById(R.id.now_3_icon);
        TextView now_3_des = (TextView)findViewById(R.id.now_3_des);
        TextView now_3_temp = (TextView)findViewById(R.id.now_3_temp);
        TextView now_3_time = (TextView)findViewById(R.id.now_3_time);

        now_3_icon.setTypeface(weatherFont);
        WeatherForecastElement now3 = lists.get(1);
        Weather now_weather3 = now3.getWeather().get(0);
        now_3_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now3.getDt() * 1000L)));
        now_3_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather3.getId(), sunrise, sunset,(long)now3.getDt() * 1000)));
        now_3_des.setText(now_weather3.getDescription());
        now_3_temp.setText(String.valueOf(now3.getMain().getTemp().intValue())+ "°");

        TextView now_6_icon = (TextView)findViewById(R.id.now_6_icon);
        TextView now_6_des = (TextView)findViewById(R.id.now_6_des);
        TextView now_6_temp = (TextView)findViewById(R.id.now_6_temp);
        TextView now_6_time = (TextView)findViewById(R.id.now_6_time);

        now_6_icon.setTypeface(weatherFont);
        WeatherForecastElement now6 = lists.get(2);
        Weather now_weather6 = now6.getWeather().get(0);
        now_6_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now6.getDt() * 1000L)));
        now_6_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather6.getId(), sunrise, sunset,(long)now6.getDt() * 1000)));
        now_6_des.setText(now_weather6.getDescription());
        now_6_temp.setText(String.valueOf(now6.getMain().getTemp().intValue())+ "°");

        TextView now_9_icon = (TextView)findViewById(R.id.now_9_icon);
        TextView now_9_des = (TextView)findViewById(R.id.now_9_des);
        TextView now_9_temp = (TextView)findViewById(R.id.now_9_temp);
        TextView now_9_time = (TextView)findViewById(R.id.now_9_time);

        now_9_icon.setTypeface(weatherFont);
        WeatherForecastElement now9 = lists.get(3);
        Weather now_weather9 = now9.getWeather().get(0);
        now_9_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now9.getDt() * 1000L)));
        now_9_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather9.getId(), sunrise, sunset,(long)now9.getDt() * 1000)));
        now_9_des.setText(now_weather9.getDescription());
        now_9_temp.setText(String.valueOf(now9.getMain().getTemp().intValue())+ "°");

        TextView now_12_icon = (TextView)findViewById(R.id.now_12_icon);
        TextView now_12_des = (TextView)findViewById(R.id.now_12_des);
        TextView now_12_temp = (TextView)findViewById(R.id.now_12_temp);
        TextView now_12_time = (TextView)findViewById(R.id.now_12_time);

        now_12_icon.setTypeface(weatherFont);
        WeatherForecastElement now12 = lists.get(4);
        Weather now_weather12 = now12.getWeather().get(0);
        now_12_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now12.getDt() * 1000L)));
        now_12_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather12.getId(), sunrise, sunset,(long)now12.getDt() * 1000)));
        now_12_des.setText(now_weather12.getDescription());
        now_12_temp.setText(String.valueOf(now12.getMain().getTemp().intValue())+ "°");

        TextView now_15_icon = (TextView)findViewById(R.id.now_15_icon);
        TextView now_15_des = (TextView)findViewById(R.id.now_15_des);
        TextView now_15_temp = (TextView)findViewById(R.id.now_15_temp);
        TextView now_15_time = (TextView)findViewById(R.id.now_15_time);

        now_15_icon.setTypeface(weatherFont);
        WeatherForecastElement now15 = lists.get(5);
        Weather now_weather15 = now15.getWeather().get(0);
        now_15_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now15.getDt() * 1000L)));
        now_15_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather15.getId(), sunrise, sunset,(long)now15.getDt() * 1000)));
        now_15_des.setText(now_weather15.getDescription());
        now_15_temp.setText(String.valueOf(now15.getMain().getTemp().intValue())+ "°");

        TextView now_18_icon = (TextView)findViewById(R.id.now_18_icon);
        TextView now_18_des = (TextView)findViewById(R.id.now_18_des);
        TextView now_18_temp = (TextView)findViewById(R.id.now_18_temp);
        TextView now_18_time = (TextView)findViewById(R.id.now_18_time);

        now_18_icon.setTypeface(weatherFont);
        WeatherForecastElement now18 = lists.get(6);
        Weather now_weather18 = now18.getWeather().get(0);
        now_18_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now18.getDt() * 1000L)));
        now_18_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather18.getId(), sunrise, sunset,(long)now18.getDt() * 1000)));
        now_18_des.setText(now_weather18.getDescription());
        now_18_temp.setText(String.valueOf(now18.getMain().getTemp().intValue())+ "°");

        TextView now_21_icon = (TextView)findViewById(R.id.now_21_icon);
        TextView now_21_des = (TextView)findViewById(R.id.now_21_des);
        TextView now_21_temp = (TextView)findViewById(R.id.now_21_temp);
        TextView now_21_time = (TextView)findViewById(R.id.now_21_time);

        now_21_icon.setTypeface(weatherFont);
        WeatherForecastElement now21 = lists.get(7);
        Weather now_weather21 = now21.getWeather().get(0);
        now_21_time.setText(new SimpleDateFormat("h:mm a").format(new Date(now21.getDt() * 1000L)));
        now_21_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather21.getId(), sunrise, sunset,(long)now21.getDt() * 1000)));
        now_21_des.setText(now_weather21.getDescription());
        now_21_temp.setText(String.valueOf(now21.getMain().getTemp().intValue())+ "°");

        //Toast.makeText(getApplicationContext(), String.valueOf(lists.size()), Toast.LENGTH_LONG).show();

        HashMap<Integer, String> DayDate = new HashMap<>();
        HashMap<Integer, Integer> DayMax = new HashMap<>();
        HashMap<Integer, Integer> DayMin = new HashMap<>();
        HashMap<Integer, String> DayDes = new HashMap<>();
        HashMap<Integer, Integer> DayId = new HashMap<>();
        Deque<Integer> DateQueue = new ArrayDeque<>();

        for(int i = 0; i < lists.size(); i++){
            WeatherForecastElement currday = lists.get(i);
            Date currDt = new Date(currday.getDt() * 1000L);
            int currdate = Integer.valueOf(new SimpleDateFormat("dd").format(currDt));
            int currtime  = Integer.valueOf(new SimpleDateFormat("HH").format(currDt));
            String currdateMd = new SimpleDateFormat("MM/dd").format(currDt);
            int currtemp = currday.getMain().getTemp().intValue();
            String currdes = currday.getWeather().get(0).getDescription();
            int currid = currday.getWeather().get(0).getId();

            Log.i(TAG, "Date: " + String.valueOf(currdate)
                    + " Time: " + String.valueOf(currtime)
                    + " Temp: " + String.valueOf(currtemp)
                    + " Des: " + currdes
                    + " Id: "+ String.valueOf(currid));

            if(DateQueue.isEmpty() || DateQueue.peekLast() != currdate){
                DateQueue.offerLast(currdate);
            }

            if(!DayDate.containsKey(currdate)){
                DayDate.put(currdate, currdateMd);
            }

            if(!DayMax.containsKey(currdate)){
                DayMax.put(currdate, currtemp);
            }else{
                if(currtemp > DayMax.get(currdate)){
                    DayMax.put(currdate, currtemp);
                }
            }

            if(!DayMin.containsKey(currdate)){
                DayMin.put(currdate, currtemp);
            }else{
                if(currtemp < DayMin.get(currdate)){
                    DayMin.put(currdate, currtemp);
                }
            }

            if(currtime >= 10 && currtime <= 14){
                DayDes.put(currdate, currdes);
                DayId.put(currdate, currid);
            }
        }

        WeatherForecastElement today = lists.get(0);
        Date todayDt = new Date(today.getDt() * 1000L);
        int today_time  = Integer.valueOf(new SimpleDateFormat("HH").format(todayDt));
        int startday = DateQueue.pollFirst();
        if(today_time > 14){
            startday = DateQueue.pollFirst();
        }

        Log.i(TAG, "Startday: " + String.valueOf(startday));

        TextView day_1_icon = (TextView)findViewById(R.id.day_1_icon);
        TextView day_1_des = (TextView)findViewById(R.id.day_1_des);
        TextView day_1_temp = (TextView)findViewById(R.id.day_1_temp);
        TextView day_1_time = (TextView)findViewById(R.id.day_1_time);

//        WeatherForecastElement day1 = lists.get(4);
//        Weather day_weather1 = day1.getWeather().get(0);
//        day_1_time.setText(new SimpleDateFormat("MM/dd").format(new Date(day1.getDt() * 1000L)));
//        day_1_icon.setText(Html.fromHtml(Function.setWeatherIcon2(day_weather1.getId())));
//        day_1_des.setText(day_weather1.getDescription());
//        day_1_temp.setText(String.valueOf(day1.getMain().getTempMax().intValue())+ "/" +
//                        String.valueOf(day1.getMain().getTempMin().intValue())+ "°C"
//                            );
        day_1_icon.setTypeface(weatherFont);
        day_1_time.setText(DayDate.get(startday));
        day_1_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(startday))));
        day_1_des.setText(DayDes.get(startday));
        day_1_temp.setText(String.valueOf(DayMax.get(startday)) + "/" + String.valueOf(DayMin.get(startday)) + "°C");

        TextView day_2_icon = (TextView)findViewById(R.id.day_2_icon);
        TextView day_2_des = (TextView)findViewById(R.id.day_2_des);
        TextView day_2_temp = (TextView)findViewById(R.id.day_2_temp);
        TextView day_2_time = (TextView)findViewById(R.id.day_2_time);

        int day2 = DateQueue.pollFirst();
        day_2_icon.setTypeface(weatherFont);
        day_2_time.setText(DayDate.get(day2));
        day_2_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(day2))));
        day_2_des.setText(DayDes.get(day2));
        day_2_temp.setText(String.valueOf(DayMax.get(day2)) + "/" + String.valueOf(DayMin.get(day2)) + "°C");

        TextView day_3_icon = (TextView)findViewById(R.id.day_3_icon);
        TextView day_3_des = (TextView)findViewById(R.id.day_3_des);
        TextView day_3_temp = (TextView)findViewById(R.id.day_3_temp);
        TextView day_3_time = (TextView)findViewById(R.id.day_3_time);

        int day3 = DateQueue.pollFirst();
        day_3_icon.setTypeface(weatherFont);
        day_3_time.setText(DayDate.get(day3));
        day_3_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(day3))));
        day_3_des.setText(DayDes.get(day3));
        day_3_temp.setText(String.valueOf(DayMax.get(day3)) + "/" + String.valueOf(DayMin.get(day3)) + "°C");

        TextView day_4_icon = (TextView)findViewById(R.id.day_4_icon);
        TextView day_4_des = (TextView)findViewById(R.id.day_4_des);
        TextView day_4_temp = (TextView)findViewById(R.id.day_4_temp);
        TextView day_4_time = (TextView)findViewById(R.id.day_4_time);

        int day4 = DateQueue.pollFirst();
        day_4_icon.setTypeface(weatherFont);
        day_4_time.setText(DayDate.get(day4));
        day_4_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(day4))));
        day_4_des.setText(DayDes.get(day4));
        day_4_temp.setText(String.valueOf(DayMax.get(day4)) + "/" + String.valueOf(DayMin.get(day4)) + "°C");

        TextView day_5_icon = (TextView)findViewById(R.id.day_5_icon);
        TextView day_5_des = (TextView)findViewById(R.id.day_5_des);
        TextView day_5_temp = (TextView)findViewById(R.id.day_5_temp);
        TextView day_5_time = (TextView)findViewById(R.id.day_5_time);

        int day5 = DateQueue.pollFirst();
        day_5_icon.setTypeface(weatherFont);
        day_5_time.setText(DayDate.get(day5));
        day_5_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(day5))));
        day_5_des.setText(DayDes.get(day5));
        day_5_temp.setText(String.valueOf(DayMax.get(day5)) + "/" + String.valueOf(DayMin.get(day5)) + "°C");

    }
}

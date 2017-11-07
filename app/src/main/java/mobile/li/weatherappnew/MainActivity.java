package mobile.li.weatherappnew;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import az.openweatherapi.OWService;
import az.openweatherapi.listener.OWRequestListener;
import az.openweatherapi.model.OWResponse;
import az.openweatherapi.model.gson.common.Coord;
import az.openweatherapi.model.gson.common.Weather;
import az.openweatherapi.model.gson.current_day.CurrentWeather;
import az.openweatherapi.model.gson.five_day.ExtendedWeather;
import az.openweatherapi.model.gson.five_day.WeatherForecastElement;
import az.openweatherapi.utils.OWSupportedUnits;
import mobile.li.weatherappnew.model.Coor;

import static mobile.li.weatherappnew.Function.CelsiusToFahrenheit;
import static mobile.li.weatherappnew.Function.CelsiusToFahrenheitDays;
import static mobile.li.weatherappnew.Function.FahrenheitToCelsius;
import static mobile.li.weatherappnew.Function.FahrenheitToCelsiusDays;
import static mobile.li.weatherappnew.Function.castDate;
import static mobile.li.weatherappnew.Function.castDateCurr;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    TextView cityField, detailsField, currentTemperatureField, weatherIcon, updatedField;
    Typeface weatherFont;
    long sunrise;
    long sunset;

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    private LocationManager locationManager;
    private String provider;
    private static boolean intentBackCurr = false;
    Coor intentLocation;
    Coor userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        (findViewById(R.id.listButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ToggleButton toggle = (ToggleButton)findViewById(R.id.toggleButton);

        intentLocation = getIntentLocation();
        userLocation = getUserLocation();

        if(intentLocation == null){
            if(userLocation == null){
                loadWeather(null);
            }else{
                loadWeather(userLocation);
            }
        }else{
            if(userLocation != null && userLocation.equals(intentLocation)){
                loadWeather(userLocation);
            }else if(userLocation == null || (userLocation != null && !userLocation.equals(intentLocation))){
                loadWeather(intentLocation);
            }else{
                loadWeather(null);
            }
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
                TextView now_temp = (TextView)findViewById(R.id.now_temp);
                TextView now_3_temp = (TextView)findViewById(R.id.now_3_temp);
                TextView now_6_temp = (TextView)findViewById(R.id.now_6_temp);
                TextView now_9_temp = (TextView)findViewById(R.id.now_9_temp);
                TextView now_12_temp = (TextView)findViewById(R.id.now_12_temp);
                TextView now_15_temp = (TextView)findViewById(R.id.now_15_temp);
                TextView now_18_temp = (TextView)findViewById(R.id.now_18_temp);
                TextView now_21_temp = (TextView)findViewById(R.id.now_21_temp);
                TextView day_1_temp = (TextView)findViewById(R.id.day_1_temp);
                TextView day_2_temp = (TextView)findViewById(R.id.day_2_temp);
                TextView day_3_temp = (TextView)findViewById(R.id.day_3_temp);
                TextView day_4_temp = (TextView)findViewById(R.id.day_4_temp);
                TextView day_5_temp = (TextView)findViewById(R.id.day_5_temp);

                if (isChecked) {
                    // The toggle is enabled - F
                    currentTemperatureField.setText(FahrenheitToCelsius(currentTemperatureField.getText().toString()));
                    now_temp.setText(FahrenheitToCelsius(now_temp.getText().toString()));
                    now_3_temp.setText(FahrenheitToCelsius(now_3_temp.getText().toString()));
                    now_6_temp.setText(FahrenheitToCelsius(now_6_temp.getText().toString()));
                    now_9_temp.setText(FahrenheitToCelsius(now_9_temp.getText().toString()));
                    now_12_temp.setText(FahrenheitToCelsius(now_12_temp.getText().toString()));
                    now_15_temp.setText(FahrenheitToCelsius(now_15_temp.getText().toString()));
                    now_18_temp.setText(FahrenheitToCelsius(now_18_temp.getText().toString()));
                    now_21_temp.setText(FahrenheitToCelsius(now_21_temp.getText().toString()));
                    day_1_temp.setText(FahrenheitToCelsiusDays(day_1_temp.getText().toString()));
                    day_2_temp.setText(FahrenheitToCelsiusDays(day_2_temp.getText().toString()));
                    day_3_temp.setText(FahrenheitToCelsiusDays(day_3_temp.getText().toString()));
                    day_4_temp.setText(FahrenheitToCelsiusDays(day_4_temp.getText().toString()));
                    day_5_temp.setText(FahrenheitToCelsiusDays(day_5_temp.getText().toString()));

                } else {
                    // The toggle is disabled - C
                    currentTemperatureField.setText(CelsiusToFahrenheit(currentTemperatureField.getText().toString()));
                    now_temp.setText(CelsiusToFahrenheit(now_temp.getText().toString()));
                    now_3_temp.setText(CelsiusToFahrenheit(now_3_temp.getText().toString()));
                    now_6_temp.setText(CelsiusToFahrenheit(now_6_temp.getText().toString()));
                    now_9_temp.setText(CelsiusToFahrenheit(now_9_temp.getText().toString()));
                    now_12_temp.setText(CelsiusToFahrenheit(now_12_temp.getText().toString()));
                    now_15_temp.setText(CelsiusToFahrenheit(now_15_temp.getText().toString()));
                    now_18_temp.setText(CelsiusToFahrenheit(now_18_temp.getText().toString()));
                    now_21_temp.setText(CelsiusToFahrenheit(now_21_temp.getText().toString()));
                    day_1_temp.setText(CelsiusToFahrenheitDays(day_1_temp.getText().toString()));
                    day_2_temp.setText(CelsiusToFahrenheitDays(day_2_temp.getText().toString()));
                    day_3_temp.setText(CelsiusToFahrenheitDays(day_3_temp.getText().toString()));
                    day_4_temp.setText(CelsiusToFahrenheitDays(day_4_temp.getText().toString()));
                    day_5_temp.setText(CelsiusToFahrenheitDays(day_5_temp.getText().toString()));
                }
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
            Coor locationChangedCoor = null;
            if(arg0 != null){
                String logChangeLat = String.format("%.2f", arg0.getLatitude());
                String logChangeLon = String.format("%.2f", arg0.getLongitude());
                locationChangedCoor = new Coor(arg0.getLatitude(), arg0.getLongitude(), true);
                Toast.makeText(getBaseContext(), "[Location Updated] Lat: " + logChangeLat + " Lon: " + logChangeLon, Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Latitude Changed: " + logChangeLat);
                Log.v(TAG, "Longitude Changed: " + logChangeLon);
            }else{
                Toast.makeText(getBaseContext(), "Please check your Internet and GPS settings.", Toast.LENGTH_LONG).show();
            }

            //intentLocation == null
            //loadWeather(locationChangedCoor)
            if(intentLocation == null){
                loadWeather(locationChangedCoor);
            } else if(intentLocation != null && locationChangedCoor != null && intentLocation.equals(locationChangedCoor)){
                //locationChangedCoor != null and intentLocation != null and intentLocation.equals(arg0)
                //loadWeather(locationChangedCoor)
                loadWeather(locationChangedCoor);
            } else if(intentLocation != null && locationChangedCoor != null && !intentLocation.equals(locationChangedCoor)){
                //locationChangedCoor != null and intentLocation != null and !intentLocation.equals(arg0)
                //loadWeather(intentLocation)
                loadWeather(intentLocation);
            } else if(intentLocation != null && locationChangedCoor == null){
                loadWeather(intentLocation);
            }

        }
    };

    private Coor getIntentLocation(){
        String intentString = getIntent().getStringExtra(CityListActivity.KEY_LAT_LON);
        if(intentString != null){
            String[] intentpartial = intentString.split(",");
            if(intentpartial.length == 3){
                if(intentpartial[0] == "1"){
                    intentBackCurr = true;
                    Log.v(TAG, "[IntentLocation] currentLocation");
                    return null;
                }else{
                    intentBackCurr = false;
                    Double intent_lat = Double.valueOf(intentpartial[1]);
                    Double intent_lon = Double.valueOf(intentpartial[2]);
                    if(intent_lat.intValue() == 0 && intent_lon.intValue() == 0){
                        intentBackCurr = true;
                        Log.v(TAG, "[IntentLocation] currentLocation");
                        return null;
                    }
                    Log.v(TAG, "[IntentLocation] Latitude: " + intentpartial[1] + " Longitude:" + intentpartial[2]);
                    return new Coor(intent_lat, intent_lon, false);
                }
            }else{
                Log.e(TAG, "[IntentLocation] null");
                return null;
            }
        }else{
            Log.e(TAG, "[IntentLocation] null");
            return null;
        }

    }

    private Coor getUserLocation(){
        Location userLocation;

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
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "Please check your Internet and GPS settings.", Toast.LENGTH_LONG).show();
            return null;
        }

        double currLatitude = 0;
        double currLongitude = 0;

        userLocation = locationManager.getLastKnownLocation(provider);
        if (userLocation != null) {
            currLatitude = userLocation.getLatitude();
            Log.v(TAG, "Current Latitude: " + String.valueOf(currLatitude));
            currLongitude = userLocation.getLongitude();
            Log.v(TAG, "Current Longitude: " + String.valueOf(currLongitude));
        }

        locationManager.requestLocationUpdates(provider, 2000, 100, locationListener); // 2000, 2

        if(userLocation != null){
            return new Coor(userLocation.getLatitude(), userLocation.getLongitude(), true);
        }else{
            return null;
        }
    }

    private void loadWeather(final Coor inputCoor){
        final Coord coordinate = new Coord();
        if(inputCoor == null){
            Toast.makeText(this, "Get User Location Error. Use Default Location", Toast.LENGTH_LONG).show();
            coordinate.setLat(37.338981);
            coordinate.setLon(-121.886087);
        }else{
            coordinate.setLat(inputCoor.getLatitude());
            coordinate.setLon(inputCoor.getLongitude());
        }

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        OWService mOWService = new OWService("3a3b335b2f571f559d800d785915a563");
        mOWService.setMetricUnits(OWSupportedUnits.METRIC);

//        CovertToCoordinateService s1 = new CovertToCoordinateService("Beijing", "CN");
//        s1.CovertToCoordinateExecuate();
//        Double testLatDouble = s1.getLatitude();
//        Double testLonDouble = s1.getLongitude();
//        String testTime = s1.getCurrentTime();
//        String testTemp = s1.getCurrentTemp();
//
//        String testlat = String.valueOf(testLatDouble);
//        String testlon = String.valueOf(testLonDouble);
//        Toast.makeText(this, "[TEST]: Lat:" + testlat
//                + " Lon: " + testlon
//                + " Local Time: " + testTime
//                + " Local Temp: " + testTemp, Toast.LENGTH_LONG).show();

//        getWeatherDataService g1 = new getWeatherDataService(String.valueOf(coordinate.getLat()), String.valueOf(coordinate.getLon()));
//        Toast.makeText(this, "[TEST]: CityName:" + g1.getCityName()
//                + " CountryName: " + g1.getCountryName()
//                + " Local Time: " + g1.getCurrentTime()
//                + " Local Temp: " + g1.getCurrentTime(), Toast.LENGTH_LONG).show();

        mOWService.getCurrentDayForecast(coordinate, new OWRequestListener<CurrentWeather>() {
            @Override
            public void onResponse(OWResponse<CurrentWeather> response) {
                CurrentWeather currentWeather = response.body();

                SimpleDateFormat dateFormat =new SimpleDateFormat("MMM d, hh:mm a");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
                //String currentDateTimeString = dateFormat.format(castDateCurr(new Date(currentWeather.getDt() * 1000L), coordinate.getLon().intValue()));
                String currentDateTimeString = dateFormat.format(castDateCurr(new Date(System.currentTimeMillis()), coordinate.getLon().intValue()));
                updatedField.setText("Updated in Local time: " + currentDateTimeString);

                sunrise = castDateCurr(new Date((long)currentWeather.getSys().getSunrise() * 1000L), coordinate.getLon().intValue()).getTime();
                sunset = castDateCurr(new Date((long)currentWeather.getSys().getSunset() * 1000L), coordinate.getLon().intValue()).getTime();

                //long responsetime = (long)currentWeather.getDt() * 1000;
                long responsetime = castDateCurr(new Date((long)currentWeather.getDt() * 1000L), coordinate.getLon().intValue()).getTime();
                StringBuilder cityText = new StringBuilder();
                if(inputCoor == null){
                    cityText.append("[Default] ");
                }else if(inputCoor.isCurrentLocation() == true){
                    cityText.append("➹ ");
                }

                cityText.append(currentWeather.getName() + ", " +currentWeather.getSys().getCountry());

                cityField.setText(cityText.toString());
                az.openweatherapi.model.gson.current_day.Weather currentDetail = currentWeather.getWeather().get(0);
                detailsField.setText(currentDetail.getDescription());
                currentTemperatureField.setText(String.valueOf(currentWeather.getMain().getTemp().intValue()) + "°");
                weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(currentDetail.getId(), sunrise, sunset, responsetime)));


                Log.i(TAG, "[mOWService]CURRENT Time: " + currentDateTimeString
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
                updateItems(lists, coordinate.getLon().intValue());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Five Day Forecast request failed: " + t.getMessage());
            }
        });
    }

    private void updateItems(List<WeatherForecastElement> lists, int lon){
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        SimpleDateFormat dateFormat =new SimpleDateFormat("h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        TextView now_icon = (TextView)findViewById(R.id.now_icon);
        TextView now_des = (TextView)findViewById(R.id.now_des);
        TextView now_temp = (TextView)findViewById(R.id.now_temp);
        TextView now_time = (TextView)findViewById(R.id.now_time);

        now_icon.setTypeface(weatherFont);
        WeatherForecastElement now = lists.get(0);
        Weather now_weather = now.getWeather().get(0);
        long now_responsetime = castDateCurr(new Date((long)now.getDt() * 1000L), lon).getTime();

        now_time.setText(dateFormat.format(castDate(new Date(now.getDt() * 1000L + lon * 240000L))));
        now_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather.getId(), sunrise, sunset, now_responsetime)));
        now_des.setText(now_weather.getDescription());
        now_temp.setText(String.valueOf(now.getMain().getTemp().intValue())+ "°");

        TextView now_3_icon = (TextView)findViewById(R.id.now_3_icon);
        TextView now_3_des = (TextView)findViewById(R.id.now_3_des);
        TextView now_3_temp = (TextView)findViewById(R.id.now_3_temp);
        TextView now_3_time = (TextView)findViewById(R.id.now_3_time);

        now_3_icon.setTypeface(weatherFont);
        WeatherForecastElement now3 = lists.get(1);
        Weather now_weather3 = now3.getWeather().get(0);
        long now3_responsetime = castDateCurr(new Date((long)now3.getDt() * 1000L), lon).getTime();

        now_3_time.setText(dateFormat.format(castDate(new Date(now3.getDt() * 1000L + lon * 240000L))));
        now_3_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather3.getId(), sunrise, sunset, now3_responsetime)));
        now_3_des.setText(now_weather3.getDescription());
        now_3_temp.setText(String.valueOf(now3.getMain().getTemp().intValue())+ "°");

        TextView now_6_icon = (TextView)findViewById(R.id.now_6_icon);
        TextView now_6_des = (TextView)findViewById(R.id.now_6_des);
        TextView now_6_temp = (TextView)findViewById(R.id.now_6_temp);
        TextView now_6_time = (TextView)findViewById(R.id.now_6_time);

        now_6_icon.setTypeface(weatherFont);
        WeatherForecastElement now6 = lists.get(2);
        Weather now_weather6 = now6.getWeather().get(0);
        long now6_responsetime = castDateCurr(new Date((long)now6.getDt() * 1000L), lon).getTime();

        now_6_time.setText(dateFormat.format(castDate(new Date(now6.getDt() * 1000L + lon * 240000L))));
        now_6_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather6.getId(), sunrise, sunset, now6_responsetime)));
        now_6_des.setText(now_weather6.getDescription());
        now_6_temp.setText(String.valueOf(now6.getMain().getTemp().intValue())+ "°");

        TextView now_9_icon = (TextView)findViewById(R.id.now_9_icon);
        TextView now_9_des = (TextView)findViewById(R.id.now_9_des);
        TextView now_9_temp = (TextView)findViewById(R.id.now_9_temp);
        TextView now_9_time = (TextView)findViewById(R.id.now_9_time);

        now_9_icon.setTypeface(weatherFont);
        WeatherForecastElement now9 = lists.get(3);
        Weather now_weather9 = now9.getWeather().get(0);
        long now9_responsetime = castDateCurr(new Date((long)now9.getDt() * 1000L), lon).getTime();

        now_9_time.setText(dateFormat.format(castDate(new Date(now9.getDt() * 1000L + lon * 240000L))));
        now_9_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather9.getId(), sunrise, sunset, now9_responsetime)));
        now_9_des.setText(now_weather9.getDescription());
        now_9_temp.setText(String.valueOf(now9.getMain().getTemp().intValue())+ "°");

        TextView now_12_icon = (TextView)findViewById(R.id.now_12_icon);
        TextView now_12_des = (TextView)findViewById(R.id.now_12_des);
        TextView now_12_temp = (TextView)findViewById(R.id.now_12_temp);
        TextView now_12_time = (TextView)findViewById(R.id.now_12_time);

        now_12_icon.setTypeface(weatherFont);
        WeatherForecastElement now12 = lists.get(4);
        Weather now_weather12 = now12.getWeather().get(0);
        long now12_responsetime = castDateCurr(new Date((long)now12.getDt() * 1000L), lon).getTime();

        now_12_time.setText(dateFormat.format(castDate(new Date(now12.getDt() * 1000L + lon * 240000L))));
        now_12_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather12.getId(), sunrise, sunset, now12_responsetime)));
        now_12_des.setText(now_weather12.getDescription());
        now_12_temp.setText(String.valueOf(now12.getMain().getTemp().intValue())+ "°");

        TextView now_15_icon = (TextView)findViewById(R.id.now_15_icon);
        TextView now_15_des = (TextView)findViewById(R.id.now_15_des);
        TextView now_15_temp = (TextView)findViewById(R.id.now_15_temp);
        TextView now_15_time = (TextView)findViewById(R.id.now_15_time);

        now_15_icon.setTypeface(weatherFont);
        WeatherForecastElement now15 = lists.get(5);
        Weather now_weather15 = now15.getWeather().get(0);
        long now15_responsetime = castDateCurr(new Date((long)now15.getDt() * 1000L), lon).getTime();

        now_15_time.setText(dateFormat.format(castDate(new Date(now15.getDt() * 1000L + lon * 240000L))));
        now_15_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather15.getId(), sunrise, sunset, now15_responsetime)));
        now_15_des.setText(now_weather15.getDescription());
        now_15_temp.setText(String.valueOf(now15.getMain().getTemp().intValue())+ "°");

        TextView now_18_icon = (TextView)findViewById(R.id.now_18_icon);
        TextView now_18_des = (TextView)findViewById(R.id.now_18_des);
        TextView now_18_temp = (TextView)findViewById(R.id.now_18_temp);
        TextView now_18_time = (TextView)findViewById(R.id.now_18_time);

        now_18_icon.setTypeface(weatherFont);
        WeatherForecastElement now18 = lists.get(6);
        Weather now_weather18 = now18.getWeather().get(0);
        long now18_responsetime = castDateCurr(new Date((long)now18.getDt() * 1000L), lon).getTime();

        now_18_time.setText(dateFormat.format(castDate(new Date(now18.getDt() * 1000L + lon * 240000L))));
        now_18_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather18.getId(), sunrise, sunset, now18_responsetime)));
        now_18_des.setText(now_weather18.getDescription());
        now_18_temp.setText(String.valueOf(now18.getMain().getTemp().intValue())+ "°");

        TextView now_21_icon = (TextView)findViewById(R.id.now_21_icon);
        TextView now_21_des = (TextView)findViewById(R.id.now_21_des);
        TextView now_21_temp = (TextView)findViewById(R.id.now_21_temp);
        TextView now_21_time = (TextView)findViewById(R.id.now_21_time);

        now_21_icon.setTypeface(weatherFont);
        WeatherForecastElement now21 = lists.get(7);
        Weather now_weather21 = now21.getWeather().get(0);
        long now21_responsetime = castDateCurr(new Date((long)now21.getDt() * 1000L), lon).getTime();

        now_21_time.setText(dateFormat.format(castDate(new Date(now21.getDt() * 1000L + lon * 240000L))));
        now_21_icon.setText(Html.fromHtml(Function.setWeatherIcon(now_weather21.getId(), sunrise, sunset, now21_responsetime)));
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

            if(currtime <= 14){ //currtime >= 10 &&
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

        if(DayDes.size() > 3){
            int day4 = DateQueue.pollFirst();
            day_4_icon.setTypeface(weatherFont);
            day_4_time.setText(DayDate.get(day4));
            day_4_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(day4))));
            day_4_des.setText(DayDes.get(day4));
            day_4_temp.setText(String.valueOf(DayMax.get(day4)) + "/" + String.valueOf(DayMin.get(day4)) + "°C");
        }else{
            day_4_icon.setTypeface(weatherFont);
            day_4_time.setText("");
            day_4_icon.setText("");
            day_4_des.setText("");
            day_4_temp.setText("");
        }

        TextView day_5_icon = (TextView)findViewById(R.id.day_5_icon);
        TextView day_5_des = (TextView)findViewById(R.id.day_5_des);
        TextView day_5_temp = (TextView)findViewById(R.id.day_5_temp);
        TextView day_5_time = (TextView)findViewById(R.id.day_5_time);

        if(DayDes.size() > 4){
            int day5 = DateQueue.pollFirst();
            day_5_icon.setTypeface(weatherFont);
            day_5_time.setText(DayDate.get(day5));
            day_5_icon.setText(Html.fromHtml(Function.setWeatherIcon2(DayId.get(day5))));
            day_5_des.setText(DayDes.get(day5));
            day_5_temp.setText(String.valueOf(DayMax.get(day5)) + "/" + String.valueOf(DayMin.get(day5)) + "°C");
        }else{
            day_5_icon.setTypeface(weatherFont);
            day_5_time.setText("");
            day_5_icon.setText("");
            day_5_des.setText("");
            day_5_temp.setText("");
        }
    }
}

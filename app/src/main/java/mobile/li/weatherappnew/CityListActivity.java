package mobile.li.weatherappnew;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobile.li.weatherappnew.model.CityInfo;
import mobile.li.weatherappnew.util.ModelUtils;


public class CityListActivity extends AppCompatActivity {

    private static final int REQ_CODE_CITY_DELETE = 101;
    private static final String MODEL_CITY_INFO = "city_info";
    public static final String KEY_LAT_LON = "city_lat_lon";
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    private List<CityInfo> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
        setupUI();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    String addr = place.getAddress().toString();
                    LatLng lat = place.getLatLng();
                    double l1 = lat.latitude;
                    double l2 = lat.longitude;
                    String sLatLon = String.valueOf(l1) + "," + String.valueOf(l2);

                    List<String> addInfo = Arrays.asList(addr.split(","));

                    CityInfo newCity = new CityInfo();
                    newCity.name = addInfo.get(0).trim();
                    newCity.latLon = sLatLon;
                    newCity.lat = l1;
                    newCity.lon = l2;

                    if(newCity != null){
                        deleteCity(newCity.lat, newCity.lon);
                    }
                    cities.add(newCity);
                    ModelUtils.save(this, MODEL_CITY_INFO, cities);
                    setupCityUI();
                    break;
                case REQ_CODE_CITY_DELETE:
                    String deleteCityLat = data.getStringExtra(CityDeleteActivity.DELETE_CITY_LAT);
                    String deleteCityLon = data.getStringExtra(CityDeleteActivity.DELETE_CITY_LON);
                    double deleteLat = Double.parseDouble(deleteCityLat);
                    double deleteLon = Double.parseDouble(deleteCityLon);
                    deleteCity(deleteLat, deleteLon);
                    ModelUtils.save(this, MODEL_CITY_INFO, cities);
                    setupCityUI();
                    break;
            }
        }
    }

    private void setupUI() {
        setContentView(R.layout.activity_city_list);

        setupCityUI();

        (findViewById(R.id.city_list_addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(CityListActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                }catch (GooglePlayServicesRepairableException e){

                }catch(GooglePlayServicesNotAvailableException e){

                }
            }
        });

    }


    private void setupCityUI(){
        LinearLayout cityLayout = findViewById(R.id.city_list);
        cityLayout.removeAllViews();
        View currentView = getLayoutInflater().inflate(R.layout.city_current, null);
        setupCurrent(currentView);
        cityLayout.addView(currentView);
        for(CityInfo c: cities){
            View cityView = getLayoutInflater().inflate(R.layout.city_item, null);
            setupCity(cityView, c);
            cityLayout.addView(cityView);
        }
    }

    private void setupCurrent(View currentView){
        ((Button) currentView.findViewById(R.id.city_current)).setText("CURRENT");

        (currentView.findViewById(R.id.city_current)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String result = "1,0.0,0.0";
                Intent intent = new Intent(CityListActivity.this, MainActivity.class);
                intent.putExtra(KEY_LAT_LON, result);
                startActivity(intent);
                finish();
            }
        });
    }


    private void setupCity(View cityView, final CityInfo c){
        CovertToCoordinateService s = new CovertToCoordinateService(c.name, c.code);
        s.CovertToCoordinateExecuate();
        String temp = s.getCurrentTemp();


        ((Button) cityView.findViewById(R.id.city_item)).setText(c.name + "   " + temp);

        ImageButton cityDeleteBtn = (ImageButton) cityView.findViewById(R.id.city_item_delete);
        cityDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityListActivity.this, CityDeleteActivity.class);
                String s = c.latLon;
                intent.putExtra(CityDeleteActivity.DELETE_CITY, s);
                startActivityForResult(intent, REQ_CODE_CITY_DELETE);
            }
        });

        (cityView.findViewById(R.id.city_item)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String result = "0" + "," + c.latLon;
                Intent intent = new Intent(CityListActivity.this, MainActivity.class);
                intent.putExtra(KEY_LAT_LON, result);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadData() {
        List<CityInfo> saveCityInfo = ModelUtils.read(this,
                MODEL_CITY_INFO,
                new TypeToken<List<CityInfo>>(){});
        cities = saveCityInfo == null ? new ArrayList<CityInfo>() : saveCityInfo;
    }

    private void deleteCity(double lat, double lon){
        for (int i = 0; i < cities.size(); ++i){
            CityInfo c = cities.get(i);
            if(c.lat == lat && c.lon == lon){
                cities.remove(i);
                break;
            }
        }
    }
}

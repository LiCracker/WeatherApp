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

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobile.li.weatherappnew.model.CityInfo;
import mobile.li.weatherappnew.util.ModelUtils;

import static mobile.li.weatherappnew.CityAdderActivity.KEY_CITY;

public class CityListActivity extends AppCompatActivity {

    private static final int REQ_CODE_CITY_ADDER = 100;
    private static final int REQ_CODE_CITY_DELETE = 101;
    private static final String MODEL_CITY_INFO = "city_info";
    public static final String KEY_LAT_LON = "city_lat_lon";


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
                case REQ_CODE_CITY_ADDER:
                    List<String> addInfo = Arrays.asList(data.getStringExtra(KEY_CITY).split(","));

                    CityInfo newCity = new CityInfo();
                    newCity.name = addInfo.get(0).trim();
                    newCity.code = addInfo.get(1).trim();

                    if(newCity != null){
                        deleteCity(newCity.name, newCity.code);
                    }
                    cities.add(newCity);
                    ModelUtils.save(this, MODEL_CITY_INFO, cities);
                    setupCityUI();
                    break;
                case REQ_CODE_CITY_DELETE:
                    String deleteCityName = data.getStringExtra(CityDeleteActivity.DELETE_CITY_NAME);
                    String deleteCityCode = data.getStringExtra(CityDeleteActivity.DELETE_CITY_CODE);
                    deleteCity(deleteCityName, deleteCityCode);
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
                Intent intent = new Intent(CityListActivity.this, CityAdderActivity.class);
                startActivityForResult(intent, REQ_CODE_CITY_ADDER);
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


        ((Button) cityView.findViewById(R.id.city_item)).setText(c.name + "," + c.code + "   " + temp + "°C");

        ImageButton cityDeleteBtn = (ImageButton) cityView.findViewById(R.id.city_item_delete);
        cityDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityListActivity.this, CityDeleteActivity.class);
                String s = c.name + "," + c.code;
                intent.putExtra(CityDeleteActivity.DELETE_CITY, s);
                startActivityForResult(intent, REQ_CODE_CITY_DELETE);
            }
        });

        (cityView.findViewById(R.id.city_item)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                CovertToCoordinateService s1 = new CovertToCoordinateService(c.name, c.code);
                s1.CovertToCoordinateExecuate();
                Double lat = s1.getLatitude();
                Double lon = s1.getLongitude();

                String result = "0" + "," + String.valueOf(lat) + "," + String.valueOf(lon);
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

    private void deleteCity(String name, String code){
        for (int i = 0; i < cities.size(); ++i){
            CityInfo c = cities.get(i);
            if(TextUtils.equals(c.name, name) && TextUtils.equals(c.code, code)){
                cities.remove(i);
                break;
            }
        }
    }
}

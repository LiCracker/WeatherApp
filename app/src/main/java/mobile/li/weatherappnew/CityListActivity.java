package mobile.li.weatherappnew;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

public class CityListActivity extends AppCompatActivity {

    private static final int REQ_CODE_CITY_ADDER = 100;
    private static final String MODEL_CITY_INFO = "city_info";

    private List<CityInfo> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
        setupUI();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CODE_CITY_ADDER){
            List<String> info = Arrays.asList(data.getStringExtra("city").split(","));

            CityInfo newCity = new CityInfo();
            newCity.name = info.get(0);
            newCity.code = info.get(1);
            cities.add(newCity);

            ModelUtils.save(this, MODEL_CITY_INFO, cities);
            setupCityUI();
        }
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

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
        for(CityInfo c: cities){
            View cityView = getLayoutInflater().inflate(R.layout.city_item, null);
            ((Button) cityView.findViewById(R.id.city_item)).setText(c.name + "," + c.code);
            cityLayout.addView(cityView);
        }

    }

    private void loadData() {
        List<CityInfo> saveCityInfo = ModelUtils.read(this,
                MODEL_CITY_INFO,
                new TypeToken<List<CityInfo>>(){});
        cities = saveCityInfo == null ? new ArrayList<CityInfo>() : saveCityInfo;
    }
}

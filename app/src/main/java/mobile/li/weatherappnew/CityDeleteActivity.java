package mobile.li.weatherappnew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class CityDeleteActivity extends AppCompatActivity {

    public static final String DELETE_CITY = "delete_city";
    public static final String DELETE_CITY_LAT = "delete_city_lat";
    public static final String DELETE_CITY_LON = "delete_city_lon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_delete);

        findViewById(R.id.city_delete_delButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                List<String> deleteInfo = Arrays.asList(getIntent().getStringExtra(DELETE_CITY).split(","));

                String lat = deleteInfo.get(0).trim();
                String lon = deleteInfo.get(1).trim();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(DELETE_CITY_LAT, lat);
                resultIntent.putExtra(DELETE_CITY_LON, lon);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }
}

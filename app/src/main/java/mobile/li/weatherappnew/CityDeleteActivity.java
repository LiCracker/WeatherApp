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
    public static final String DELETE_CITY_NAME = "delete_city_name";
    public static final String DELETE_CITY_CODE = "delete_city_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_delete);

        findViewById(R.id.city_delete_delButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                List<String> deleteInfo = Arrays.asList(getIntent().getStringExtra(DELETE_CITY).split(","));

                String name = deleteInfo.get(0).trim();
                String code = deleteInfo.get(1).trim();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(DELETE_CITY_NAME, name);
                resultIntent.putExtra(DELETE_CITY_CODE, code);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }
}

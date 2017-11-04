package mobile.li.weatherappnew;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import mobile.li.weatherappnew.model.ValidCountryCode;

public class CityAdderActivity extends AppCompatActivity {

    private String name = "";
    private String code = "";

    public static final String KEY_CITY = "city";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_adder);

        findViewById(R.id.city_adder_addButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveAndExit();
                ValidCountryCode valid = new ValidCountryCode();
                Set<String> set = new HashSet<>();
                valid.getCode(set);
                if(name == " " || code == " " ) {
                    Toast.makeText(CityAdderActivity.this, "Please fill all the blank", Toast.LENGTH_LONG).show();
                }else if(!set.contains(code)) {
                    Toast.makeText(CityAdderActivity.this, "Please enter the valid country code. e.g. USA = US", Toast.LENGTH_LONG).show();
                }else{
                        String result = name + "," + code;
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY_CITY, result);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                }
            }
        });

    }



    private void saveAndExit(){


        name = ((EditText) findViewById(R.id.city_adder_name)).getText().toString().toUpperCase();

        code = ((EditText) findViewById(R.id.city_adder_code)).getText().toString().toUpperCase();

        if(name == null || name.trim().equals("")){
            name = " ";
        }

        if(code == null || code.trim().equals("")){
            code = " ";
        }

    }
}

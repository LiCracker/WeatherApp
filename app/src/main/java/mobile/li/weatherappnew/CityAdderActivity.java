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
                if(name != " " && code != " ") {
                    String result = name + "," + code;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY_CITY, result);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }else{
                    Toast.makeText(CityAdderActivity.this, "Please fill all the blank", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    private void saveAndExit(){


        name = ((EditText) findViewById(R.id.city_adder_name)).getText().toString();

        code = ((EditText) findViewById(R.id.city_adder_code)).getText().toString();

        if(name == null || name.trim().equals("")){
            name = " ";
        }

        if(code == null || code.trim().equals("")){
            code = " ";
        }

    }
}

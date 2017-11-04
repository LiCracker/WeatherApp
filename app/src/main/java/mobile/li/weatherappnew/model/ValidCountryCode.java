package mobile.li.weatherappnew.model;

import android.text.TextUtils;

import java.util.Set;

/**
 * Created by JiaYi on 11/2/2017.
 */

public class ValidCountryCode {

    private static final String usCode = "US";
    private static final String chinaCode = "CN";

    public ValidCountryCode(){};

    public void getCode(Set<String> set){
        set.add(usCode);
        set.add(chinaCode);
    }
}

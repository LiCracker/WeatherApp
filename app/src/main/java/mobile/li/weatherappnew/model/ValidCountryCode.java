package mobile.li.weatherappnew.model;

import java.util.Set;

/**
 * Created by JiaYi on 11/2/2017.
 */

public class ValidCountryCode {

    private static final String usCode = "US";
    private static final String chinaCode = "CN";

    public ValidCountryCode(){};

    private Set<String> getCode(Set<String> set){
        set.add(usCode);
        set.add(chinaCode);

        return set;
    }
}

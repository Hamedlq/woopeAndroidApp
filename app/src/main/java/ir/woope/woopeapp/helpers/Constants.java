package ir.woope.woopeapp.helpers;

import static ir.woope.woopeapp.helpers.Constants.HTTP.BASE_URL;

/**
 * Created by alireza on 3/27/18.
 */

public final class Constants {
    public Constants() {
    }

    public final class HTTP {

        public HTTP() {

        }


        
//        public static final String BASE_URL = "http://localhost/";

        public static final String BASE_URL = "http://192.168.100.2:8090";

       // public static final String BASE_URL = "http://192.168.100.5:8090/";
         public static final String BASE_URL = "http://localhost:58795/";
//        public static final String BASE_URL = "http://test.mywoope.com/";
    }

    public final class GlobalConstants{

        public static final String MOBILE_NUMBER_TAG = "MOBILENUMBERTTAG";
        public static final String MY_SHARED_PREFERENCES = "ir.woope.woopeapp";
        public static final String TOKEN = "TOKEN";
        public static final String TOTAL_PRICE = "total_price";
        public static final String STORE_NAME = "StoreName";
        public static final String STORE = "StoreObj";
        public static final String BUY_AMOUNT = "BuyAmount";
        public static final String PAY_LIST_ITEM = "PayListItem";
        public static final String PROFILE = "profile";
        public static final String PREF_PROFILE = "preferences_profile";
        public static final String POINTS_PAYED = "pointsPaid";
        public static final String GET_PROFILE_FROM_SERVER = "getProfileFromServer";
        public static final String LOGO_URL = BASE_URL ;
        public static final int REQUEST_CAMERA =2356;
        public static final int SELECT_FILE =8596;
    }

    public final class Actions {
        public static final String PARAM_AUTHORIZATION = "Authorization";
        public static final String FOLLOW_STORE = "api/Store/FollowStore";
        public static final String GET_FOLLOW_STORE = "api/Store/GetFollowingStores";
    }
}

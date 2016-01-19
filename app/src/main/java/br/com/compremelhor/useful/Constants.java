package br.com.compremelhor.useful;

/**
 * Created by adriano on 22/08/15.
 *
 * @author : adriano.jesus282@gmail.com
 *
 */
public interface Constants {
    String PREFERENCES = "global_preferences";
    String KEEP_CONNECT_SP = "keep_connected";

    String ADDRESS_BUNDLE = "address_bundle";
    String ADDRESS_NAME = "address_name";
    String ZIPCODE = "zipcode";
    String STREET = "street";
    String NUMBER = "number";
    String QUARTER = "quarter";
    String CITY = "city";
    String STATE = "state";
    String ADDRESS_ID_EXTRA = "address_id";

    String USER_ID_SHARED_PREFERENCE = "user_id";
    String FACEBOOK_USER_ID_SP = "facebook_user_id";
    String LOGGED_ON_FACEBOOK_SP = "is_logged_on_facebook";

    String CLIENT_SCANNER = "com.google.zxing.client.android.SCAN";
    String SCAN_MODE = "SCAN_MODE";
    String QR_CODE_MODE = "QR_CODE_MODE";
    String PRODUCT_MODE = "PRODUCT_MODE";
    String OTHERS_CODES = "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF";

    int REQUEST_CODE_SCANNED_CODE = 10;
    int REQUEST_CODE_CART_ITEM_ADDED = 11;
}

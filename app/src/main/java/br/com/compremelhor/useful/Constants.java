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
    String PURCHASE_ID_EXTRA = "purchase_id";
    String CURRENT_QUANTITY_OF_ITEM_EXTRA = "current_quantity_of_item_selected";

    String SP_SELECTED_ADDRESS_ID = "selected_address_id_sp";
    String SP_USER_ID = "user_id";
    String SP_FACEBOOK_USER_ID = "facebook_user_id";
    String SP_LOGGED_ON_FACEBOOK = "is_logged_on_facebook";
    String SP_FREIGHT_VALUE = "sp_freight_value";

    String CLIENT_SCANNER = "com.google.zxing.client.android.SCAN";
    String SCAN_MODE = "SCAN_MODE";
    String QR_CODE_MODE = "QR_CODE_MODE";
    String PRODUCT_MODE = "PRODUCT_MODE";
    String OTHERS_CODES = "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF";

    // Constants for delegate tasks for Activities
    int REQUEST_CODE_SCANNED_CODE = 10;
    int REQUEST_CODE_CART_ITEM_ADDED = 11;
    int REQUEST_CODE_CART_ITEM_EDITED = 12;
    int REQUEST_CODE_ADDRESS_EDITED_OR_ADDED = 13;


    // Constants for MENU OPTIONS on Action Bar
    int MENU_OPTION_ID_MANAGE_ADDRESS = 1;
}

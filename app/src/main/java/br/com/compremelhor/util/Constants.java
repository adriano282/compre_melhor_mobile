package br.com.compremelhor.util;

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

    String ROOT_RESOURCE_STOCK = "stock";

    String EXTRA_CURRENT_QUANTITY_OF_ITEM = "current_quantity_of_item_selected";
    String EXTRA_PURCHASE_ID = "purchase_id";
    String EXTRA_CATEGORY_NAME = "category_name";
    String EXTRA_PARTNER_ID = "partner_id";
    String EXTRA_ADDRESS_ID = "address_id";
    String EXTRA_SER_PRODUCT = "product";

    String SP_SELECTED_ADDRESS_ID = "selected_address_id_sp";
    String SP_PARTNER_ID = "sp_partner_id";
    String SP_PARTNER_NAME = "sp_partner_name";
    String SP_USER_ID = "user_id";
    String SP_FACEBOOK_USER_ID = "facebook_user_id";
    String SP_LOGGED_ON_FACEBOOK = "is_logged_on_facebook";
    String SP_FREIGHT_VALUE = "sp_freight_value";
    String SP_VIEW_PURCHASE_BY_YEAR = "sp_view_purchase_by_year";


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
    int REQUEST_CODE_PAYMENT_PURCHASE_PAID = 14;
    int REQUEST_CODE_PURCHASE_FINISHED = 15;
    int REQUEST_CODE_PURCHASE_VIEWED = 16;
    int REQUEST_CODE_PURCHASE_LINE_VIEWED = 17;
    int REQUEST_CODE_UNVAILABLE_PRODUCTS_ISSUE = 18;


    // Constants for MENU OPTIONS on Action Bar
    int MENU_OPTION_ID_MANAGE_ADDRESS = 1;
    int MENU_OPTION_ID_LIST_BY_YEAR = 2;
    int MENU_OPTION_ID_LIST_BY_MONTH = 3;

    String CART_FRAGMENT = "cart_fragment";
    String FREIGHT_FRAGMENT = "freight_fragment";
    String PAYMENT_FRAGMENT = "payment_fragment";
}

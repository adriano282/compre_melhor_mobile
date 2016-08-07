package br.com.compremelhor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.service.CartService;
import br.com.compremelhor.util.helper.DatabaseHelper;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class ExpiredCartItemsActivity extends ActivityTemplate<PurchaseLine> {

    private List<Map<String, Object>> lines;

    private ListAdapter adapter;
    private ListView listView;

    private Button buttonClosePurchase;
    private Button buttonEditCart;

    private CartService cartService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(
                DAOPurchaseLine.getInstance(this), null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expired_cart_item);

        cartService = CartService.getInstance(this, preferences.getInt(SP_USER_ID, 0), preferences.getInt(SP_PARTNER_ID, 0));

        setToolbar();
        setWidgets();
        registerWidgets();
    }

    @Override
    protected void setWidgets() {
        lines = listLines();

        String[] from = {
                DatabaseHelper.PurchaseLine.PRODUCT_NAME,
                DatabaseHelper.PurchaseLine.QUANTITY
        };
        int [] to = {
                R.id.tv_expired_item_name,
                R.id.tv_expired_item_quantity
        };

        adapter = new SimpleAdapter(this, lines, R.layout.expired_cart_row, from, to);
        listView = (ListView) findViewById(R.id.expired_cart_items_view);
        listView.setAdapter(adapter);

        buttonEditCart = (Button) findViewById(R.id.button_edit_cart);
        buttonClosePurchase = (Button) findViewById(R.id.button_close_purchase);

        if (cartService.getItems().size() > 1) {
            buttonClosePurchase.setVisibility(View.VISIBLE); }
        else {
            buttonClosePurchase.setVisibility(View.GONE); }

    }

    private List<Map<String, Object>> listLines() {
        List<PurchaseLine> lines = cartService.getExpiredItems(false);

        List<Map<String, Object>> objects = new ArrayList<>();

        for (PurchaseLine line : lines) {
            if (line == null) continue;

            HashMap<String, Object> object = new HashMap<>();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       object.put(DatabaseHelper.PurchaseLine.PRODUCT_NAME, line.getProductName());
            object.put(DatabaseHelper.PurchaseLine.QUANTITY, line.getQuantity());
            String name = line.getProductName();

            name = name != null && name.length() > 30 ? name.substring(0, 30).concat("...") : name;
            object.put(DatabaseHelper.PurchaseLine.PRODUCT_NAME, name);
            objects.add(object);
        }
        return objects;
    }

    @Override
    protected void registerWidgets() {
        buttonClosePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        buttonEditCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void fillFields() {}
}

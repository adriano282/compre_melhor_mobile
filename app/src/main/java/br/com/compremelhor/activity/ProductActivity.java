package br.com.compremelhor.activity;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.resource.impl.StockResource;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.model.Stock;
import br.com.compremelhor.service.CartService;

import static br.com.compremelhor.util.Constants.EXTRA_CURRENT_QUANTITY_OF_ITEM;
import static br.com.compremelhor.util.Constants.EXTRA_SER_PRODUCT;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;
import static br.com.compremelhor.util.Constants.ROOT_RESOURCE_STOCK;

public class ProductActivity extends ActivityTemplate<Stock> {
    private ImageView ivProduct;

    private TextView tvName;
    private TextView tvManufacturer;
    private TextView tvUnit;
    private TextView tvPriceUnitary;
    private TextView tvCategory;
    private TextView tvSubtotal;

    private NumberPicker npQuantity;

    private Button btnChangeOnCart;
    private PurchaseLine item;
    private CartService cartService;

    private int itemId;

    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(R.id.my_toolbar,
                getSharedPreferences(PREFERENCES, MODE_PRIVATE),
                new Handler(),
                null, new StockResource(ROOT_RESOURCE_STOCK, this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        item = new PurchaseLine();
        int userId = preferences.getInt(SP_USER_ID, 0);
        int partnerId = preferences.getInt(SP_PARTNER_ID, 0);

        cartService = CartService.getInstance(this, userId, partnerId);
        setWidgets();
        registerWidgets();
        fillFields();
    }

    public void onClickChangeOnCart(View view) {
        item.setQuantity(new BigDecimal(npQuantity.getValue()));
        item.setSubTotal(item.getQuantity().multiply(item.getProduct().getPriceUnitary()));

        item.setDateCreated(Calendar.getInstance());

        item.setLastUpdated(Calendar.getInstance());
        item.setUnitaryPrice(item.getProduct().getPriceUnitary());
        item.setCategory(item.getProduct().getCategory().getName());
        item.setId(itemId);
        item.setProductName(item.getProduct().getName());

        AsyncTask<Void, Void, Void> request = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HashMap<String, String> paramsStock = new HashMap<>();
                paramsStock.put("skuPartner.sku.id", String.valueOf(item.getProduct().getId()));
                paramsStock.put("skuPartner.partner.id", String.valueOf(preferences.getInt(SP_PARTNER_ID, 0)));
                Stock stock = resource.getResource(paramsStock);
                item.setStock(stock);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (btnChangeOnCart.getText().toString().equals(getString(R.string.button_text_change_on_cart))) {
                            if (!cartService.editItem(item)) showMessage(R.string.err_while_trying_edit_item_on_cart);
                        }
                        else if (cartService.getItems().contains(item))
                            showMessage(R.string.err_item_already_on_cart);
                        else if (!cartService.addItem(item))
                            showMessage(R.string.err_while_trying_add_item_on_cart);
                    }
                });

                ProductActivity.this.setResult(RESULT_OK);
                finish();
                return null;
            }
        };
        request.execute();
    }

    protected void fillFields() {
        Product product = (Product) getIntent().getSerializableExtra(EXTRA_SER_PRODUCT);

        item.setProduct(product);
        tvCategory.setText(product.getCategory().getName());
        tvManufacturer.setText(product.getManufacturer().getCompanyName());
        tvName.setText(product.getName());
        tvPriceUnitary.setText(String.format("R$ %,.2f", product.getPriceUnitary()));
        tvUnit.setText(product.getUnit().name());
        ivProduct.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

    }

    protected void setWidgets() {
        ivProduct = (ImageView) findViewById(R.id.iv_product);
        tvCategory = (TextView) findViewById(R.id.tv_product_category);
        tvManufacturer = (TextView) findViewById(R.id.tv_product_manufacturer);
        tvName = (TextView) findViewById(R.id.tv_product_name);
        tvPriceUnitary = (TextView) findViewById(R.id.tv_product_price_unitary);
        tvSubtotal = (TextView) findViewById(R.id.tv_sub_total_product);
        tvUnit = (TextView) findViewById(R.id.tv_product_unit);

        btnChangeOnCart = (Button) findViewById(R.id.btn_put_on_cart);

        String stringId = getIntent().getStringExtra(EXTRA_PURCHASE_ID);

        itemId = stringId == null || stringId.isEmpty() ? 0 : Integer.valueOf(stringId.trim());

        if (itemId != 0)
            btnChangeOnCart.setText(getString(R.string.button_text_change_on_cart));

        String quantity = getIntent().getStringExtra(EXTRA_CURRENT_QUANTITY_OF_ITEM);
        npQuantity = (NumberPicker) findViewById(R.id.np_quantity);
        npQuantity.setMinValue(0);
        npQuantity.setMaxValue(20);
        npQuantity.setValue(quantity == null || quantity.isEmpty() ? 0 : Integer.valueOf(quantity));
    }

    protected void registerWidgets() {
        npQuantity.setOnValueChangedListener(new OnValueChangeListener());
    }

    private class OnValueChangeListener implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            BigDecimal bdNewValue = new BigDecimal(newVal);

            if (item == null || item.getProduct() == null)
                return;

            BigDecimal newSubTotal = item.getProduct().getPriceUnitary().multiply(bdNewValue);
            newSubTotal.setScale(2);
            tvSubtotal.setText(String.format("R$ %,.2f",newSubTotal));
        }
    }
}

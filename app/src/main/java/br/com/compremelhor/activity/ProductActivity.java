package br.com.compremelhor.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.compremelhor.R;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.service.CartService;

import static br.com.compremelhor.util.Constants.CURRENT_QUANTITY_OF_ITEM_EXTRA;
import static br.com.compremelhor.util.Constants.PURCHASE_ID_EXTRA;

public class ProductActivity extends AppCompatActivity {
    private ImageView ivProduct;

    private TextView tvName;
    private TextView tvManufacturer;
    private TextView tvUnit;
    private TextView tvPriceUnitary;
    private TextView tvCategory;
    private TextView tvSubtotal;

    private NumberPicker npQuantity;

    private Button btnPutInCart;
    private PurchaseLine item;
    private CartService cartService;
    private int itemId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        item = new PurchaseLine();
        cartService = CartService.getInstance(this);
        setToolbar();
        setViews();
        registerListeners();
        fillViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Here we would open up our settings activity
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickPutOnCart(View view) {
        item.setQuantity(new BigDecimal(npQuantity.getValue()));
        item.setSubTotal(item.getQuantity().multiply(item.getProduct().getPriceUnitary()));

        item.setDateCreated(Calendar.getInstance());

        item.setLastUpdated(Calendar.getInstance());
        item.setUnitaryPrice(item.getProduct().getPriceUnitary());
        item.setCategory(item.getProduct().getCategory().getName());
        item.setId(Integer.valueOf(itemId));
        item.setProductName(item.getProduct().getName());

        cartService.addItem(item);
        this.setResult(RESULT_OK);
        finish();
    }

    private void fillViews() {
        Product product = (Product) getIntent().getSerializableExtra("product");

        item.setProduct(product);
        tvCategory.setText(product.getCategory().getName());
        tvManufacturer.setText(product.getManufacturer().getCompanyName());
        tvName.setText(product.getName());
        tvPriceUnitary.setText("R$ " + product.getPriceUnitary());
        tvUnit.setText(product.getUnit().name());
        ivProduct.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

    }

    private void setViews() {
        ivProduct = (ImageView) findViewById(R.id.iv_product);
        tvCategory = (TextView) findViewById(R.id.tv_product_category);
        tvManufacturer = (TextView) findViewById(R.id.tv_product_manufacturer);
        tvName = (TextView) findViewById(R.id.tv_product_name);
        tvPriceUnitary = (TextView) findViewById(R.id.tv_product_price_unitary);
        tvSubtotal = (TextView) findViewById(R.id.tv_sub_total_product);
        tvUnit = (TextView) findViewById(R.id.tv_product_unit);

        btnPutInCart = (Button) findViewById(R.id.btn_put_on_cart);

        String stringId = getIntent().getStringExtra(PURCHASE_ID_EXTRA);

        itemId = stringId == null || stringId.isEmpty() ? 0 : Integer.valueOf(stringId);

        if (itemId != 0)
            btnPutInCart.setText("ALTERAR QUANTIDADE");

        String quantity = getIntent().getStringExtra(CURRENT_QUANTITY_OF_ITEM_EXTRA);
        npQuantity = (NumberPicker) findViewById(R.id.np_quantity);
        npQuantity.setMinValue(0);
        npQuantity.setMaxValue(20);
        npQuantity.setValue(quantity == null || quantity.isEmpty() ? 0 : Integer.valueOf(quantity));


    }

    private void registerListeners() {
        npQuantity.setOnValueChangedListener(new OnValueChangeListener());
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private class OnValueChangeListener implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            BigDecimal bdNewValue = new BigDecimal(newVal);

            if (item == null || item.getProduct() == null)
                return;

            BigDecimal newSubTotal = item.getProduct().getPriceUnitary().multiply(bdNewValue);
            newSubTotal.setScale(2);
            tvSubtotal.setText("RS " + newSubTotal);
        }
    }
}

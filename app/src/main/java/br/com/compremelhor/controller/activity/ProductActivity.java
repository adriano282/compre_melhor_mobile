package br.com.compremelhor.controller.activity;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOCart;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.Code;
import br.com.compremelhor.model.Manufacturer;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;

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

    private static Map<String, Product> productsForTest;
    private static void startMap() {
        productsForTest = new HashMap<>();

        Product p1 = new Product();
        p1.setId(new Long(1));
        p1.setName("Maionese");
        p1.setDescription("Maionese");
        p1.setPriceUnitary(new BigDecimal(4.50));

        Manufacturer hellmans = new Manufacturer();
        hellmans.setCompanyName("Hellmans");
        p1.setManufacturer(hellmans);

        Code code = new Code();
        code.setCode("123444444444");
        code.setType(Code.CodeType.BAR_CODE);

        p1.setCode(code);

        Category category1 = new Category();
        category1.setName("Gelados");
        p1.setCategory(category1);

        p1.setUnit(Product.Unit.UNIT);

        productsForTest.put(p1.getCode().getCode(), p1);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        startMap();
        item = new PurchaseLine();
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
        item.setId(new Long(1));

        DAOCart dao = new DAOCart(this);

        Cart c = new Cart();
        c.setItens(new ArrayList<>(Arrays.asList(item)));
        c.setId(new Long(1));

        dao.insertOrUpdate(c);

        this.setResult(RESULT_OK);
        finish();

    }

    private void fillViews() {
        String code = getIntent().getStringExtra("codeResult");
        // Here will request the product data from webservice
        Product product = getProductOnWebServer("123444444444");
        item.setProduct(product);

        tvCategory.setText(product.getCategory().getName());
        tvManufacturer.setText(product.getManufacturer().getCompanyName());
        tvName.setText(product.getName());
        tvPriceUnitary.setText("R$ " + product.getPriceUnitary());
        tvUnit.setText(product.getUnit().name());
        ivProduct.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

    }

    private Product getProductOnWebServer(String code) {
        return productsForTest.get(code);
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
        npQuantity = (NumberPicker) findViewById(R.id.np_quantity);
        npQuantity.setValue(20);
        npQuantity.setMinValue(0);
        npQuantity.setMaxValue(20);
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

package br.com.compremelhor.controller.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.controller.adapter.ExpandableListAdapter;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.Category;
import br.com.compremelhor.model.Code;
import br.com.compremelhor.model.Manufacturer;
import br.com.compremelhor.model.Product;
import br.com.compremelhor.model.PurchaseLine;

import static br.com.compremelhor.useful.Constants.CLIENT_SCANNER;
import static br.com.compremelhor.useful.Constants.OTHERS_CODES;
import static br.com.compremelhor.useful.Constants.PRODUCT_MODE;
import static br.com.compremelhor.useful.Constants.QR_CODE_MODE;
import static br.com.compremelhor.useful.Constants.SCAN_MODE;

public class CartFragment extends android.support.v4.app.Fragment {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    
    private AlertDialog actionsProduct;
    private AlertDialog alertDialogConfirmation;

    private Button btnAddProduct;

    private OptionsDialogOnClickListener optionsListener;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        prepareListData();

    }

    @Override
    public void onStart() {
        super.onStart();
        setWidgets();
        registerViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != 0)
            return;

        if (resultCode == Activity.RESULT_OK) {
            Log.v("SCRIP", intent.getStringExtra("SCAN_RESULT_FORMAT"));
            Log.v("SCRIP", intent.getStringExtra("SCAN_RESULT"));
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.v("SCRIP", "Press a button to start a scan.");
            Log.v("SCRIP", "Scan cancelled");
        }
    }


    private void registerViews() {
        optionsListener = new OptionsDialogOnClickListener();
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createReadingAlertDialog().show();
            }
        });

        explicitView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                actionsProduct.show();
                return false;
            }
        });
    }

    private void setWidgets() {
        explicitView = (ExpandableListView) getView().findViewById(R.id.lv_shopping_list);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        explicitView.setAdapter(listAdapter);

        btnAddProduct = (Button) getActivity().findViewById(R.id.btn_shopping_list_add);
        actionsProduct = createProductAlertDialog();
        alertDialogConfirmation = createDialogConfirmation();

    }

    private AlertDialog createReadingAlertDialog() {
        CharSequence[] items;

        items = new CharSequence[] {
                getString(R.string.QR_CODE),
                getString(R.string.BARCODE),
                getString(R.string.OTHER)
        };

        return new Builder(getActivity())
                .setTitle(getString(R.string.read_type))
                .setItems(items, new ScannerOnClickListener())
                .create();
    }



    private AlertDialog createDialogConfirmation() {
        return new Builder(getActivity())
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, optionsListener)
                .setNegativeButton(R.string.no, optionsListener)
                .create();
    }

    private AlertDialog createProductAlertDialog() {
        final CharSequence[] items;

        items = new CharSequence[] {
                getString(R.string.alter_quantity),
                getString(R.string.remove_product)
        };

        return new Builder(getActivity())
                .setTitle(getString(R.string.options))
                .setItems(items, optionsListener)
                .create();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        Category meatCategory = new Category();



        meatCategory.setName("Carnes");
        Manufacturer m1 = new Manufacturer();
        m1.setCompanyName("SEARA");

        Category c2 = new Category();
        c2.setName("Bebidas");

        Category c3 = new Category();
        c3.setName("Guloseimas");

        Product p1 = new Product();
        p1.setId(new Long(1));
        p1.setName("Passarinho");
        p1.setCategory(meatCategory);
        Code code1 = new Code();
        code1.setCode("123456");
        code1.setType(Code.CodeType.BAR_CODE);
        p1.setCode(code1);
        p1.setDescription("Carne de Passarinho Sabia");
        p1.setManufacturer(m1);
        p1.setManufacturer(m1);
        p1.setUnit(Product.Unit.KILO);
        p1.setPriceUnitary(new BigDecimal(10.00));

        PurchaseLine item = new PurchaseLine();
        item.setProduct(p1);
        item.setQuantity(new BigDecimal(1.5));
        item.setSubTotal(p1.getPriceUnitary().multiply(item.getQuantity()));

        Cart cart = new Cart();
        cart.setItens(new ArrayList<>(Arrays.asList(item)));
        cart.setId(Long.valueOf(1));


        listDataHeader.add(meatCategory.getName() + "/ R$ " + (item.getSubTotal()));

        List<String> carnes = new ArrayList<>();
        carnes.add(formattedItem(item));

        listDataChild.put(listDataHeader.get(0), carnes);
    }

    private String formattedItem(PurchaseLine pl) {
        return pl.getProduct().getName() + " / " + pl.getQuantity() + " / " + pl.getSubTotal();
    }

    private class OptionsDialogOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
            Intent intent;

            switch (item) {
                case 0:
                    // Here, implement change quantity product
                    break;

                case 1:
                    alertDialogConfirmation.show();
                    break;

                case DialogInterface.BUTTON_POSITIVE:
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }

    private class ScannerOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            Intent intent = new Intent(CLIENT_SCANNER);
            switch(item) {
                case 0:
                    intent.putExtra(SCAN_MODE, QR_CODE_MODE);
                    break;
                case 1:
                    intent.putExtra(SCAN_MODE, PRODUCT_MODE);
                    break;
                case 2:
                    intent.putExtra(SCAN_MODE, OTHERS_CODES);
                    break;
            }
            startActivityForResult(intent, 0);
        }
    }
}

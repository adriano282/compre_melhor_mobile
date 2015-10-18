package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.controller.adapter.ExpandableListAdapter;

/**
 * Created by adriano on 16/10/15.
 */
public class ShoppingListActivity extends Activity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explictView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private int productSelected;

    private Button btnAddProduct;
    private Button btnFinishPurchase;

    private AlertDialog alertDialog;
    private AlertDialog alertDialogConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        explictView = (ExpandableListView) findViewById(R.id.lv_shopping_list);

        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        explictView.setAdapter(listAdapter);

        setWidgets();
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }


    private void setWidgets() {
        btnAddProduct = (Button) findViewById(R.id.btn_shopping_list_add);
        btnFinishPurchase = (Button) findViewById(R.id.btn_shoppint_list_finish);

        explictView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                showMessage(listDataHeader.get(groupPosition)
                        + ":"
                        + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));

                productSelected = childPosition;
                alertDialog.show();
                return false;
            }
        });

        alertDialog = createAlertDialog();
        alertDialogConfirmation = createDialogConfirmation();
    }


    private AlertDialog createDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete);
        builder.setPositiveButton(getString(R.string.yes), this);
        builder.setNegativeButton(getString(R.string.no), this);
        return builder.create();
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("CATEGORIA CARNES    R$ 30,00");
        listDataHeader.add("CATEGORIA BEBIDAS   R$ 30,00");
        listDataHeader.add("CATEGORIA GULOSEIMAS    R$ 30,00");

        List<String> carnes = new ArrayList<String>();
        carnes.add("Passarinho          R$ 10,00");
        carnes.add("Frango              R$ 10,00");
        carnes.add("hamburguer          R$ 10,00");


        List<String> drinks = new ArrayList<String>();
        drinks.add("Coca-cola           R$ 10,00");
        drinks.add("Dell-Vale           R$ 10,00");
        drinks.add("Guarana Antertica   R$ 10,00");

        List<String> sweets = new ArrayList<String>();
        sweets.add("passa-tempo         R$ 10,00");
        sweets.add("chocolate nestle    R$ 10,00");
        sweets.add("sorvete             R$ 10,00");

        listDataChild.put(listDataHeader.get(0), carnes);
        listDataChild.put(listDataHeader.get(1), drinks);
        listDataChild.put(listDataHeader.get(2), sweets);
    }


    private AlertDialog createAlertDialog() {
        final CharSequence[] items;

        items = new CharSequence[] {
                getString(R.string.alter_quantity),
                getString(R.string.remove_product),
                getString(R.string.add_product)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.options));
        builder.setItems(items, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;

        switch (item) {
            case 0:
                showMessage("Alterar quantidade");
                break;

            case 1:
                alertDialogConfirmation.show();
                break;

            case 2:
                showMessage("Adicionar Produto");
                break;

            case DialogInterface.BUTTON_POSITIVE:
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                alertDialogConfirmation.dismiss();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.productSelected = position;
        alertDialog.show();
    }
}
